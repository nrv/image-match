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
public class MyPoint implements Cloneable {
	private float x;

	private float y;

	public MyPoint() {
		super();
	}

	public MyPoint(float x, float y) {
		this();
		this.x = x;
		this.y = y;
	}

	@Override
	public MyPoint clone() {
		return new MyPoint(x, y);
	}

	public float distance(MyPoint other) {
		float dx = x - other.x;
		float dy = y - other.y;
		return (float) Math.sqrt(dx * dx + dy * dy);
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public void setX(float x) {
		this.x = x;
	}

	public void setY(float y) {
		this.y = y;
	}
}
