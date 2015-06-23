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

import name.herve.imagematch.impl.MyFeature;

import org.apache.lucene.util.OpenBitSet;

import plugins.nherve.toolbox.image.feature.signature.DenseVectorSignature;
import plugins.nherve.toolbox.image.feature.signature.SignatureException;
import plugins.nherve.toolbox.image.feature.signature.SparseVectorSignature;
import plugins.nherve.toolbox.image.feature.signature.VectorSignature;

/**
 * @author Nicolas HERVE - n.herve@laposte.net
 */
public class RandomProjection {
	public final static String VERSION = "RP_v1.0.0";

	private final int inputDim;
	private final int nb;
	private double[][] projection;

	public RandomProjection(int nb, int inputDim) {
		super();
		this.nb = nb;
		this.inputDim = inputDim;
	}

	public BitsetSignature binarize(VectorSignature s) throws SignatureException {
		if (s.getSize() != nb) {
			throw new SignatureException("Bad VectorSignature dim !");
		}

		OpenBitSet bitSet = new OpenBitSet(nb);
		for (int t = 0; t < nb; t++) {
			if (s.get(t) >= 0) {
				bitSet.set(t);
			}
		}

		return new BitsetSignature(nb, bitSet);
	}

	public void generateProjection() {
		projection = new double[nb][inputDim];
		for (int t = 0; t < nb; t++) {
			double norm = 0;
			double v;
			for (int d = 0; d < inputDim; d++) {
				v = Math.sqrt(-2.0d * Math.log(Math.random())) * Math.sin(2.0d * Math.PI * Math.random());
				projection[t][d] = v;
				norm += v * v;
			}

			norm = Math.sqrt(norm);

			for (int d = 0; d < inputDim; d++) {
				projection[t][d] /= norm;
			}
		}
	}

	public int getInputDim() {
		return inputDim;
	}

	public int getNb() {
		return nb;
	}

	public double[][] getProjection() {
		return projection;
	}

	public VectorSignature project(VectorSignature s) throws SignatureException {
		DenseVectorSignature proj = new DenseVectorSignature(nb);

		if (s instanceof SparseVectorSignature) {
			for (int t = 0; t < nb; t++) {
				double val = 0;
				double[] projectionVector = projection[t];
				SparseVectorSignature ss = (SparseVectorSignature) s;
				for (int idx : ss) {
					val += ss.get(idx) * projectionVector[idx];
				}
				proj.set(t, val);
			}
		} else if (s instanceof DenseVectorSignature) {
			for (int t = 0; t < nb; t++) {
				double val = 0;
				double[] projectionVector = projection[t];
				double[] data = ((DenseVectorSignature) s).getData();
				for (int d = 0; d < projectionVector.length; d++) {
					val += data[d] * projectionVector[d];
				}
				proj.set(t, val);
			}
		} else if (s instanceof MyFeature) {
			for (int t = 0; t < nb; t++) {
				double val = 0;
				double[] projectionVector = projection[t];
				float[] desc = ((MyFeature) s).getDesc();
				for (int d = 0; d < projectionVector.length; d++) {
					val += desc[d] * projectionVector[d];
				}
				proj.set(t, val);
			}
		}else {
			throw new SignatureException("Hum, what !");
		}

		return proj;
	}

	public void setProjection(double[][] projection) {
		this.projection = projection;
	}
}
