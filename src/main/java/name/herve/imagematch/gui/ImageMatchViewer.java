package name.herve.imagematch.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JRadioButton;
import javax.swing.SwingWorker;

import name.herve.imagematch.ImageMatch;
import name.herve.imagematch.MyFeature;
import name.herve.imagematch.MyPointMatch;
import name.herve.imagematch.gui.util.FileChooserListener;
import name.herve.imagematch.gui.util.FileChooserWithTextField;
import name.herve.imagematch.gui.viewer.ImageViewer;
import name.herve.imagematch.gui.viewer.ImageViewerListener;
import name.herve.imagematch.gui.viewer.ImageViewerPanel;
import plugins.nherve.toolbox.Algorithm;
import plugins.nherve.toolbox.gui.GUIUtil;

public class ImageMatchViewer extends Algorithm implements ActionListener, ImageViewerListener, FileChooserListener, ItemListener {
	private class ImageLoader extends SwingWorker<BufferedImage, Object> {
		private File f;
		private ImageViewerPanel v;

		public ImageLoader(File f, ImageViewerPanel v) {
			super();
			this.f = f;
			this.v = v;
		}

		@Override
		protected BufferedImage doInBackground() throws Exception {
			try {
				return ImageMatch.load(f);
			} catch (IOException e) {
				System.err.println(f.getAbsolutePath() + " : " + e.getMessage());
				return null;
			}
		}

		@Override
		protected void done() {
			try {
				v.setImage(get(), f.getName());
				v.setFeatures(null);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
	}

	private class MatchComputer extends SwingWorker<List<MyPointMatch>, Object> {
		private ImageViewerPanel v1;
		private ImageViewerPanel v2;

		public MatchComputer(ImageViewerPanel v1, ImageViewerPanel v2) {
			super();
			this.v1 = v1;
			this.v2 = v2;
		}

		@Override
		protected List<MyPointMatch> doInBackground() throws Exception {
			if ((v1.getFeatures() != null) && (v2.getFeatures() != null)) {
				List<MyPointMatch> m = ImageMatch.findMatches(v1.getFeatures(), v2.getFeatures());
				log("Matches computed [" + m.size() + "]");
				m = ImageMatch.ransac(m);
				log("RANSAC computed [" + m.size() + "]");
				return m;
			}
			return null;
		}
	}

	private class POIComputer extends SwingWorker<List<MyFeature>, Object> {
		private ImageViewerPanel v;
		private boolean doSurf;

		public POIComputer(ImageViewerPanel v, boolean doSurf) {
			super();
			this.v = v;
			this.doSurf = doSurf;
		}

		@Override
		protected List<MyFeature> doInBackground() throws Exception {
			try {
				List<MyFeature> f = doSurf ? ImageMatch.processSURF(v.getImage()) : ImageMatch.processSIFT(v.getImage());
				log("Features extracted for " + v.getLabel());
				return f;
			} catch (IOException e) {
				logError(e.getMessage());
				return null;
			}
		}

		@Override
		protected void done() {
			try {
				v.setFeatures(get());
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ImageMatchViewer application = new ImageMatchViewer();
		String defaultLocation = null;
		if (args.length > 0) {
			defaultLocation = args[0];
		}
		application.startInterface(defaultLocation);
	}

	private FileChooserWithTextField fc1;
	private FileChooserWithTextField fc2;
	private ImageViewerPanel iv1;
	private ImageViewerPanel iv2;

	private JButton btCompute;
	private JButton btMatch;
	private JCheckBox cbSynchroOpacity;
	private JRadioButton rbSift;
	private JRadioButton rbSurf;
	private JRadioButton rbSquare;
	private JRadioButton rbCircle;
	private JRadioButton rbFill;
	private JRadioButton rbBorder;

	public ImageMatchViewer() {
		super(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();

		if (o == null) {
			return;
		}

		if (o instanceof JRadioButton) {
			JRadioButton b = (JRadioButton) e.getSource();
			if ((b == rbCircle) || (b == rbSquare)) {
				iv1.setUseCircle(rbCircle.isSelected());
				iv2.setUseCircle(rbCircle.isSelected());
			} else if ((b == rbFill) || (b == rbBorder)) {
				iv1.setFill(rbFill.isSelected());
				iv2.setFill(rbFill.isSelected());
			}
			return;
		}

		if (o instanceof JButton) {
			JButton b = (JButton) e.getSource();
			if (b == btCompute) {
				computePOI();
			} else if (b == btMatch) {
				computeMatch();
			}
			return;
		}

	}

	private void computeMatch() {
		new MatchComputer(iv1, iv2).execute();
	}

	private void computePOI() {
		if (iv1.getImage() != null) {
			new POIComputer(iv1, rbSurf.isSelected()).execute();
		}
		if (iv2.getImage() != null) {
			new POIComputer(iv2, rbSurf.isSelected()).execute();
		}
	}

	@Override
	public void fileChoosen(FileChooserWithTextField chooser) {
		if (chooser == fc1) {
			new ImageLoader(fc1.getChoosenFile(), iv1).execute();
		} else {
			new ImageLoader(fc2.getChoosenFile(), iv2).execute();
		}

	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		Object o = e.getSource();

		if (o == null) {
			return;
		}

		if (o instanceof JCheckBox) {
			JCheckBox c = (JCheckBox) e.getSource();

			if (c == cbSynchroOpacity) {
				if (cbSynchroOpacity.isSelected()) {
					iv2.setOpacity(iv1.getOpacity());
				}
			}
		}
	}

	@Override
	public void opacityChanged(ImageViewer v) {
		if (cbSynchroOpacity.isSelected()) {
			if (v == iv1.getViewer()) {
				iv2.setOpacity(iv1.getOpacity());
			} else {
				iv1.setOpacity(iv2.getOpacity());
			}
		}
	}

	@Override
	public void poiChanged(ImageViewer v) {
	}

	public void startInterface(String defaultLocation) {
		// JFrame.setDefaultLookAndFeelDecorated(true);
		JFrame frame = new JFrame("Image match viewer");
		frame.setSize(1200, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container mainPanel = frame.getContentPane();
		mainPanel.setLayout(new BorderLayout());

		fc1 = new FileChooserWithTextField("Image 1", defaultLocation);
		fc1.addFileChooserListener(this);
		fc2 = new FileChooserWithTextField("Image 2", defaultLocation);
		fc2.addFileChooserListener(this);
		mainPanel.add(GUIUtil.createLineBoxPanel(fc1, Box.createHorizontalStrut(10), fc2), BorderLayout.PAGE_START);

		iv1 = new ImageViewerPanel();
		iv1.addImageViewerListener(this);
		iv2 = new ImageViewerPanel();
		iv2.addImageViewerListener(this);
		mainPanel.add(GUIUtil.createLineBoxPanel(iv1, Box.createHorizontalStrut(10), iv2), BorderLayout.CENTER);

		cbSynchroOpacity = new JCheckBox("Synchro");
		cbSynchroOpacity.setSelected(false);
		cbSynchroOpacity.addItemListener(this);

		ButtonGroup bg1 = new ButtonGroup();
		rbSift = new JRadioButton("SIFT");
		bg1.add(rbSift);
		rbSurf = new JRadioButton("SURF");
		bg1.add(rbSurf);
		rbSurf.setSelected(true);

		ButtonGroup bg2 = new ButtonGroup();
		rbSquare = new JRadioButton("Square");
		bg2.add(rbSquare);
		rbCircle = new JRadioButton("Circle");
		bg2.add(rbCircle);
		rbCircle.setSelected(true);
		rbSquare.addActionListener(this);
		rbCircle.addActionListener(this);

		ButtonGroup bg3 = new ButtonGroup();
		rbFill = new JRadioButton("Fill");
		bg3.add(rbFill);
		rbBorder = new JRadioButton("Border");
		bg3.add(rbBorder);
		rbBorder.setSelected(true);
		rbBorder.addActionListener(this);
		rbFill.addActionListener(this);

		btCompute = new JButton("Compute");
		btCompute.addActionListener(this);
		btMatch = new JButton("Match");
		btMatch.addActionListener(this);
		mainPanel.add(GUIUtil.createLineBoxPanel(cbSynchroOpacity, Box.createHorizontalGlue(), rbSquare, rbCircle, Box.createHorizontalGlue(), rbFill, rbBorder, Box.createHorizontalGlue(), rbSurf, rbSift, Box.createHorizontalGlue(), btMatch, Box.createHorizontalStrut(10), btCompute), BorderLayout.PAGE_END);

		frame.setLocation(100, 100);
		frame.setVisible(true);
	}

	@Override
	public void zoomChanged(ImageViewer v) {
	}

}
