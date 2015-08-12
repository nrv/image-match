package name.herve.imagematch.video;

import java.io.IOException;

import plugins.nherve.toolbox.AbleToLogMessages;

public interface VideoFrameProcessor extends AbleToLogMessages {
	void processFrame(VideoFrame frame) throws IOException;
}
