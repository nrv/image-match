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
package name.herve.imagematch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import plugins.nherve.toolbox.Algorithm;

/**
 * @author Nicolas HERVE - n.herve@laposte.net
 */
public class MyRansac extends Algorithm {
	public final static int MIN_MATCHES = 2;

	public final static int ITERATIONS = 1000;
	private MyModel model;

	private List<MyPointMatch> inliers;

	public MyRansac() {
		super(true);
	}

	public void estimateModel(List<MyPointMatch> matches, float epsilon, float minInlierRatio) {
		inliers = new ArrayList<MyPointMatch>();
		
		if (matches.size() < MIN_MATCHES) {
			return;
		}
		
		model = new MyModel();

		Random rd = new Random(System.currentTimeMillis());

		for (int i = 0; i < ITERATIONS; i++) {
			Set<MyPointMatch> randomMatches = new HashSet<MyPointMatch>();
			do {
				randomMatches.add(matches.get(rd.nextInt(matches.size())));
			} while (randomMatches.size() < MIN_MATCHES);

			MyModel tempModel = new MyModel();
			ArrayList<MyPointMatch> tempInliers = new ArrayList<MyPointMatch>();
			tempModel.estimateOnTwoMatches(randomMatches);

			int numInliers = 0;
			boolean isGood = tempModel.testAndKeepGoodMatches(matches, tempInliers, epsilon, minInlierRatio);
			while (isGood && numInliers < tempInliers.size()) {
				numInliers = tempInliers.size();
				tempModel.minimize(tempInliers);
				isGood = tempModel.testAndKeepGoodMatches(matches, tempInliers, epsilon, minInlierRatio);
			}

			if (isGood && tempModel.betterThan(model) && tempInliers.size() >= 3 /*3 * MIN_MATCHES*/) {
				model = tempModel.clone();
				inliers.clear();
				inliers.addAll(tempInliers);
			}
		}

		if (inliers.size() == 0) {
			model = null;
		}
	}

	public List<MyPointMatch> getInliers() {
		return inliers;
	}

	public MyModel getModel() {
		return model;
	}
}
