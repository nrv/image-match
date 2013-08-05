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
package name.herve.imagematch.gui.viewer;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import name.herve.imagematch.MyFeature;
import name.herve.imagematch.MyPointMatch;
import plugins.nherve.toolbox.gui.GUIUtil;

/**
 * @author Nicolas HERVE - n.herve@laposte.net
 */
public class ImageViewerPanel extends JPanel implements ActionListener, ImageViewerListener, ChangeListener {
	private static final DecimalFormat ZOOM_DF = new DecimalFormat("000");
	private static final DecimalFormat POI_DF = new DecimalFormat("0000");
	private static final long serialVersionUID = 8789165530950106414L;

	private ImageViewer viewer;
	private Set<ImageViewerListener> listeners;
	private JButton btReset;
	private JLabel lbZoom;
	private JLabel lbPOI;
	private JSlider slOpacity;
	private String label;

	public ImageViewerPanel() {
		super();

		listeners = new HashSet<ImageViewerListener>();

		viewer = new ImageViewer();
		viewer.addImageViewerListener(this);

		btReset = new JButton("Reset");
		btReset.addActionListener(this);

		lbZoom = new JLabel();
		zoomChanged(viewer);

		lbPOI = new JLabel();
		poiChanged(viewer);

		slOpacity = new JSlider(0, 1000, 1000);
		slOpacity.addChangeListener(this);

		setLayout(new BorderLayout());
		add(viewer, BorderLayout.CENTER);
		add(GUIUtil.createLineBoxPanel(lbZoom, Box.createHorizontalGlue(), slOpacity, Box.createHorizontalGlue(), btReset, Box.createHorizontalGlue(), lbPOI), BorderLayout.PAGE_END);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();

		if (o == null) {
			return;
		}

		if (o instanceof JButton) {
			JButton b = (JButton) e.getSource();
			if (b == btReset) {
				viewer.resetZoom();
			}
		}
	}

	public boolean addImageViewerListener(ImageViewerListener e) {
		return listeners.add(e);
	}

	private void fireOpacityChangedEvent() {
		for (ImageViewerListener l : listeners) {
			l.opacityChanged(viewer);
		}
	}

	public List<MyFeature> getFeatures() {
		return viewer.getFeatures();
	}

	public BufferedImage getImage() {
		return viewer.getImage();
	}

	public String getLabel() {
		return label;
	}

	public float getOpacity() {
		return slOpacity.getValue() / 1000f;
	}

	public ImageViewer getViewer() {
		return viewer;
	}

	@Override
	public void opacityChanged(ImageViewer v) {

	}

	@Override
	public void poiChanged(ImageViewer v) {
		List<MyFeature> f = v.getFeatures();
		lbPOI.setText("[" + (f == null ? " ~~ " : POI_DF.format(f.size())) + "]");
	}

	public boolean removeImageViewerListener(ImageViewerListener o) {
		return listeners.remove(o);
	}

	public void setFeatures(List<MyFeature> features) {
		viewer.setFeatures(features);
	}

	public void setFill(boolean fill) {
		viewer.setFill(fill);
	}

	public void setImage(BufferedImage image, String label) {
		viewer.setImage(image);
		this.label = label;
	}

	public void setMatches(List<MyPointMatch> matches, boolean matchFirst) {
		viewer.setMatches(matches, matchFirst);
	}

	public void setOnlyMatches(boolean onlyMatches) {
		viewer.setOnlyMatches(onlyMatches);
	}

	public void setOpacity(float o) {
		slOpacity.setValue((int) (o * 1000));
	}

	public void setUseCircle(boolean useCircle) {
		viewer.setUseCircle(useCircle);
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		Object o = e.getSource();

		if (o == null) {
			return;
		}

		if (o instanceof JSlider) {
			JSlider s = (JSlider) e.getSource();

			if (s == slOpacity) {
				viewer.setOpacity(getOpacity());
				fireOpacityChangedEvent();
			}
		}
	}

	@Override
	public void zoomChanged(ImageViewer v) {
		lbZoom.setText("[" + ZOOM_DF.format(v.getZoom() * 100f) + " %]");
	}
}
