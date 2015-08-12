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

/**
 * @author Nicolas HERVE - n.herve@laposte.net
 */
public class MyPointMatch {
	private int id1;
	private int id2;
	private MyPoint p1;
	private MyPoint p2;
	private MyPoint p1t;
	private MyPoint p2t;
	private double featureDistance;
	private float distance;
	private int group;
	
	public MyPointMatch(float x1, float y1, float x2, float y2) {
		super();
		
		p1 = new MyPoint(x1, y1);
		p2 = new MyPoint(x2, y2);
	}

	public MyPointMatch(MyFeature f1, MyFeature f2, double featureDistance) {
		super();
		if (f1 != null) {
			id1 = f1.getId();
			p1 = f1.getPoint();
			p1t = p1.clone();
		}
		if (f2 != null) {
			id2 = f2.getId();
			p2 = f2.getPoint();
			p2t = p2.clone();
		}
		this.featureDistance = featureDistance;
	}

	public void applyModel(MyModel m) {
		p1t = m.apply(p1);
		distance = p1t.distance(p2t);
	}

	public float getDistance() {
		return distance;
	}

	public double getFeatureDistance() {
		return featureDistance;
	}

	public int getGroup() {
		return group;
	}

	public MyPoint getP(boolean first) {
		return first ? p1 : p2;
	}

	public MyPoint getP1() {
		return p1;
	}

	public MyPoint getP1t() {
		return p1t;
	}

	public MyPoint getP2() {
		return p2;
	}

	public MyPoint getP2t() {
		return p2t;
	}

	public void setGroup(int group) {
		this.group = group;
	}

	public int getId1() {
		return id1;
	}

	public int getId2() {
		return id2;
	}

}
