package name.herve.imagematch;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

import name.herve.imagematch.impl.MyFeature;
import name.herve.imagematch.impl.MyPointMatch;

import plugins.nherve.toolbox.image.feature.signature.SignatureException;

public class TestPoints {
	private final static String ROOT = "/home/nherve/Travail/Data/Perso/data/";
	// private final static String IMAGE_FILE_1 = ROOT + "IMG_9684.JPG";
	// private final static String IMAGE_FILE_2 = ROOT + "IMG_7546.JPG";
	private final static String IMAGE_FILE_1 = ROOT + "IMG_9801.JPG";
	private final static String IMAGE_FILE_2 = ROOT + "IMG_9804.JPG";
	// private final static String IMAGE_FILE_1 = ROOT + "IMG_0443.JPG";
	// private final static String IMAGE_FILE_2 = ROOT + "IMG_0444.JPG";

	private final static DecimalFormat DF = new DecimalFormat("0.0000");

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		testSURF();
		// testSIFT();
	}

	private static void testSIFT() {
		try {
			ImageMatchHelper algo = new ImageMatchHelper();
			
			BufferedImage img1 = algo.load(new File(IMAGE_FILE_1));
			List<MyFeature> p1 = algo.processSIFT(img1);
			System.out.println("SIFT - [" + img1.getWidth() + " / " + img1.getHeight() + "] [" + p1.size() + "]");

			BufferedImage img2 = algo.load(new File(IMAGE_FILE_2));
			List<MyFeature> p2 = algo.processSIFT(img2);
			System.out.println("SIFT - [" + img2.getWidth() + " / " + img2.getHeight() + "] [" + p2.size() + "]");

			List<MyPointMatch> m = algo.findMatches(p1, p2);
			System.out.println("SIFT - [" + m.size() + "]");

			m = algo.ransac(m);
			System.out.println("SIFT - [" + m.size() + "]");

			algo.drawPoints(img1, p1);
			algo.drawPoints(img2, p2);
			algo.drawMatches(img1, img2, m, new File(new File(IMAGE_FILE_1).getParentFile(), "sift-matches.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SignatureException e) {
			e.printStackTrace();
		}
	}

	private static void testSURF() {
		try {
			ImageMatchHelper algo = new ImageMatchHelper();
			
			BufferedImage img1 = algo.load(new File(IMAGE_FILE_1));
			List<MyFeature> p1 = algo.processSURF(img1);
			System.out.println("SURF - [" + img1.getWidth() + " / " + img1.getHeight() + "] [" + p1.size() + "]");

			BufferedImage img2 = algo.load(new File(IMAGE_FILE_2));
			List<MyFeature> p2 = algo.processSURF(img2);
			System.out.println("SURF - [" + img2.getWidth() + " / " + img2.getHeight() + "] [" + p2.size() + "]");

			List<MyPointMatch> m = algo.findMatches(p1, p2);
			System.out.println("SURF - [" + m.size() + "]");

			m = algo.ransac(m);
			System.out.println("SURF - [" + m.size() + "]");

			algo.drawPoints(img1, p1);
			algo.drawPoints(img2, p2);
			algo.drawMatches(img1, img2, m, new File(new File(IMAGE_FILE_1).getParentFile(), "surf-matches.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SignatureException e) {
			e.printStackTrace();
		}
	}

}
