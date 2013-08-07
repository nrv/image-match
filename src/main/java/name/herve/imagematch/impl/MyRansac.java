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
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import plugins.nherve.toolbox.Algorithm;

/**
 * @author Nicolas HERVE - n.herve@laposte.net
 */
public class MyRansac extends Algorithm {
	public final static int MIN_MATCHES = 3;
	public final static int ITERATIONS = 1000;
	public final static float EPSILON = 200f;
	public final static float MIN_INLIER_RATIO = 0.15f;

	private MyModel model;
	private List<MyPointMatch> inliers;
	private int minMatches;
	private int iterations;
	private float epsilon;
	private float minInlierRatio;

	public MyRansac() {
		super(true);
		setMinMatches(MIN_MATCHES);
		setIterations(ITERATIONS);
		setEpsilon(EPSILON);
		setMinInlierRatio(MIN_INLIER_RATIO);
	}

	public void estimateModel(List<MyPointMatch> matches) {
		inliers = new ArrayList<MyPointMatch>();

		if (matches.size() < minMatches) {
			return;
		}

		model = new MyModel();

		Random rd = new Random(System.currentTimeMillis());

		for (int i = 0; i < iterations; i++) {
			Set<MyPointMatch> randomMatches = new HashSet<MyPointMatch>();
			do {
				randomMatches.add(matches.get(rd.nextInt(matches.size())));
			} while (randomMatches.size() < 2);

			MyModel tempModel = new MyModel();
			ArrayList<MyPointMatch> tempInliers = new ArrayList<MyPointMatch>();
			tempModel.estimateOnTwoMatches(randomMatches);

			int numInliers = 0;
			boolean isGood = tempModel.testAndKeepGoodMatches(matches, tempInliers, epsilon, minInlierRatio);
			while (isGood && (numInliers < tempInliers.size())) {
				numInliers = tempInliers.size();
				tempModel.minimize(tempInliers);
				isGood = tempModel.testAndKeepGoodMatches(matches, tempInliers, epsilon, minInlierRatio);
			}

			if (isGood && tempModel.betterThan(model) && (tempInliers.size() >= minMatches + 1)) {
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

	public void setEpsilon(float epsilon) {
		this.epsilon = epsilon;
	}

	public void setIterations(int iterations) {
		this.iterations = iterations;
	}

	public void setMinInlierRatio(float minInlierRatio) {
		this.minInlierRatio = minInlierRatio;
	}

	public void setMinMatches(int minMatches) {
		this.minMatches = minMatches;
	}
}
