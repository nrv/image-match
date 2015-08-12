package name.herve.imagematch.video;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.MediaListenerAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.IVideoPictureEvent;

public class VideoFrameExtractor extends MediaListenerAdapter {
	private final File video;

	private final VideoFrameProcessor processor;
	private int mVideoStreamIndex = -1;
	private int frame;
	private double fps;
	private final int n;
	private IOException lastError;

	public VideoFrameExtractor(File video, int n, VideoFrameProcessor processor) {
		super();
		this.processor = processor;
		this.video = video;
		this.n = n;
		fps = -1;
	}

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

		if ((frame % n) == 0) {
			try {
				processor.processFrame(new VideoFrame(event.getImage(), frame, event.getTimeStamp()));
			} catch (IOException e) {
				lastError = e;
			}
		}
	}

	public void start() throws IOException {
		this.frame = -1;

		IMediaReader reader = ToolFactory.makeReader(video.getAbsolutePath());
		reader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);
		reader.addListener(this);
		while ((reader.readPacket() == null) && (lastError == null)) {
			if (fps < 0 && mVideoStreamIndex >= 0) {
				fps = reader.getContainer().getStream(mVideoStreamIndex).getFrameRate().getDouble();
				processor.log("FPS (" + mVideoStreamIndex + "): " + fps);
			}
		}

		if (lastError != null) {
			throw lastError;
		}
	}
}
