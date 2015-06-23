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

import org.apache.lucene.util.OpenBitSet;

import plugins.nherve.toolbox.image.feature.signature.SignatureException;
import plugins.nherve.toolbox.image.feature.signature.VectorSignature;

/**
 * @author Nicolas HERVE - n.herve@laposte.net
 */
public class LSHTables {
	public final static String VERSION = "LSH_v1.0.0";

	private int inputDim;
	private int k;
	private int L;
	private RandomProjection rp;

	public LSHTables(int k, int L, int inputDim) {
		super();
		this.k = k;
		this.L = L;
		this.inputDim = inputDim;
	}

	// TODO a optimiser
	public boolean collision(BitsetSignature s1, BitsetSignature s2) {
		OpenBitSet obs1 = s1.getBitSet();
		OpenBitSet obs2 = s2.getBitSet();

		for (int t = 0; t < L; t++) {
			boolean col = false;
			for (int d = 0; d < k; d++) {
				if (obs1.fastGet(d) != obs2.fastGet(d)) {
					break;
				}
				col = true;
			}
			if (col) {
				return true;
			}
		}
		return false;
	}

	public void generateProjection() {
		rp = new RandomProjection(k * L, inputDim);
		rp.generateProjection();
	}

	public int getInputDim() {
		return inputDim;
	}

	public int getK() {
		return k;
	}

	public int getL() {
		return L;
	}

	public RandomProjection getRp() {
		return rp;
	}

	// TODO a optimiser
	public BitsetSignature h(VectorSignature s) throws SignatureException {
		return rp.binarize(rp.project(s));
	}

	public void setRp(RandomProjection rp) {
		this.rp = rp;
	}
}
