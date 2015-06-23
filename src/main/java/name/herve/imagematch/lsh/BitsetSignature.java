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

package name.herve.imagematch.lsh;

import java.text.DecimalFormat;
import java.util.Iterator;

import org.apache.lucene.util.OpenBitSet;

import plugins.nherve.toolbox.image.feature.signature.SignatureException;
import plugins.nherve.toolbox.image.feature.signature.VectorSignature;

/**
 * @author Nicolas HERVE - n.herve@laposte.net
 */
public class BitsetSignature extends VectorSignature {
	protected final static DecimalFormat bdf = new DecimalFormat("0");

	private OpenBitSet bitSet;
	private int size;

	public BitsetSignature(int sz) {
		super();
		size = sz;
		bitSet = new OpenBitSet(sz);
	}

	public BitsetSignature(int sz, OpenBitSet bs) {
		super();
		size = sz;
		bitSet = bs;
	}

	@Override
	public BitsetSignature clone() throws CloneNotSupportedException {
		BitsetSignature ns = new BitsetSignature(getSize());
		ns.setBitSet((OpenBitSet) bitSet.clone());
		return ns;
	}

	@Override
	public void concat(VectorSignature other) throws SignatureException {
		throw new SignatureException("not implemented");
	}

	@Override
	public double get(int idx) throws SignatureException {
		return bitSet.getBit(idx);
	}

	public OpenBitSet getBitSet() {
		return bitSet;
	}

	@Override
	public int getNonZeroBins() throws SignatureException {
		return (int) bitSet.cardinality();
	}

	@Override
	public int getSize() {
		return size;
	}

	@Override
	public Iterator<Integer> iterator() {
		return null;
	}

	@Override
	public void set(int idx, double val) throws SignatureException {
		if (val > 0) {
			bitSet.fastSet(idx);
		} else {
			bitSet.fastClear(idx);
		}
	}

	public void setBitSet(OpenBitSet bitSet) {
		this.bitSet = bitSet;
	}

	@Override
	public void setSize(int s) {
		// ignored
	}

	@Override
	public String toString() {
		try {
			String str = getClass().getSimpleName() + "(" + getSize() + " - " + bdf.format(sum()) + "/" + getNonZeroBins() + ")[";
			boolean first = true;
			for (int d = 0; d < getSize(); d++) {
				if (first) {
					first = false;
				} else {
					str += " ";
				}
				str += bdf.format(get(d));
			}
			str += "]";
			return str;
		} catch (SignatureException e) {
			return "SignatureException : " + e.getMessage();
		}
	}

}
