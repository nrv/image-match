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
import plugins.nherve.toolbox.gui.GUIUtil;

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

	public void setImage(BufferedImage image) {
		viewer.setImage(image);
	}

	public void setOpacity(float o) {
		slOpacity.setValue((int) (o * 1000));
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
