/*
 * Copyright 2013 Nicolas HERVE.
 * 
 * This file is part of image-match
 * 
 * image-match is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * image-match is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with image-match. If not, see <http://www.gnu.org/licenses/>.
 */
package name.herve.imagematch.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import plugins.nherve.toolbox.concurrent.MultipleDataTask;
import plugins.nherve.toolbox.concurrent.TaskException;
import plugins.nherve.toolbox.concurrent.TaskManager;
import plugins.nherve.toolbox.image.feature.SignatureDistance;
import plugins.nherve.toolbox.image.feature.signature.DenseVectorSignature;
import plugins.nherve.toolbox.image.feature.signature.L2Distance;
import plugins.nherve.toolbox.image.feature.signature.SignatureException;
import plugins.nherve.toolbox.image.feature.signature.VectorSignature;

/**
 * @author Nicolas HERVE - n.herve@laposte.net
 */
public class MyMatchFinder {
	public class MDT extends MultipleDataTask<MyFeature, Boolean> {
		public MDT(List<MyFeature> allData, int idx1, int idx2) {
			super(allData, idx1, idx2);
		}

		@Override
		public void call(MyFeature ip1, int idx) throws Exception {
			VectorSignature s1 = new DenseVectorSignature(ip1.getDesc());
			MyPointMatch firstBest = null;
			MyPointMatch secondBest = null;
			for (MyFeature ip2 : p2) {
				VectorSignature s2 = new DenseVectorSignature(ip2.getDesc());
				double d = distance.computeDistance(s1, s2);

				if (firstBest == null) {
					firstBest = new MyPointMatch(ip1, ip2, d);
				} else if (secondBest == null) {
					if (d < firstBest.getFeatureDistance()) {
						secondBest = firstBest;
						firstBest = new MyPointMatch(ip1, ip2, d);
					} else {
						secondBest = new MyPointMatch(ip1, ip2, d);
					}
				} else {
					if (d < firstBest.getFeatureDistance()) {
						secondBest = firstBest;
						firstBest = new MyPointMatch(ip1, ip2, d);
					} else if (d < secondBest.getFeatureDistance()) {
						secondBest = new MyPointMatch(ip1, ip2, d);
					}
				}
			}

			if ((firstBest != null) && (secondBest != null) && (firstBest.getFeatureDistance() < (distThresh * secondBest.getFeatureDistance()))) {
				firstBest.setGroup(0);
				matches.add(firstBest);
			}

		}

		@Override
		public Boolean outputCall() throws Exception {
			return true;
		}

		@Override
		public void processContextualData() {
		}
	}

	public final static double DIST_THRESH = 0.7;

	private final List<MyFeature> p1;
	private final List<MyFeature> p2;
	private List<MyPointMatch> matches;
	private SignatureDistance<VectorSignature> distance;
	private double distThresh;

	public MyMatchFinder(List<MyFeature> p1, List<MyFeature> p2) {
		super();

		matches = Collections.synchronizedList(new ArrayList<MyPointMatch>());

		setDistance(new L2Distance());
		setDistThresh(DIST_THRESH);

		this.p1 = p1;
		this.p2 = p2;
	}

	public void setDistance(SignatureDistance<VectorSignature> distance) {
		this.distance = distance;
	}

	public void setDistThresh(double distThresh) {
		this.distThresh = distThresh;
	}

	public List<MyPointMatch> work() throws SignatureException {
		TaskManager mgr = new TaskManager();
		mgr.setShowProgress(false);

		try {
			mgr.submitMultiForAll(p1, MDT.class, this, "finding matches", 1000);
		} catch (TaskException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		mgr.shutdown();

		return matches;
	}
}
