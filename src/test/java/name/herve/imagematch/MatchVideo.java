package name.herve.imagematch;

import plugins.nherve.toolbox.Algorithm;

public class MatchVideo extends Algorithm /*implements VideoFrameProcessor */{
//	public static void main(String[] args) {
//		try {
//			MatchVideo mv = new MatchVideo();
//			mv.setDebugActivated(true, new File("/tmp/MatchVideo"));
//			mv.extractExampleFeatures(new File("/home/nherve/Travail/Data/joconde"));
//			
//			String f = "MGCAF0006836--AP_1_213419_231219";
////			String f = "FPVDB03080705_1_100822_113811";
////			String f = "KPCAB860319_4_312907_344419";
//			
//			mv.matchVideo(new File("/home/nherve/Travail/Data/videos/" + f + ".MP4"));
//			mv.postProcess(new File("/home/nherve/Travail/Data/videos/" + f + ".json"));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//	private Map<File, List<MyFeature>> learningFeatures;
//	private ImageMatchHelper matchHelper;
//	private List<VideoFrame> frames;
//	private TaskManager tm;
//	private String objectName;
//	private boolean debugActivated;
//	private File debugDir;
//
//	public MatchVideo() {
//		super(true);
//
//		debugActivated = false;
//
//		matchHelper = new ImageMatchHelper();
//
//		tm = new TaskManager(2 * Runtime.getRuntime().availableProcessors());
//		tm.setLogEnabled(false);
//		tm.setShowProgress(false);
//
//		log("Working with " + tm.getCorePoolSize() + " threads");
//
//		learningFeatures = new HashMap<File, List<MyFeature>>();
//		frames = new ArrayList<VideoFrame>();
//	}
//
//	public void extractExampleFeatures(File directory) throws IOException {
//		objectName = directory.getName();
//
//		for (File f : getImageFiles(directory)) {
//			BufferedImage img = matchHelper.load(f);
//			List<MyFeature> fs = matchHelper.processSURF(img);
//			log("SURF - [" + img.getWidth() + " x " + img.getHeight() + "] [" + fs.size() + "]  -- " + f.getName());
//			learningFeatures.put(f, fs);
//		}
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
//	public void matchVideo(File video) throws IOException {
//		if (isDebugActivated()) {
//			debugDir.mkdirs();
//		}
//
//		VideoFrameExtractor extractor = new VideoFrameExtractor(video, 10, this);
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
//	public void postProcess(File jsonFile) throws IOException {
//		try {
//			Collections.sort(frames, new Comparator<VideoFrame>() {
//
//				@Override
//				public int compare(VideoFrame v1, VideoFrame v2) {
//					return v1.getFrame() - v2.getFrame();
//				}
//			});
//
//			// Post-process
//			double maxScore = 0.;
//
//			List<TrackPosition> allPositions = new ArrayList<TrackPosition>();
//
//			for (VideoFrame frame : frames) {
//				for (Entry<String, List<TrackPosition>> entry : frame.getBoudingBoxes().entrySet()) {
//					for (TrackPosition r : entry.getValue()) {
//						allPositions.add(r);
//					}
//				}
//			}
//			
//			TrackedObject to = new TrackedObject();
//			TrackSegment ts = new TrackSegment();
//			
//			// Temporal stuff
//			Collections.sort(allPositions);
//			TrackPosition previous = null;
//			boolean previousAdded = false;
//			for (TrackPosition p : allPositions) {
//				if (previous == null) {
//					previousAdded = false;
//				} else {
//					if (p.getTc().getSecond() - previous.getTc().getSecond() < 1.0) {
//						if (!previousAdded) {
//							ts.add(previous);
//						}
//						ts.add(p);
//						previousAdded = true;
//					} else {
//						if (!ts.isEmpty()) {
//							to.add(ts);
//							ts = new TrackSegment();
//						}
//						previousAdded = false;
//					}
//				}
//				previous = p;
//			}
//			
//			if (!ts.isEmpty()) {
//				to.add(ts);
//			}
//
//			to.wrap();
//			
//			// Score stuff
//			List<TrackSegment> toRemove = new ArrayList<TrackSegment>();
//			double threshold = to.getScore() * 0.2;
//			log("Max score : " + to.getScore());
//			log("Threshold : " + threshold);
//			for (TrackSegment cts : to) {
//				log("Segment " + cts);
//				if (cts.getScore() < threshold) {
//					toRemove.add(cts);
//				}
//			}
////			for (TrackSegment cts : toRemove) {
////				log("Removing segment " + cts);
////				to.remove(cts);
////			}
////			to.wrap();
//
//			// Dump JSON file
//			MetadataBlock m = TrackedObject.createTrackingMetadataBlock(to, "tracking");
//			m.setProcessor("Ina Research Department - N. HERVE");
//			m.setAlgorithm("image-match-video");
//			m.setVersion(1);
//			m.getRootLocalisationBlock().setLabel(objectName);
//
//			List<MetadataBlock> asList = new ArrayList<MetadataBlock>();
//			asList.add(m);
//
//			MetadataFactory.serializeToJsonFile(asList, jsonFile);
//		} catch (AmaliaException e) {
//			throw new IOException(e);
//		}
//
//	}
//
//	@Override
//	public void processFrame(final VideoFrame frame) throws IOException {
//		Callable<VideoFrame> task = new Callable<VideoFrame>() {
//
//			@Override
//			public VideoFrame call() throws Exception {
//				frame.setTc(new RexTimeCode((double) frame.getTime() / (double) Global.DEFAULT_PTS_PER_SECOND));
//
//				List<MyFeature> frameFeatures = matchHelper.processSURF(frame.getImage());
//				log(frame + " SURF - [" + frame.getWidth() + " x " + frame.getHeight() + "] [" + frameFeatures.size() + "]");
//
////				PointMatchFinder matchFinder = new ThresholdSecondBestMatchFinder();
////				matchFinder.setParameter(ThresholdSecondBestMatchFinder.DIST_THRESH_P, ThresholdSecondBestMatchFinder.DIST_THRESH_V);
//				
//				PointMatchFinder matchFinder = new MutualKNNMatchFinder();
//
//				matchFinder.setDistance(new L2Distance());
//				
//				matchFinder.setP1(frameFeatures);
//				for (Entry<File, List<MyFeature>> entry : learningFeatures.entrySet()) {
//					matchFinder.setP2(entry.getValue());
//					List<MyPointMatch> m = matchFinder.work();
//					m = matchHelper.ransac(m);
//					if ((m != null) && (m.size() > 0)) {
//						log(frame + "     ~ " + m.size() + " matches with " + entry.getKey().getName());
//						frame.addMatch(entry.getKey(), m);
//					}
//				}
//
//				if (frame.hasMatches()) {
//					// if (frame.getMatches().size() > 1) {
//					Graphics2D g2 = null;
//
//					if (isDebugActivated()) {
//						g2 = (Graphics2D) frame.getImage().getGraphics();
//						g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//						g2.setColor(Color.RED);
//					}
//
//					float x1 = frame.getWidth();
//					float x2 = 0;
//					float y1 = frame.getHeight();
//					float y2 = 0;
//					float score = 0;
//					for (Entry<File, List<MyPointMatch>> entry : frame.getMatches().entrySet()) {
//						for (MyPointMatch mp : entry.getValue()) {
//							if (mp.getP1().getX() < x1) {
//								x1 = mp.getP1().getX();
//							}
//							if (mp.getP1().getX() > x2) {
//								x2 = mp.getP1().getX();
//							}
//							if (mp.getP1().getY() < y1) {
//								y1 = mp.getP1().getY();
//							}
//							if (mp.getP1().getY() > y2) {
//								y2 = mp.getP1().getY();
//							}
//							if (isDebugActivated()) {
//								g2.drawRect((int) (mp.getP1().getX() - 4), (int) (mp.getP1().getY() - 4), 9, 9);
//							}
//							score++;
//						}
//					}
//
//					if (isDebugActivated()) {
//						g2.setColor(Color.GREEN);
//						g2.drawRect((int) x1, (int) y1, (int) (x2 - x1), (int) (y2 - y1));
//						ImageIO.write(frame.getImage(), "png", new File(debugDir, frame.getTc() + ".png"));
//					}
//
//					x1 /= frame.getWidth();
//					x2 /= frame.getWidth();
//					y1 /= frame.getHeight();
//					y2 /= frame.getHeight();
//
//					TrackPosition pos = new TrackPosition();
//					pos.setTc(frame.getTc());
//					pos.setXc((x1 + x2) / 2.);
//					pos.setYc((y1 + y2) / 2.);
//					pos.setHw((x2 - x1) / 2.);
//					pos.setHh((y2 - y1) / 2.);
//					pos.setO(0);
//					pos.setScore(score);
//
//					frame.addBoudingBox(objectName, pos);
//				}
//
//				frame.setImage(null);
//
//				synchronized (frames) {
//					frames.add(frame);
//				}
//
//				return frame;
//			}
//		};
//
//		while (tm.getQueueSize() >= 100) {
//			try {
//				Thread.sleep(100);
//			} catch (InterruptedException e) {
//			}
//		}
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
//		tm.shutdown();
//	}

}
