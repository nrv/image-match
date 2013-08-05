/*
 * Copyright 2013 Nicolas HERVE.
 * 
 * This file is part of image-match
 * 
 * image-match is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * image-match is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with image-match. If not, see <http://www.gnu.org/licenses/>.
 */
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import plugins.nherve.toolbox.image.DifferentColorsMap;

/**
 * @author Nicolas HERVE - n.herve@laposte.net
 */
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
			if (v1.getFeatures() != null && v2.getFeatures() != null) {
				List<MyPointMatch> m = ImageMatch.findMatches(v1.getFeatures(), v2.getFeatures());
				log("Matches computed [" + m.size() + "]");
				if (cbIterativeRansac.isSelected()) {
					m = ImageMatch.iterativeRansac(m);
				} else {
					m = ImageMatch.ransac(m);
				}
				log("RANSAC computed [" + m.size() + "]");
				return m;
			}
			return null;
		}

		@Override
		protected void done() {
			try {
				List<MyPointMatch> matches = get();
				
				Set<Integer> groups = new HashSet<Integer>();
				for (MyPointMatch pm : matches) {
					groups.add(pm.getGroup());
				}
				
				DifferentColorsMap dcm = new DifferentColorsMap(groups.size());
				
				v1.setMatches(matches, dcm, true);
				v2.setMatches(matches, dcm, false);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
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
		String defaultLocation1 = null;
		String defaultLocation2 = null;
		if (args.length == 1) {
			defaultLocation1 = args[0];
			defaultLocation2 = args[0];
		} else if (args.length == 2) {
			defaultLocation1 = args[0];
			defaultLocation2 = args[1];
		}
		application.startInterface(defaultLocation1, defaultLocation2);
	}

	private FileChooserWithTextField fc1;
	private FileChooserWithTextField fc2;
	private ImageViewerPanel iv1;
	private ImageViewerPanel iv2;

	private JButton btLoad;
	private JButton btCompute;
	private JButton btMatch;
	private JCheckBox cbSynchroOpacity;
	private JCheckBox cbOnlyMatches;
	private JCheckBox cbIterativeRansac;
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
			if (b == rbCircle || b == rbSquare) {
				iv1.setUseCircle(rbCircle.isSelected());
				iv2.setUseCircle(rbCircle.isSelected());
			} else if (b == rbFill || b == rbBorder) {
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
			} else if (b == btLoad) {
				new ImageLoader(fc1.getChoosenFile(), iv1).execute();
				new ImageLoader(fc2.getChoosenFile(), iv2).execute();
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

			if (c == cbOnlyMatches) {
				iv1.setOnlyMatches(cbOnlyMatches.isSelected());
				iv2.setOnlyMatches(cbOnlyMatches.isSelected());
				iv1.repaint();
				iv2.repaint();
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

	public void startInterface(String defaultLocation1, String defaultLocation2) {
		// JFrame.setDefaultLookAndFeelDecorated(true);
		JFrame frame = new JFrame("Image match viewer");
		frame.setSize(1200, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container mainPanel = frame.getContentPane();
		mainPanel.setLayout(new BorderLayout());

		fc1 = new FileChooserWithTextField("Image 1", defaultLocation1);
		fc1.addFileChooserListener(this);
		fc2 = new FileChooserWithTextField("Image 2", defaultLocation2);
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

		cbOnlyMatches = new JCheckBox("Matches");
		cbOnlyMatches.setSelected(false);
		cbOnlyMatches.addItemListener(this);
		
		cbIterativeRansac = new JCheckBox("Iterative");
		cbIterativeRansac.setSelected(false);

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
		btLoad = new JButton("Load");
		btLoad.addActionListener(this);
		btMatch = new JButton("Match");
		btMatch.addActionListener(this);
		mainPanel.add(GUIUtil.createLineBoxPanel(cbSynchroOpacity, Box.createHorizontalGlue(), cbOnlyMatches, Box.createHorizontalStrut(10), rbSquare, rbCircle, Box.createHorizontalGlue(), rbFill, rbBorder, Box.createHorizontalGlue(), rbSurf, rbSift, Box.createHorizontalGlue(), cbIterativeRansac, btMatch, Box.createHorizontalStrut(10), btCompute, Box.createHorizontalStrut(10), btLoad), BorderLayout.PAGE_END);

		frame.setLocation(100, 100);
		frame.setVisible(true);
	}

	@Override
	public void zoomChanged(ImageViewer v) {
	}

}
