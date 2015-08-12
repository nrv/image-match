package name.herve.imagematch;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

import javax.imageio.ImageIO;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.MediaListenerAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.IVideoPictureEvent;

public class TestXuggler extends MediaListenerAdapter {
	private final static String MP4_FILE = "/home/nherve/Travail/Data/videos/MGCAF0006836--AP_1_213419_231219.MP4";
	private final static String TMP_DIR = "/tmp/TestXuggler";

	public static void main(String[] args) {
		try {
			new TestXuggler().work(MP4_FILE, TMP_DIR, 10);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private File tmp;
	private int mVideoStreamIndex = -1;
	private int frame;
	private int n;
	private DecimalFormat df;

	@Override
	public void onVideoPicture(IVideoPictureEvent event) {
		if (event.getStreamIndex() != mVideoStreamIndex) {
			if (-1 == mVideoStreamIndex) {
				mVideoStreamIndex = event.getStreamIndex();
			} else {
				return;
			}
		}
		
		frame++;
		
		if (frame % n == 0) {
			File file = new File(tmp, "frame-" + df.format(frame) + ".png");
	        try {
				ImageIO.write(event.getImage(), "png", file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void work(String mp4file, String tempDir, int n) throws IOException {
		tmp = new File(tempDir);
		tmp.mkdirs();
		
		this.n = n;
		this.frame = -1;
		
		df = new DecimalFormat("000000");

		IMediaReader reader = ToolFactory.makeReader(mp4file);
		reader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);
		reader.addListener(this);
		while (reader.readPacket() == null) {
			do {
			} while (false);
		}
	}

}
