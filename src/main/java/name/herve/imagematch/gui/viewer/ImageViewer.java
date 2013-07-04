package name.herve.imagematch.gui.viewer;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.event.MouseInputListener;

import name.herve.imagematch.MyFeature;
import plugins.nherve.toolbox.image.toolboxes.ImageTools;

public class ImageViewer extends JComponent implements MouseWheelListener, MouseInputListener, ComponentListener {
	private static final long serialVersionUID = 5321795190135695790L;

	private Set<ImageViewerListener> listeners;
	private BufferedImage image;
	private List<MyFeature> features;
	private BufferedImage cache;
	private BufferedImage featuresCache;
	private int cacheWidth;
	private int cacheHeight;
	private int cacheXOffset;
	private int cacheYOffset;
	private float zoom;
	private float opacity;
	private boolean needCacheRedraw;
	private boolean needFeatureCacheRedraw;
	private int lastX;
	private int lastY;

	public ImageViewer() {
		super();
		setBorder(BorderFactory.createLineBorder(Color.RED));
		addMouseWheelListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		addComponentListener(this);
		listeners = new HashSet<ImageViewerListener>();
		zoom = 1;
		opacity = 1f;
	}

	public boolean addImageViewerListener(ImageViewerListener e) {
		return listeners.add(e);
	}

	@Override
	public void componentHidden(ComponentEvent e) {
	}

	@Override
	public void componentMoved(ComponentEvent e) {
	}

	@Override
	public void componentResized(ComponentEvent e) {
		updateSize();
	}

	@Override
	public void componentShown(ComponentEvent e) {
	}

	private void firePOIChangedEvent() {
		for (ImageViewerListener l : listeners) {
			l.poiChanged(this);
		}
	}

	private void fireZoomChangedEvent() {
		for (ImageViewerListener l : listeners) {
			l.zoomChanged(this);
		}
	}

	public List<MyFeature> getFeatures() {
		return features;
	}

	public BufferedImage getImage() {
		return image;
	}

	public float getZoom() {
		return zoom;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (!e.isConsumed()) {
			int deltaX = e.getX() - lastX;
			lastX = e.getX();
			int deltaY = e.getY() - lastY;
			lastY = e.getY();

			cacheXOffset += deltaX;
			cacheYOffset += deltaY;

			e.consume();
			repaint();
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (!e.isConsumed()) {
			lastX = e.getX();
			lastY = e.getY();
			e.consume();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (!e.isConsumed()) {
			zoom += (e.getWheelRotation() / 10f);
			if (zoom < 0.1f) {
				zoom = 0.1f;
			}
			if (zoom > 2f) {
				zoom = 2f;
			}

			e.consume();
			updateSize();
			fireZoomChangedEvent();
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		if (needCacheRedraw && (image != null)) {
			cache = ImageTools.resize(image, cacheWidth, cacheHeight);
			needCacheRedraw = false;
		}

		if (needFeatureCacheRedraw && (features != null)) {
			featuresCache = new BufferedImage(cacheWidth, cacheHeight, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = (Graphics2D) featuresCache.getGraphics();
			g2.setColor(Color.RED);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			for (MyFeature p : features) {
				g2.fillRect((int) (p.getX() * zoom) - 4, (int) (p.getY() * zoom) - 4, 9, 9);
			}
			g2.dispose();
			needFeatureCacheRedraw = false;
		}

		if (cache != null) {
			Graphics2D g2 = (Graphics2D) g;
			int xo = cacheXOffset + ((getWidth() - cacheWidth) / 2);
			int yo = cacheYOffset + ((getHeight() - cacheHeight) / 2);
			g2.drawImage(cache, xo, yo, null);

			if (featuresCache != null) {
				Composite bck = g2.getComposite();
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
				g2.drawImage(featuresCache, xo, yo, null);
				g2.setComposite(bck);
			}

			g2.dispose();
		}
	}

	public boolean removeImageViewerListener(ImageViewerListener o) {
		return listeners.remove(o);
	}

	public void resetZoom() {
		if (image != null) {
			float wr = (float) getWidth() / (float) image.getWidth();
			float hr = (float) getHeight() / (float) image.getHeight();
			zoom = Math.min(wr, hr);
		} else {
			zoom = 1;
		}
		cacheXOffset = 0;
		cacheYOffset = 0;
		updateSize();
		fireZoomChangedEvent();
	}

	public void setFeatures(List<MyFeature> features) {
		this.features = features;
		featuresCache = null;
		needFeatureCacheRedraw = true;
		firePOIChangedEvent();
		repaint();
	}

	public void setImage(BufferedImage image) {
		this.image = image;
		resetZoom();
	}

	public void setOpacity(float opacity) {
		this.opacity = opacity;
		repaint();
	}

	private void updateSize() {
		if (image != null) {
			cacheWidth = (int) (zoom * image.getWidth());
			cacheHeight = (int) (zoom * image.getHeight());
		}
		needCacheRedraw = true;
		needFeatureCacheRedraw = true;
		repaint();
	}
}
