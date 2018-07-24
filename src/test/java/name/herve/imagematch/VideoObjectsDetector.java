package name.herve.imagematch;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.imageio.ImageIO;

import name.herve.imagematch.impl.MyFeature;
import name.herve.imagematch.impl.MyPointMatch;
import name.herve.imagematch.impl.ThresholdSecondBestMatchFinder;
import plugins.nherve.toolbox.Algorithm;
import plugins.nherve.toolbox.concurrent.TaskManager;
import plugins.nherve.toolbox.image.feature.signature.L2Distance;
import fr.ina.research.amalia.model.tracking.TrackPosition;
import fr.ina.research.rex.commons.tc.RexTimeCode;

public class VideoObjectsDetector /* extends Algorithm implements VideoFrameProcessor<String, List<BoundingBox>> */{
//	public static void main(String[] args) {
//		try {
//			VideoObjectsDetector mv = new VideoObjectsDetector();
//			mv.setDebugActivated(true, new File("/tmp/MatchVideo"));
//
//			// mv.extractExampleFeatures("Credit Lyonnais", new File("/home/nherve/Travail/Data/videos/tracking_dataset/logos/CreditLyonnais"));
//			// mv.processVideo(new File("/home/nherve/Travail/Data/videos/tracking_dataset/extract1.mpg"));
//
//			mv.extractExampleFeatures("Vittel", new File("/home/nherve/Travail/Data/videos/tracking_dataset/logos/Vittel"));
//			mv.processVideo(new File("/home/nherve/Travail/Data/videos/tracking_dataset/vittel_extract2.mp4"), 5);
//
//			mv.shutdown();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//	private Map<String, Set<List<MyFeature>>> learningFeatures;
//	private ImageMatchHelper matchHelper;
//	private List<VideoFrame<String, List<BoundingBox>>> frames;
//	private TaskManager tm;
//	private boolean debugActivated;
//	private double threshold = 0.75;
//	private int saliencySigma = 10;
//	private float saliencyThreshold = 0.3f;
//	private File debugDir;
//	private boolean useSift = true;
//
//	public VideoObjectsDetector() {
//		super(true);
//
//		debugActivated = true;
//
//		matchHelper = new ImageMatchHelper();
//
//		tm = TaskManager.getMainInstance();
//		tm.setLogEnabled(false);
//		tm.setShowProgress(false);
//
//		log("Working with " + tm.getCorePoolSize() + " threads");
//
//		learningFeatures = new HashMap<String, Set<List<MyFeature>>>();
//		frames = new ArrayList<VideoFrame<String, List<BoundingBox>>>();
//	}
//
//	public void extractExampleFeatures(String objectName, File directory) throws IOException {
//		Set<List<MyFeature>> data = new HashSet<List<MyFeature>>();
//		List<Future<List<MyFeature>>> tasks = new ArrayList<Future<List<MyFeature>>>();
//		for (final File f : getImageFiles(directory)) {
//			tasks.add(tm.submit(new Callable<List<MyFeature>>() {
//
//				@Override
//				public List<MyFeature> call() throws Exception {
//					BufferedImage img = matchHelper.load(f);
//					List<MyFeature> fs = useSift ? matchHelper.processSIFT(img) : matchHelper.processSURF(img);
//					log((useSift ? "SIFT" : "SURF") + " - [" + img.getWidth() + " x " + img.getHeight() + "] [" + fs.size() + "]  -- " + f.getName());
//					return fs;
//				}
//			}));
//		}
//
//		for (Future<List<MyFeature>> task : tasks) {
//			try {
//				data.add(task.get());
//			} catch (InterruptedException e) {
//				throw new IOException(e);
//			} catch (ExecutionException e) {
//				throw new IOException(e);
//			}
//		}
//
//		learningFeatures.put(objectName, data);
//	}
//
//	private List<File> getImageFiles(File dir) {
//		if (dir.exists() && dir.isDirectory()) {
//			File[] files = dir.listFiles(new FileFilter() {
//				@Override
//				public boolean accept(File f) {
//					String n = f.getName().toUpperCase();
//					return f.isFile() && (n.endsWith(".JPG") || n.endsWith(".JPEG") || n.endsWith(".PNG") || n.endsWith(".GIF"));
//				}
//			});
//			return Arrays.asList(files);
//		}
//		return null;
//	}
//
//	public boolean isDebugActivated() {
//		return debugActivated;
//	}
//
//	public void processVideo(File video, int freq) throws IOException {
//		if (isDebugActivated()) {
//			debugDir.mkdirs();
//		}
//
//		VideoFrameExtractor<String, List<BoundingBox>> extractor = new VideoFrameExtractor<String, List<BoundingBox>>(video, freq, this);
//		extractor.start();
//
//		tm.shutdown();
//		while (!tm.isTerminated()) {
//			try {
//				Thread.sleep(100);
//			} catch (InterruptedException e) {
//			}
//		}
//	}
//
//	@Override
//	public void processFrame(final VideoFrame<String, List<BoundingBox>> frame) throws IOException {
//		Callable<VideoFrame<String, List<BoundingBox>>> task = new Callable<VideoFrame<String, List<BoundingBox>>>() {
//
//			@Override
//			public VideoFrame<String, List<BoundingBox>> call() throws Exception {
//				frame.setTc(new RexTimeCode((double) frame.getTime() / (double) Global.DEFAULT_PTS_PER_SECOND));
//
//				List<MyFeature> frameFeatures = useSift ? matchHelper.processSIFT(frame.getImage()) : matchHelper.processSURF(frame.getImage());
//				// log("[" + tm.getQueueSize() + "]" + frame + " " + (useSift ? "SIFT" : "SURF") + " - [" + frame.getWidth() + " x " + frame.getHeight() + "] [" + frameFeatures.size() + "]");
//
//				PointMatchFinder matchFinder = new ThresholdSecondBestMatchFinder();
//				matchFinder.setParameter(ThresholdSecondBestMatchFinder.DIST_THRESH_P, threshold);
//
//				// PointMatchFinder matchFinder = new MutualKNNMatchFinder();
//
//				matchFinder.setDistance(new L2Distance());
//
//				Graphics2D g2 = null;
//
//				matchFinder.setP1(frameFeatures);
//				for (Entry<String, Set<List<MyFeature>>> entry : learningFeatures.entrySet()) {
//					String objectName = entry.getKey();
//					SaliencyMap objectMap = new SaliencyMap(frame.getWidth(), frame.getHeight(), (6 * saliencySigma) + 1, saliencySigma);
//
//					int nbMatchImages = 0;
//
//					List<BoundingBox> thisObjectMatches = new ArrayList<BoundingBox>();
//					for (List<MyFeature> features : entry.getValue()) {
//						matchFinder.setP2(features);
//						List<MyPointMatch> m = matchFinder.work();
//						m = matchHelper.ransac(m);
//						if ((m != null) && (m.size() > 0)) {
//							// log("[" + tm.getQueueSize() + "]" + frame + "     ~ " + m.size() + " matches with " + objectName);
//
//							for (MyPointMatch mmp : m) {
//								objectMap.add((int) mmp.getP1().getX(), (int) mmp.getP1().getY());
//							}
//							nbMatchImages++;
//						}
//					}
//
//					
//					float bbSaliencyGlobalThreshold = saliencyThreshold * nbMatchImages * objectMap.getMaxGaussianValue();
//					double bbMinSize = Math.pow(saliencySigma, 2);
//					
//					if (isDebugActivated()) {
//						SaliencyMap tempmap = objectMap.clone();
//						tempmap.postprocess();
//						MBFImage mbf = ImageUtilities.createMBFImage(frame.getImage(), false);
//						FImage fimg = Transforms.calculateIntensity(mbf);
//						mbf = new MBFImage(fimg.clone(), fimg.clone(), fimg.clone());
//						mbf.getBand(1).addInplace(tempmap.getMap());
//						frame.setImage(ImageUtilities.createBufferedImage(mbf));
//						g2 = (Graphics2D) frame.getImage().getGraphics();
//						g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//					}
//
//					// TODO composantes connexes
//
//					
//					BoundingBox bb = objectMap.getBoundingBox(bbSaliencyGlobalThreshold);
//					
//					if (isDebugActivated()) {
//						SaliencyMap tempmap = objectMap.clone();
//						tempmap.postprocess();
//						MBFImage mbf = ImageUtilities.createMBFImage(frame.getImage(), false);
//						mbf.getBand(0).addInplace(tempmap.getMap());
//						frame.setImage(ImageUtilities.createBufferedImage(mbf));
//						g2 = (Graphics2D) frame.getImage().getGraphics();
//						g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//					}
//
//					if ((bb != null) && (bb.getSurface() <= bbMinSize)) {
//						log("[" + tm.getQueueSize() + "]" + frame + "     ~ small bounding box removed : " + bb);
//						bb = null;
//					}
//
//					log("[" + tm.getQueueSize() + "]" + frame + "     ~ max saliency = " + objectMap.max() + " / " + bbSaliencyGlobalThreshold + " (" + nbMatchImages + ") : " + bb);
//
//					if (bb != null) {
//						thisObjectMatches.add(bb);
//					}
//
//					if (!thisObjectMatches.isEmpty()) {
//						frame.addMatch(objectName, thisObjectMatches);
//					}
//				}
//
//				if (frame.hasMatches()) {
//					for (Entry<String, List<BoundingBox>> entry : frame.getMatches().entrySet()) {
//						String objectName = entry.getKey();
//						for (BoundingBox bbx : entry.getValue()) {
//							if (isDebugActivated()) {
//								g2.setColor(Color.BLUE);
//								g2.drawRect((int) bbx.getX1(), (int) bbx.getY1(), (int) (bbx.getX2() - bbx.getX1()), (int) (bbx.getY2() - bbx.getY1()));
//							}
//
//							TrackPosition pos = new TrackPosition();
//							pos.setTc(frame.getTc());
//							pos.setXc((bbx.getX1() + bbx.getX2()) / 2.);
//							pos.setYc((bbx.getY1() + bbx.getY2()) / 2.);
//							pos.setHw((bbx.getX2() - bbx.getX1()) / 2.);
//							pos.setHh((bbx.getY2() - bbx.getY1()) / 2.);
//							pos.setO(0);
//							pos.setScore(0);
//
//							frame.addBoudingBox(objectName, pos);
//						}
//					}
//				}
//				
//				if (isDebugActivated()) {
//					ImageIO.write(frame.getImage(), "png", new File(debugDir, frame.getTc() + ".png"));
//				}
//				
//				frame.setImage(null);
//
//				synchronized (frames) {
//					frames.add(frame);
//				}
//
//				// log("[" + tm.getQueueSize() + "]" + frame + " DONE");
//
//				return frame;
//			}
//		};
//
//		tm.submit(task);
//	}
//
//	public void setDebugActivated(boolean debugActivated, File debugDir) {
//		this.debugActivated = debugActivated;
//		this.debugDir = debugDir;
//	}
//
//	public void shutdown() {
//		TaskManager.shutdownAll();
//	}

}
