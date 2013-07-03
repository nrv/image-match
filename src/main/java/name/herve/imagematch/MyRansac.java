package name.herve.imagematch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class MyRansac {
	public final static int MIN_MATCHES = 2;
	public final static int ITERATIONS = 1000;

	private MyModel model;
	private List<MyPointMatch> inliers;

	public void estimateModel(List<MyPointMatch> matches, float epsilon, float minInlierRatio) {
		if (matches.size() < MIN_MATCHES) {
			return;
		}

		inliers = new ArrayList<MyPointMatch>();
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
			//System.out.println("["+i+"] tempInliers - " + tempInliers.size());
			while (isGood && numInliers < tempInliers.size()) {
				numInliers = tempInliers.size();
				tempModel.minimize(tempInliers);
				isGood = tempModel.testAndKeepGoodMatches(matches, tempInliers, epsilon, minInlierRatio);
				//System.out.println("["+i+"] tempInliers - " + tempInliers.size());
			}

			if (isGood && tempModel.betterThan(model) && tempInliers.size() >= 3 * MIN_MATCHES) {
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
