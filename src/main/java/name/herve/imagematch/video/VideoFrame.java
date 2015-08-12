package name.herve.imagematch.video;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import name.herve.imagematch.impl.MyPointMatch;
import fr.ina.research.rex.commons.tc.RexTimeCode;

public class VideoFrame {
	private BufferedImage image;
	private int width;
	private int heigth;
	private int frame;
	private long time;
	private RexTimeCode tc;
	private Map<File, List<MyPointMatch>> matches;
	private Map<String, List<TrackPosition>> boudingBoxes;

	public VideoFrame(BufferedImage image, int frame, long time) {
		super();
		this.image = image;
		this.frame = frame;
		this.time = time;
		this.width = image.getWidth();
		this.heigth = image.getHeight();
		matches = new HashMap<File, List<MyPointMatch>>();
		boudingBoxes = new HashMap<String, List<TrackPosition>>();
	}

	public void addBoudingBox(String n, TrackPosition bb) {
		if (!boudingBoxes.containsKey(n)) {
			boudingBoxes.put(n, new ArrayList<TrackPosition>());
		}
		boudingBoxes.get(n).add(bb);
	}

	public void addMatch(File f, List<MyPointMatch> m) {
		matches.put(f, m);
	}

	public Map<String, List<TrackPosition>> getBoudingBoxes() {
		return boudingBoxes;
	}

	public int getFrame() {
		return frame;
	}

	public int getHeight() {
		return heigth;
	}

	public BufferedImage getImage() {
		return image;
	}

	public Map<File, List<MyPointMatch>> getMatches() {
		return matches;
	}

	public RexTimeCode getTc() {
		return tc;
	}

	public long getTime() {
		return time;
	}

	public int getWidth() {
		return width;
	}

	public boolean hasBoundingBoxes() {
		return !boudingBoxes.isEmpty();
	}

	public boolean hasMatches() {
		return !matches.isEmpty();
	}

	public void setFrame(int frame) {
		this.frame = frame;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public void setTc(RexTimeCode tc) {
		this.tc = tc;
	}

	@Override
	public String toString() {
		return "[frame=" + frame + ", time=" + time + (tc != null ? " / " + tc : "") + "]";
	}
}
