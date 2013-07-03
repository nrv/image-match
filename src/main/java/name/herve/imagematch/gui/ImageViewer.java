package name.herve.imagematch.gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import plugins.nherve.toolbox.image.toolboxes.ImageTools;

public class ImageViewer extends JComponent {
	private BufferedImage image;
	private BufferedImage cache;
	private boolean needCacheRedraw;
	
	@Override
	public void paint(Graphics g) {
		if (needCacheRedraw && (image != null)) {
			if ((image.getWidth() <= getWidth()) && (image.getHeight() <= getHeight())) {
				cache = image;
			} else {
				cache = ImageTools.resize(image, getWidth(), getHeight());
			}
			needCacheRedraw = false;
		}

		if (cache != null) {
			Graphics2D g2 = (Graphics2D) g;
			g2.drawImage(cache, (getWidth() - cache.getWidth()) / 2, (getHeight() - cache.getHeight()) / 2, null);
		}
	}
}
