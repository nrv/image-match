/*
 * Copyright 2015 Nicolas HERVE.
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
public class MyRectangle {
	private float x1;
	private float y1;
	private float x2;
	private float y2;

	public MyRectangle(float x1, float y1, float x2, float y2) {
		super();
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}

	public MyRectangle(MyPoint p) {
		this(p, p);
	}

	public MyRectangle(MyPoint p1, MyPoint p2) {
		this(p1.getX(), p1.getY(), p2.getX(), p2.getY());
	}

	public MyRectangle(MyRectangle r) {
		this(r.x1, r.y1, r.x2, r.y2);
	}

	public void addPoint(MyPoint p) {
		x1 = Math.min(x1, p.getX());
		x2 = Math.max(x2, p.getX());
		y1 = Math.min(y1, p.getY());
		y2 = Math.max(y2, p.getY());
	}

	public void addRectangle(MyRectangle r) {
		x1 = Math.min(x1, r.x1);
		x2 = Math.max(x2, r.x2);
		y1 = Math.min(y1, r.y1);
		y2 = Math.max(y2, r.y2);
	}

	public float getHeight() {
		return y2 - y1;
	}

	public float getWidth() {
		return x2 - x1;
	}

	public float getX1() {
		return x1;
	}

	public float getX2() {
		return x2;
	}

	public float getY1() {
		return y1;
	}

	public float getY2() {
		return y2;
	}

	public void normalize(float w, float h) {
		x1 /= w;
		x2 /= w;
		y1 /= h;
		y2 /= h;
	}

	public void setX1(float x1) {
		this.x1 = x1;
	}

	public void setX2(float x2) {
		this.x2 = x2;
	}

	public void setY1(float y1) {
		this.y1 = y1;
	}

	public void setY2(float y2) {
		this.y2 = y2;
	}
}
