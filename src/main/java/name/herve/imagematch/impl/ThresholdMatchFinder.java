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
import plugins.nherve.toolbox.image.feature.signature.SignatureException;

/**
 * @author Nicolas HERVE - n.herve@laposte.net
 */
public class ThresholdMatchFinder extends PointMatchFinder {
	public ThresholdMatchFinder() {
		super();
		setParameter(T_P, T_V);
	}

	public final static String T_P = "T";
	public final static double T_V = 0.1;

	public class DistanceCompute extends MultipleDataTask<MyFeature, Boolean> {
		public DistanceCompute(List<MyFeature> allData, int idx1, int idx2) {
			super(allData, idx1, idx2);
		}

		@Override
		public void call(MyFeature ip1, int idx) throws Exception {
			double t = (Double) getParameter(T_P);
			for (int i = 0; i < getP2().size(); i++) {
				double d = getDistance().computeDistance(ip1, getP2().get(i));
				if (d <= t) {
					matches.add(new MyPointMatch(ip1, getP2().get(i), d));
				}
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

	private List<MyPointMatch> matches;
	
	@Override
	public List<MyPointMatch> work() throws SignatureException {
		matches = Collections.synchronizedList(new ArrayList<MyPointMatch>());
		
		TaskManager mgr = new TaskManager();
		mgr.setShowProgress(false);

		try {
			mgr.submitMultiForAll(getP1(), DistanceCompute.class, this, "compute distances", 1000);
		} catch (TaskException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		mgr.shutdown();

		

		return matches;
	}

}
