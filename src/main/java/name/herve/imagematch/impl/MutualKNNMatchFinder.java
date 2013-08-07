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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import name.herve.imagematch.PointMatchFinder;
import plugins.nherve.toolbox.concurrent.MultipleDataTask;
import plugins.nherve.toolbox.concurrent.TaskException;
import plugins.nherve.toolbox.concurrent.TaskManager;
import plugins.nherve.toolbox.image.feature.signature.DenseVectorSignature;
import plugins.nherve.toolbox.image.feature.signature.SignatureException;
import plugins.nherve.toolbox.image.feature.signature.VectorSignature;

/**
 * @author Nicolas HERVE - n.herve@laposte.net
 */
public class MutualKNNMatchFinder extends PointMatchFinder {
	public MutualKNNMatchFinder() {
		super();
		setParameter(K_P, K_V);
	}

	public final static String K_P = "K";
	public final static int K_V = 3;

	private double[][] dm;
	private Set[] p1n;
	private Set[] p2n;

	public class DistanceCompute extends MultipleDataTask<MyFeature, Boolean> {
		public DistanceCompute(List<MyFeature> allData, int idx1, int idx2) {
			super(allData, idx1, idx2);
		}

		@Override
		public void call(MyFeature ip1, int idx) throws Exception {
			VectorSignature s1 = new DenseVectorSignature(ip1.getDesc());
			for (int i = 0; i < getP2().size(); i++) {
				VectorSignature s2 = new DenseVectorSignature(getP2().get(i).getDesc());
				dm[idx][i] = getDistance().computeDistance(s1, s2);
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

	public class KNNCompute extends MultipleDataTask<Boolean, Boolean> {

		public KNNCompute(List<Boolean> allData, int idx1, int idx2) {
			super(allData, idx1, idx2);
		}

		@Override
		public void call(Boolean workOnP1, int idx) throws Exception {
			List<MyPointMatch> l = new ArrayList<MyPointMatch>();
			if (workOnP1) {
				for (int i = 0; i < getP2().size(); i++) {
					l.add(new MyPointMatch(null, getP2().get(i), dm[idx][i]));
				}
			} else {
				for (int i = 0; i < getP1().size(); i++) {
					l.add(new MyPointMatch(getP1().get(i), null, dm[i][idx]));
				}
			}

			Collections.sort(l, new Comparator<MyPointMatch>() {

				@Override
				public int compare(MyPointMatch o1, MyPointMatch o2) {
					return (int) (Math.signum(o1.getFeatureDistance() - o2.getFeatureDistance()));
				}
			});

			Set<Integer> knn = new HashSet<Integer>();

			if (workOnP1) {
				for (int i = 0; i < (Integer) getParameter(K_P); i++) {
					knn.add(l.get(i).getId2());
				}
				p1n[idx] = knn;
			} else {
				for (int i = 0; i < (Integer) getParameter(K_P); i++) {
					knn.add(l.get(i).getId1());
				}
				p2n[idx] = knn;
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

	@Override
	public List<MyPointMatch> work() throws SignatureException {
		dm = new double[getP1().size()][getP2().size()];
		for (int i = 0; i < getP1().size(); i++) {
			Arrays.fill(dm[i], Double.MAX_VALUE);
		}

		List<MyPointMatch> matches = new ArrayList<MyPointMatch>();
		
		TaskManager mgr = new TaskManager();
		mgr.setShowProgress(false);

		try {
			mgr.submitMultiForAll(getP1(), DistanceCompute.class, this, "compute distances", 1000);

			Boolean[] p = new Boolean[getP1().size()];
			p1n = new Set[getP1().size()];
			Arrays.fill(p, true);
			mgr.submitMultiForAll(p, KNNCompute.class, this, "compute p1 knn", 1000);

			p = new Boolean[getP2().size()];
			p2n = new Set[getP2().size()];
			Arrays.fill(p, false);
			mgr.submitMultiForAll(p, KNNCompute.class, this, "compute p2 knn", 1000);
			
			for (int i1 = 0; i1 < getP1().size(); i1++) {
				for (int i2 : (Set<Integer>)p1n[i1]) {
					if (((Set<Integer>)p2n[i2]).contains(i1)) {
						MyPointMatch pm = new MyPointMatch(getP1().get(i1), getP2().get(i2), dm[i1][i2]);
						matches.add(pm);
					}
				}
			}

		} catch (TaskException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		mgr.shutdown();

		

		return matches;
	}

}
