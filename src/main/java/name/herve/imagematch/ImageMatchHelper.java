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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;

import name.herve.imagematch.impl.MyFeature;
import name.herve.imagematch.impl.MyMatchFinder;
import name.herve.imagematch.impl.MyPointMatch;
import name.herve.imagematch.impl.MyRansac;

import mpi.cbg.fly.Feature;
import mpi.cbg.fly.SIFT;
import plugins.nherve.toolbox.Algorithm;
import plugins.nherve.toolbox.image.feature.SignatureDistance;
import plugins.nherve.toolbox.image.feature.signature.L2Distance;
import plugins.nherve.toolbox.image.feature.signature.SignatureException;
import plugins.nherve.toolbox.image.feature.signature.VectorSignature;
import plugins.nherve.toolbox.image.toolboxes.ImageTools;

import com.stromberglabs.jopensurf.SURFInterestPoint;
import com.stromberglabs.jopensurf.Surf;

/**
 * @author Nicolas HERVE - n.herve@laposte.net
 */
public class ImageMatchHelper extends Algorithm {
	private final static int SURF_OCTAVES = 5; // max 5
	private final static float SURF_THRESHOLD = 0.00001F;
	private final static float SURF_BALANCE_VALUE = 0.9F;

	private static int SIFT_STEPS = 5;
	private static float SIFT_INITIAL_SIGMA = 1.6f;
	private static int SIFT_FDSIZE = 4;
	private static int SIFT_FDBINS = 8;
	private static int SIFT_MIN_SIZE = 32;
	private static int SIFT_MAX_SIZE = 2048;

	public ImageMatchHelper() {
		super(true);
	}

	public void drawMatches(BufferedImage img1, BufferedImage img2, List<MyPointMatch> matches, File f) throws IOException {
		int space = 25;
		int w = img1.getWidth() + space + img2.getWidth();
		int h = Math.max(img1.getHeight(), img2.getHeight());

		int xo1 = 0;
		int yo1 = (h - img1.getHeight()) / 2;
		int xo2 = img1.getWidth() + space;
		int yo2 = (h - img2.getHeight()) / 2;

		BufferedImage r = new BufferedImage(w, h, img1.getType());

		Graphics2D g2 = (Graphics2D) r.getGraphics();
		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

		AffineTransform t1 = new AffineTransform();
		t1.translate(xo1, yo1);
		g2.drawImage(img1, t1, null);

		AffineTransform t2 = new AffineTransform();
		t2.translate(xo2, yo2);
		g2.drawImage(img2, t2, null);

		g2.setColor(Color.GREEN);
		// g2.setStroke(new BasicStroke(2));
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		for (MyPointMatch pm : matches) {
			g2.drawLine((int) (pm.getP1().getX() + xo1), (int) (pm.getP1().getY() + yo1), (int) (pm.getP2().getX() + xo2), (int) (pm.getP2().getY() + yo2));
		}

		g2.dispose();

		dump(r, f);
	}

	public void drawPoints(BufferedImage img1, List<MyFeature> points) {
		Graphics2D g2 = (Graphics2D) img1.getGraphics();
		g2.setColor(Color.RED);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		for (MyFeature p : points) {
			g2.fillRect((int) p.getX() - 4, (int) p.getY() - 4, 9, 9);
		}
		g2.dispose();
	}

	public void dump(BufferedImage img, File f) throws IOException {
		ImageIO.write(img, "JPEG", f);
	}

	public List<MyPointMatch> findMatches(final List<MyFeature> p1, final List<MyFeature> p2) throws SignatureException {
		return findMatches(p1, p2, new L2Distance(), MyMatchFinder.DIST_THRESH);
	}
	
	public List<MyPointMatch> findMatches(final List<MyFeature> p1, final List<MyFeature> p2, final SignatureDistance<VectorSignature> distance, double distThresh) throws SignatureException {
		MyMatchFinder algo = new MyMatchFinder(p1, p2);
		algo.setDistance(distance);
		algo.setDistThresh(distThresh);
		return algo.work();
	}

	
	public List<MyPointMatch> ransac(List<MyPointMatch> matches) {
		return ransac(matches, false);		
	}
	
	public List<MyPointMatch> ransac(List<MyPointMatch> matches, boolean iterative) {
		return ransac(matches, iterative, MyRansac.EPSILON, MyRansac.ITERATIONS, MyRansac.MIN_INLIER_RATIO, MyRansac.MIN_MATCHES);
	}
	
	public List<MyPointMatch> ransac(List<MyPointMatch> matches, boolean iterative, float epsilon, int iterations, float minInlierRatio, int minMatches) {
		List<MyPointMatch> result = new ArrayList<MyPointMatch>();
		
		MyRansac algo = new MyRansac();
		algo.setEpsilon(epsilon);
		algo.setIterations(iterations);
		algo.setMinInlierRatio(minInlierRatio);
		algo.setMinMatches(minMatches);

		List<MyPointMatch> iteration = null;
		int group = 0;
		
		do {
			algo.estimateModel(matches);
			iteration = algo.getInliers();

			log("RANSAC [" + group + "] : " + iteration.size());

			for (MyPointMatch pm : iteration) {
				pm.setGroup(group);
				result.add(pm);
				matches.remove(pm);
			}

			group++;

		} while (iterative && iteration.size() > 0);

		return result;
	}

	public BufferedImage load(File f) throws IOException {
		BufferedImage img = ImageIO.read(f);
		return img;
	}

	public List<MyFeature> processSIFT(BufferedImage img) throws IOException {
		log("SIFT : " + img.getWidth() + " x " + img.getHeight());

		SIFT.set_fdsize(SIFT_FDSIZE);
		SIFT.fdbins(SIFT_FDBINS);
		SIFT.set_initial_sigma(SIFT_INITIAL_SIGMA);
		SIFT.set_max_size(SIFT_MAX_SIZE);
		SIFT.set_min_size(SIFT_MIN_SIZE);
		SIFT.set_steps(SIFT_STEPS);

		Vector<Feature> points = SIFT.getFeatures(img);
		ArrayList<MyFeature> r = new ArrayList<MyFeature>();
		for (Feature ip : points) {
			r.add(new MyFeature(ip));
		}

		return r;
	}

	public List<MyFeature> processSURF(BufferedImage img) throws IOException {
		int l = Math.max(img.getWidth(), img.getHeight());
		boolean doResize = (l > SIFT_MAX_SIZE * 1.1);

		float ratio = 1;

		if (doResize) {
			ratio = (float) SIFT_MAX_SIZE / (float) l;
			img = ImageTools.resize(img, (int) (img.getWidth() * ratio), (int) (img.getHeight() * ratio), true);
		}

		log("SURF : " + img.getWidth() + " x " + img.getHeight());

		Surf surf = new Surf(img, SURF_BALANCE_VALUE, SURF_THRESHOLD, SURF_OCTAVES);

		ArrayList<MyFeature> r = new ArrayList<MyFeature>();
		if (doResize) {
			ratio = 1f / ratio;
			for (SURFInterestPoint ip : surf.getFreeOrientedInterestPoints()) {
				r.add(new MyFeature(ip, ratio));
			}
		} else {
			for (SURFInterestPoint ip : surf.getFreeOrientedInterestPoints()) {
				r.add(new MyFeature(ip));
			}
		}

		return r;
	}
}
