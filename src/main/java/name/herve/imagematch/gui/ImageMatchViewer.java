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
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.SwingWorker;

import name.herve.imagematch.ImageMatch;
import name.herve.imagematch.MyFeature;
import name.herve.imagematch.gui.util.FileChooserWithTextField;
import name.herve.imagematch.gui.viewer.ImageViewer;
import name.herve.imagematch.gui.viewer.ImageViewerListener;
import name.herve.imagematch.gui.viewer.ImageViewerPanel;
import plugins.nherve.toolbox.gui.GUIUtil;

public class ImageMatchViewer implements ActionListener, ImageViewerListener, ItemListener {
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
				v.setImage(get());
				v.setFeatures(null);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
	}

	private class POIComputer extends SwingWorker<List<MyFeature>, Object> {
		private ImageViewerPanel v;

		public POIComputer(ImageViewerPanel v) {
			super();
			this.v = v;
		}

		@Override
		protected List<MyFeature> doInBackground() throws Exception {
			try {
				return ImageMatch.processSURF(v.getImage());
			} catch (IOException e) {
				System.err.println(e.getMessage());
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

	private JButton btLoad;
	private JButton btCompute;
	private JCheckBox cbSynchroOpacity;

	public ImageMatchViewer() {
		super();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();

		if (o == null) {
			return;
		}

		if (o instanceof JButton) {
			JButton b = (JButton) e.getSource();
			if (b == btLoad) {
				loadImages();
			} else if (b == btCompute) {
				computePOI();
			}
		}

	}

	private void computePOI() {
		if (iv1.getImage() != null) {
			new POIComputer(iv1).execute();
		}
		if (iv2.getImage() != null) {
			new POIComputer(iv2).execute();
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

	private void loadImages() {
		new ImageLoader(fc1.getChoosenFile(), iv1).execute();
		new ImageLoader(fc2.getChoosenFile(), iv2).execute();
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
		JFrame.setDefaultLookAndFeelDecorated(true);
		JFrame frame = new JFrame("Image match viewer");
		frame.setSize(1200, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container mainPanel = frame.getContentPane();
		mainPanel.setLayout(new BorderLayout());

		fc1 = new FileChooserWithTextField("Image 1", defaultLocation);
		fc2 = new FileChooserWithTextField("Image 2", defaultLocation);
		mainPanel.add(GUIUtil.createLineBoxPanel(fc1, Box.createHorizontalStrut(10), fc2), BorderLayout.PAGE_START);

		iv1 = new ImageViewerPanel();
		iv1.addImageViewerListener(this);
		iv2 = new ImageViewerPanel();
		iv2.addImageViewerListener(this);
		mainPanel.add(GUIUtil.createLineBoxPanel(iv1, Box.createHorizontalStrut(10), iv2), BorderLayout.CENTER);

		cbSynchroOpacity = new JCheckBox("Synchro");
		cbSynchroOpacity.setSelected(false);
		cbSynchroOpacity.addItemListener(this);

		btLoad = new JButton("Load");
		btLoad.addActionListener(this);
		btCompute = new JButton("Compute");
		btCompute.addActionListener(this);
		mainPanel.add(GUIUtil.createLineBoxPanel(cbSynchroOpacity, Box.createHorizontalGlue(), btLoad, Box.createHorizontalStrut(10), btCompute), BorderLayout.PAGE_END);

		frame.setLocation(100, 100);
		frame.setVisible(true);
	}

	@Override
	public void zoomChanged(ImageViewer v) {
	}

}
