package mpi.cbg.fly;

/**
 * <p>Title: ImageArrayConverter</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author Stephan Preibisch and Jonathan Odul <jonathan2706@gmail.com>
 * @version 1.0
 */

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class ImageArrayConverter {
	public static boolean CUTOFF_VALUES = true;
	public static boolean NORM_VALUES = false;

	/**
	 * @author Jonathan ODUL 2009
	 * @link http://www.jidul.com
	 * @version 1.0
	 * @param w
	 *            width of the picture
	 * @param h
	 *            height of the picture
	 * @param pixels
	 *            [] tab of pixels RGB color (ex: red 0xff0000)
	 * @return picture with the type FloatArray2D
	 */
	public static FloatArray2D ArrayToFloatArray2D(int w, int h, int pixels[]) {
		FloatArray2D image = new FloatArray2D(w, h);

		int rgb, r, g, b;
		for (int i = 0; i < pixels.length; i++) {
			rgb = pixels[i];
			b = rgb & 0xff;
			rgb = rgb >> 8;
			g = rgb & 0xff;
			rgb = rgb >> 8;
			r = rgb & 0xff;
			image.data[i] = 0.3f * r + 0.6f * g + 0.1f * b;
		}

		return image;
	}

	public static void FloatArrayToFloatProcessor(Image ip, FloatArray2D pixels) {
		int[] data = new int[pixels.width * pixels.height];

		int count = 0;
		for (int y = 0; y < pixels.height; y++) {
			for (int x = 0; x < pixels.width; x++) {
				data[count] = (int) pixels.data[count++];
			}
		}

		BufferedImage buf = toBufferedImage(ip);
		int w = buf.getWidth();
		int h = buf.getWidth();
		buf.setRGB(0, 0, w, h, data, 0, w);
		ip = buf;
	}

	static int[] getRGB(int rgba) {
		int[] rgb = new int[4];
		rgb[3] = rgba >> 24 & 0xFF; // alpha
		rgb[0] = rgba >> 16 & 0xFF; // red
		rgb[1] = rgba >> 8 & 0xFF; // green
		rgb[2] = rgba & 0xFF;// blue;

		return rgb;
	}

	/**
	 * @author Jonathan ODUL 2009
	 * @link http://www.jidul.com
	 * @version 1.0
	 * @param ip
	 *            picture to convert
	 * @return picture with the type FloatArray2D
	 */
	public static FloatArray2D ImageToFloatArray2D(Image ip) {
		FloatArray2D image;
		BufferedImage buf = toBufferedImage(ip);
		int w = buf.getWidth();
		int h = buf.getHeight();

		int[] pixels = new int[w * h];
		buf.getRGB(0, 0, w, h, pixels, 0, w);

		int count = 0;

		image = new FloatArray2D(w, h);

		int rgb, r, g, b;
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				rgb = pixels[count];
				b = rgb & 0xff;
				rgb = rgb >> 8;
				g = rgb & 0xff;
				rgb = rgb >> 8;
				r = rgb & 0xff;
				image.data[count++] = 0.3f * r + 0.6f * g + 0.1f * b;
			}
		}

		return image;
	}

	public static void normPixelValuesToByte(float[][] pixels, boolean cutoff) {
		float max = 0, min = 255;

		// check minmal and maximal values or cut of values that are higher or
		// lower than 255 resp. 0
		for (int y = 0; y < pixels[0].length; y++) {
			for (int x = 0; x < pixels.length; x++) {
				if (cutoff) {
					if (pixels[x][y] < 0) {
						pixels[x][y] = 0;
					}

					if (pixels[x][y] > 255) {
						pixels[x][y] = 255;
					}
				} else {
					if (pixels[x][y] < min) {
						min = pixels[x][y];
					}

					if (pixels[x][y] > max) {
						max = pixels[x][y];
					}
				}
			}
		}

		if (cutoff) {
			return;
		}

		// if they do not match bytevalues we have to do something
		if (max > 255 || min < 0) {
			double factor;

			factor = (max - min) / 255.0;

			for (int y = 0; y < pixels[0].length; y++) {
				for (int x = 0; x < pixels.length; x++) {
					pixels[x][y] = (int) ((pixels[x][y] - min) / factor);
				}
			}
		}
	}

	public static void normPixelValuesToByte(int[][] pixels, boolean cutoff) {
		int max = 0, min = 255;

		// check minmal and maximal values or cut of values that are higher or
		// lower than 255 resp. 0
		for (int y = 0; y < pixels[0].length; y++) {
			for (int x = 0; x < pixels.length; x++) {
				if (cutoff) {
					if (pixels[x][y] < 0) {
						pixels[x][y] = 0;
					}

					if (pixels[x][y] > 255) {
						pixels[x][y] = 255;
					}
				} else {
					if (pixels[x][y] < min) {
						min = pixels[x][y];
					}

					if (pixels[x][y] > max) {
						max = pixels[x][y];
					}
				}
			}
		}

		if (cutoff) {
			return;
		}

		// if they do not match bytevalues we have to do something
		if (max > 255 || min < 0) {
			double factor;

			factor = (max - min) / 255.0;

			for (int y = 0; y < pixels[0].length; y++) {
				for (int x = 0; x < pixels.length; x++) {
					pixels[x][y] = (int) ((pixels[x][y] - min) / factor);
				}
			}
		}
	}

	static BufferedImage toBufferedImage(Image image) {
		/** On test si l'image n'est pas d�ja une instance de BufferedImage */
		if (image instanceof BufferedImage) {
			return (BufferedImage) image;
		} else {
			/** On s'assure que l'image est compl�tement charg�e */
			// image = new ImageIcon(image).getImage();

			/** On cr�e la nouvelle image */
			BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);

			Graphics g = bufferedImage.createGraphics();
			g.drawImage(image, 0, 0, null);
			g.dispose();

			return bufferedImage;
		}
	}

}
