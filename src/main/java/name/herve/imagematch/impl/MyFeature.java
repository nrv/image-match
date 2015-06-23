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
package name.herve.imagematch.impl;

import java.util.Iterator;

import mpi.cbg.fly.Feature;
import plugins.nherve.toolbox.image.feature.signature.SignatureException;
import plugins.nherve.toolbox.image.feature.signature.VectorSignature;

import com.stromberglabs.jopensurf.SURFInterestPoint;

/**
 * @author Nicolas HERVE - n.herve@laposte.net
 */
public class MyFeature extends VectorSignature {
	private class MyFeatureIterator implements Iterator<Integer> {
		private int d;

		public MyFeatureIterator() {
			super();
			d = 0;
		}

		@Override
		public boolean hasNext() {
			while (d < desc.length) {
				if (desc[d] != 0) {
					return true;
				}
				d++;
			}

			return false;
		}

		@Override
		public Integer next() {
			int r = d;
			d++;
			return r;
		}

		@Override
		public void remove() {
			// not used
		}
	}

	private int id;
	private float[] desc;
	private float orientation;
	private float scale;
	private MyPoint point;

	public MyFeature() {
		super();
	}

	public MyFeature(Feature f, int id) {
		super();
		this.id = id;
		point = new MyPoint(f.location[0], f.location[1]);
		orientation = f.orientation;
		scale = f.scale;
		desc = f.descriptor;
	}

	public MyFeature(SURFInterestPoint f, float ratio, int id) {
		super();
		this.id = id;
		point = new MyPoint(f.getX() * ratio, f.getY() * ratio);
		scale = f.getScale();
		orientation = f.getOrientation();
		desc = f.getDescriptor();
	}

	public MyFeature(SURFInterestPoint f, int id) {
		super();
		this.id = id;
		point = new MyPoint(f.getX(), f.getY());
		scale = f.getScale();
		orientation = f.getOrientation();
		desc = f.getDescriptor();
	}

	@Override
	public VectorSignature clone() throws CloneNotSupportedException {
		throw new RuntimeException("Not implemented yet !");
	}

	@Override
	public void concat(VectorSignature other) throws SignatureException {
		throw new RuntimeException("Not implemented yet !");
	}

	@Override
	public double get(int idx) throws SignatureException {
		return desc[idx];
	}

	public float[] getDesc() {
		return desc;
	}

	public int getId() {
		return id;
	}

	@Override
	public int getNonZeroBins() throws SignatureException {
		int res = 0;
		for (int d = 0; d < getSize(); d++) {
			if (get(d) != 0) {
				res++;
			}
		}
		return res;
	}

	public float getOrientation() {
		return orientation;
	}

	public MyPoint getPoint() {
		return point;
	}

	public float getScale() {
		return scale;
	}

	@Override
	public int getSize() {
		return desc == null ? 0 : desc.length;
	}

	public float getX() {
		return point.getX();
	}

	public float getY() {
		return point.getY();
	}

	@Override
	public Iterator<Integer> iterator() {
		return new MyFeatureIterator();
	}

	@Override
	public void set(int idx, double val) throws SignatureException {
		desc[idx] = (float) val;
	}

	protected void setDesc(float[] desc) {
		this.desc = desc;
	}

	protected void setId(int id) {
		this.id = id;
	}

	protected void setOrientation(float orientation) {
		this.orientation = orientation;
	}

	protected void setPoint(MyPoint point) {
		this.point = point;
	}

	protected void setScale(float scale) {
		this.scale = scale;
	}

	@Override
	public void setSize(int s) {
		// ignored
	}

}
