package name.herve.imagematch;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;

public class TestOpenImaj {
	private final static String img1 = "/tmp/TestXuggler/frame-000380.png";
	private final static String img2 = "/home/nherve/Travail/Data/joconde/687px-Mona_Lisa,_by_Leonardo_da_Vinci_small.png";
	
	public static void main(String[] args) {
		try {
			MBFImage query = ImageUtilities.readMBF(new File(img1));
			MBFImage target = ImageUtilities.readMBF(new File(img2));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
