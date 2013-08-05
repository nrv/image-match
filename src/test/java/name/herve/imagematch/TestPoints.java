package name.herve.imagematch;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

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
			BufferedImage img1 = ImageMatch.load(new File(IMAGE_FILE_1));
			List<MyFeature> p1 = ImageMatch.processSIFT(img1);
			System.out.println("SIFT - [" + img1.getWidth() + " / " + img1.getHeight() + "] [" + p1.size() + "]");

			BufferedImage img2 = ImageMatch.load(new File(IMAGE_FILE_2));
			List<MyFeature> p2 = ImageMatch.processSIFT(img2);
			System.out.println("SIFT - [" + img2.getWidth() + " / " + img2.getHeight() + "] [" + p2.size() + "]");

			List<MyPointMatch> m = ImageMatch.findMatches(p1, p2);
			System.out.println("SIFT - [" + m.size() + "]");

			m = ImageMatch.ransac(m);
			System.out.println("SIFT - [" + m.size() + "]");

			ImageMatch.drawPoints(img1, p1);
			ImageMatch.drawPoints(img2, p2);
			ImageMatch.drawMatches(img1, img2, m, new File(new File(IMAGE_FILE_1).getParentFile(), "sift-matches.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SignatureException e) {
			e.printStackTrace();
		}
	}

	private static void testSURF() {
		try {
			BufferedImage img1 = ImageMatch.load(new File(IMAGE_FILE_1));
			List<MyFeature> p1 = ImageMatch.processSURF(img1);
			System.out.println("SURF - [" + img1.getWidth() + " / " + img1.getHeight() + "] [" + p1.size() + "]");

			BufferedImage img2 = ImageMatch.load(new File(IMAGE_FILE_2));
			List<MyFeature> p2 = ImageMatch.processSURF(img2);
			System.out.println("SURF - [" + img2.getWidth() + " / " + img2.getHeight() + "] [" + p2.size() + "]");

			List<MyPointMatch> m = ImageMatch.findMatches(p1, p2);
			System.out.println("SURF - [" + m.size() + "]");

			m = ImageMatch.ransac(m);
			System.out.println("SURF - [" + m.size() + "]");

			ImageMatch.drawPoints(img1, p1);
			ImageMatch.drawPoints(img2, p2);
			ImageMatch.drawMatches(img1, img2, m, new File(new File(IMAGE_FILE_1).getParentFile(), "surf-matches.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SignatureException e) {
			e.printStackTrace();
		}
	}

}
