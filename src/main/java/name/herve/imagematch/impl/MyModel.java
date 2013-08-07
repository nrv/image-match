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

import java.awt.geom.AffineTransform;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Nicolas HERVE - n.herve@laposte.net
 */
public class MyModel implements Cloneable {
	private AffineTransform t;
	private float error;

	public MyModel() {
		super();
		t = new AffineTransform();
		error = Float.MAX_VALUE;
	}

	public MyPoint apply(MyPoint p) {
		float[] in = new float[2];
		float[] out = new float[2];
		in[0] = p.getX();
		in[1] = p.getY();
		t.transform(in, 0, out, 0, 1);
		return new MyPoint(out[0], out[1]);
	}

	public boolean betterThan(MyModel m) {
		if (error < 0) {
			return false;
		}
		return error < m.error;
	}

	@Override
	public MyModel clone() {
		MyModel n = new MyModel();
		n.t = (AffineTransform) t.clone();
		n.error = error;
		return n;
	}

	public boolean estimateOnTwoMatches(Collection<MyPointMatch> matches) {
		Iterator<MyPointMatch> it = matches.iterator();
		MyPointMatch m1 = it.next();
		MyPointMatch m2 = it.next();

		MyPoint m1p1 = m1.getP1();
		MyPoint m2p1 = m2.getP1();
		MyPoint m1p2t = m1.getP2t();
		MyPoint m2p2t = m2.getP2t();

		float x1 = m2p1.getX() - m1p1.getX();
		float y1 = m2p1.getY() - m1p1.getY();
		float x2 = m2p2t.getX() - m1p2t.getX();
		float y2 = m2p2t.getY() - m1p2t.getY();
		float l1 = (float) Math.sqrt(x1 * x1 + y1 * y1);
		float l2 = (float) Math.sqrt(x2 * x2 + y2 * y2);

		x1 /= l1;
		x2 /= l2;
		y1 /= l1;
		y2 /= l2;

		float cos = x1 * x2 + y1 * y2;
		float sin = x1 * y2 - y1 * x2;

		float tx = m1p2t.getX() - cos * m1p1.getX() + sin * m1p1.getY();
		float ty = m1p2t.getY() - sin * m1p1.getX() - cos * m1p1.getY();
		t.setTransform(cos, sin, -sin, cos, tx, ty);

		return true;
	}

	public float getError() {
		return error;
	}

	public void minimize(Collection<MyPointMatch> matches) {
		float xo1 = 0, yo1 = 0;
		float xo2 = 0, yo2 = 0;
		int length = matches.size();

		if (0 == length) {
			return;
		}

		for (MyPointMatch m : matches) {
			MyPoint p1 = m.getP1();
			MyPoint p2 = m.getP2t();

			xo1 += p1.getX();
			yo1 += p1.getY();
			xo2 += p2.getX();
			yo2 += p2.getY();
		}
		xo1 /= length;
		yo1 /= length;
		xo2 /= length;
		yo2 /= length;

		float dx = xo1 - xo2;
		float dy = yo1 - yo2;
		float sum1 = 0, sum2 = 0;
		float x1, y1, x2, y2;
		for (MyPointMatch m : matches) {
			MyPoint p1 = m.getP1();
			MyPoint p2 = m.getP2t();

			x1 = p1.getX() - xo1;
			y1 = p1.getY() - yo1;
			x2 = p2.getX() - xo2 + dx;
			y2 = p2.getY() - yo2 + dy;
			sum1 += x1 * y2 - y1 * x2;
			sum2 += x1 * x2 + y1 * y2;
		}
		float angle = (float) Math.atan2(-sum1, sum2);

		t.setToIdentity();
		t.rotate(-angle, xo2, yo2);
		t.translate(-dx, -dy);
	}

	public boolean testAndKeepGoodMatches(Collection<MyPointMatch> matches, Collection<MyPointMatch> kept, double epsilon, double min_inlier_ratio) {
		kept.clear();

		for (MyPointMatch m : matches) {
			m.applyModel(this);
			if (m.getDistance() < epsilon) {
				kept.add(m);
			}
		}

		float ir = (float) kept.size() / (float) matches.size();
		error = 1f - ir;
		if (error > 1f) {
			error = 1f;
		}
		if (error < 0f) {
			error = 0f;
		}
		return ir > min_inlier_ratio;
	}
}
