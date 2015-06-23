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

import plugins.nherve.toolbox.image.feature.signature.L1Distance;
import plugins.nherve.toolbox.image.feature.signature.SignatureException;
import plugins.nherve.toolbox.image.feature.signature.VectorSignature;

/**
 * @author Nicolas HERVE - n.herve@laposte.net
 */
public class BitsetAwareL1Distance extends L1Distance {

	@Override
	public double computeDistance(VectorSignature vs1, VectorSignature vs2) throws SignatureException {
		if ((vs1 instanceof BitsetSignature) && (vs2 instanceof BitsetSignature)) {
			return (double) xorCount((BitsetSignature) vs1, (BitsetSignature) vs2) / (double) vs1.getSize();
		} else {
			return super.computeDistance(vs1, vs2);
		}
	}

	public boolean intersects(BitsetSignature s1, BitsetSignature s2) {
		return s1.getBitSet().intersects(s2.getBitSet());
	}

	public long xorCount(BitsetSignature s1, BitsetSignature s2) {
		return OpenBitSet.xorCount(s1.getBitSet(), s2.getBitSet());
	}

}
