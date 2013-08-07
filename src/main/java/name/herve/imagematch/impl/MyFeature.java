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

import mpi.cbg.fly.Feature;

import com.stromberglabs.jopensurf.SURFInterestPoint;

/**
 * @author Nicolas HERVE - n.herve@laposte.net
 */
public class MyFeature {
	private float[] desc;
	private float orientation;
	private float scale;
	private MyPoint point;

	public MyFeature(Feature f) {
		super();
		point = new MyPoint(f.location[0], f.location[1]);
		orientation = f.orientation;
		scale = f.scale;
		desc = f.descriptor;
	}

	public MyFeature(SURFInterestPoint f) {
		super();
		point = new MyPoint(f.getX(), f.getY());
		scale = f.getScale();
		orientation = f.getOrientation();
		desc = f.getDescriptor();
	}
	
	public MyFeature(SURFInterestPoint f, float ratio) {
		super();
		point = new MyPoint(f.getX() * ratio, f.getY() * ratio);
		scale = f.getScale();
		orientation = f.getOrientation();
		desc = f.getDescriptor();
	}

	public float[] getDesc() {
		return desc;
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

	public float getX() {
		return point.getX();
	}

	public float getY() {
		return point.getY();
	}

}
