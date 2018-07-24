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

import name.herve.imagematch.PointMatchFinder;
import plugins.nherve.toolbox.concurrent.MultipleDataTask;
import plugins.nherve.toolbox.concurrent.TaskException;
import plugins.nherve.toolbox.concurrent.TaskManager;
import plugins.nherve.toolbox.image.feature.signature.L2Distance;
import plugins.nherve.toolbox.image.feature.signature.SignatureException;

/**
 * @author Nicolas HERVE - n.herve@laposte.net
 */
public class ThresholdSecondBestMatchFinder extends PointMatchFinder {
	public class MDT extends MultipleDataTask<MyFeature, Boolean> {
		public MDT(List<MyFeature> allData, int idx1, int idx2) {
			super(allData, idx1, idx2);
		}

		@Override
		public void call(MyFeature ip1, int idx) throws Exception {
			MyPointMatch firstBest = null;
			MyPointMatch secondBest = null;
			for (MyFeature ip2 : getP2()) {
				double d = getDistance().computeDistance(ip1, ip2);

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

			if ((firstBest != null) && (secondBest != null) && (firstBest.getFeatureDistance() < ((Double)(getParameter(DIST_THRESH_P)) * secondBest.getFeatureDistance()))) {
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

	public final static String DIST_THRESH_P = "DIST_THRESH";
	public final static double DIST_THRESH_V = 0.7;

	private List<MyPointMatch> matches;

	public ThresholdSecondBestMatchFinder() {
		super();

		setDistance(new L2Distance());
		setParameter(DIST_THRESH_P, DIST_THRESH_V);
	}

	@Override
	public List<MyPointMatch> work() throws SignatureException {
		matches = Collections.synchronizedList(new ArrayList<MyPointMatch>());

		TaskManager mgr = new TaskManager();
		mgr.setShowProgress(false);

		try {
			mgr.submitMultiForAll(getP1(), MDT.class, this, "finding matches", 100);
		} catch (TaskException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		mgr.shutdown();

		return matches;
	}
}
