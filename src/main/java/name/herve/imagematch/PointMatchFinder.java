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
package name.herve.imagematch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import name.herve.imagematch.impl.MyFeature;
import name.herve.imagematch.impl.MyPointMatch;
import plugins.nherve.toolbox.image.feature.SignatureDistance;
import plugins.nherve.toolbox.image.feature.signature.SignatureException;
import plugins.nherve.toolbox.image.feature.signature.VectorSignature;

/**
 * @author Nicolas HERVE - n.herve@laposte.net
 */
public abstract class PointMatchFinder {
	private SignatureDistance<VectorSignature> distance;
	private List<MyFeature> p1;
	private List<MyFeature> p2;
	
	private Map<String, Object> parameters;

	public PointMatchFinder() {
		super();
		parameters = new HashMap<String, Object>();
	}

	protected SignatureDistance<VectorSignature> getDistance() {
		return distance;
	}

	protected List<MyFeature> getP1() {
		return p1;
	}

	protected List<MyFeature> getP2() {
		return p2;
	}

	public Object getParameter(String key) {
		return parameters.get(key);
	}

	public void setDistance(SignatureDistance<VectorSignature> distance) {
		this.distance = distance;
	}

	public void setP1(List<MyFeature> p1) {
		this.p1 = p1;
	}

	public void setP2(List<MyFeature> p2) {
		this.p2 = p2;
	}

	public void setParameter(String key, Object value) {
		parameters.put(key, value);
	}

	public abstract List<MyPointMatch> work() throws SignatureException;
}
