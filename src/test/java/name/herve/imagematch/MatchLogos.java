package name.herve.imagematch;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import plugins.nherve.toolbox.image.feature.signature.SignatureException;

public class MatchLogos {
	private final static String ROOT = "/home/nherve/Travail/Data/logos";

	private List<File> getFiles(File dir) {
		if (dir.exists() && dir.isDirectory()) {
			File[] files = dir.listFiles(new FileFilter() {
				@Override
				public boolean accept(File f) {
					String n = f.getName().toUpperCase();
					return f.isFile() && (n.endsWith(".JPG") || n.endsWith(".JPEG") || n.endsWith(".PNG") || n.endsWith(".GIF"));
				}
			});
			return Arrays.asList(files);
		}
		return null;
	}
	
	private BufferedImage deepCopy(BufferedImage bi) {
		 ColorModel cm = bi.getColorModel();
		 boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		 WritableRaster raster = bi.copyData(null);
		 return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
		}

	public void process(String r) throws IOException, SignatureException {
		File root = new File(r);
		File out = new File(root, "matches");
		out.mkdirs();
		
		List<File> files = getFiles(root);

		Map<File, List<MyFeature>> features = new HashMap<File, List<MyFeature>>();
		Map<File, BufferedImage> images = new HashMap<File, BufferedImage>();

		for (File f : files) {
			BufferedImage img = ImageMatch.load(f);
			images.put(f, img);
			List<MyFeature> fs = ImageMatch.processSURF(img);
			System.out.println("SURF - [" + img.getWidth() + " x " + img.getHeight() + "] [" + fs.size() + "]  -- " + f.getName());
			features.put(f, fs);
		}

		for (int f1 = 0; f1 < files.size() - 1; f1++) {
			for (int f2 = f1 + 1; f2 < files.size(); f2++) {
				List<MyPointMatch> m = ImageMatch.findMatches(features.get(files.get(f1)), features.get(files.get(f2)));
				m = ImageMatch.ransac(m);
				if (m != null && m.size() > 0) {
					System.out.println("["+f1+" / "+f2+"] [" + m.size() + "] " + files.get(f1).getName() + "  <->  " + files.get(f2).getName());
					BufferedImage img1 = deepCopy(images.get(files.get(f1)));
					BufferedImage img2 = deepCopy(images.get(files.get(f2)));
					
					//ImageMatch.drawPoints(img1, features.get(files.get(f1)));
					//ImageMatch.drawPoints(img2, features.get(files.get(f2)));
					
					ImageMatch.drawMatches(img1, img2, m, new File(out, f1 + "-" + f2 + ".jpg"));
				}
			}
		}
	}

	public static void main(String[] args) {
		try {
			new MatchLogos().process(ROOT);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SignatureException e) {
			e.printStackTrace();
		}
	}

}
