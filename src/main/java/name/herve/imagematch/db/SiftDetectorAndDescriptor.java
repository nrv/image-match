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
package name.herve.imagematch.db;

import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import name.herve.imagematch.ImageMatchHelper;
import name.herve.imagematch.impl.MyFeature;
import plugins.nherve.toolbox.image.feature.DefaultSegmentableImage;
import plugins.nherve.toolbox.image.feature.Segmentable;
import plugins.nherve.toolbox.image.feature.SupportRegion;
import plugins.nherve.toolbox.image.feature.SupportRegionFactory;
import plugins.nherve.toolbox.image.feature.descriptor.DefaultDescriptorImpl;
import plugins.nherve.toolbox.image.feature.descriptor.LocalDescriptor;
import plugins.nherve.toolbox.image.feature.region.Pixel;
import plugins.nherve.toolbox.image.feature.region.SupportRegionException;
import plugins.nherve.toolbox.image.feature.signature.DefaultVectorSignature;
import plugins.nherve.toolbox.image.feature.signature.SignatureException;
import plugins.nherve.toolbox.image.feature.signature.VectorSignature;

/**
 * @author Nicolas HERVE - n.herve@laposte.net
 */
public class SiftDetectorAndDescriptor extends DefaultDescriptorImpl<DefaultSegmentableImage, DefaultVectorSignature> implements LocalDescriptor<DefaultSegmentableImage, DefaultVectorSignature, Pixel>, SupportRegionFactory<Pixel> {
	private Map<BufferedImage, Map<Pixel, MyFeature>> cache;

	public SiftDetectorAndDescriptor(boolean display) {
		super(display);

		cache = new HashMap<BufferedImage, Map<Pixel, MyFeature>>();
	}

	@Override
	public boolean needToLoadSegmentable() {
		return true;
	}

	@Override
	public void preProcess(DefaultSegmentableImage img) throws SignatureException {
		BufferedImage bi = img.getImage();

		if (!cache.containsKey(bi)) {
			try {
				ImageMatchHelper algo = new ImageMatchHelper();
				List<MyFeature> points = algo.processSIFT(bi);

				Map<Pixel, MyFeature> lc = new HashMap<Pixel, MyFeature>();
				for (MyFeature f : points) {
					lc.put(f.getPoint(), f);
				}
				
				synchronized (cache) {
					cache.put(bi, lc);
				}
			} catch (IOException e) {
				throw new SignatureException(e);
			}
		}
	}

	@Override
	public void postProcess(DefaultSegmentableImage img) throws SignatureException {
		synchronized (cache) {
			cache.remove(img.getImage());
		}
	}

	@Override
	public List<Pixel> extractRegions(Segmentable img) throws SupportRegionException {
		try {
			preProcess((DefaultSegmentableImage)img);
		} catch (SignatureException e) {
			throw new SupportRegionException(e);
		}
		return new ArrayList<Pixel>(cache.get(((DefaultSegmentableImage)img).getImage()).keySet());
	}

	@Override
	public DefaultVectorSignature extractLocalSignature(DefaultSegmentableImage img, SupportRegion<Pixel> reg) throws SignatureException {
		return cache.get(img.getImage()).get((Pixel) reg);
	}

	@Override
	public DefaultVectorSignature extractLocalSignature(DefaultSegmentableImage img, Shape shp) throws SignatureException {
		throw new SignatureException("Not yet implemented");
	}

	@Override
	public int getSignatureSize() {
		return 128;
	}

	@Override
	public String toString() {
		return "SiftDetectorAndDescriptor";
	}

}
