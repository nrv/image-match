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


import java.util.Map.Entry;

import plugins.nherve.toolbox.image.db.ImageDatabase;
import plugins.nherve.toolbox.image.db.QueryManager.Response;
import plugins.nherve.toolbox.image.db.QueryManager.ResponseUnit;
import plugins.nherve.toolbox.image.feature.SignatureDistance;
import plugins.nherve.toolbox.image.feature.signature.SignatureException;
import plugins.nherve.toolbox.image.feature.signature.VectorSignature;

/**
 * @author Nicolas HERVE - n.herve@laposte.net
 */
public class LSHIndex {
	private ImageDatabase db;
	private SignatureDistance<VectorSignature> distance;
	private LSHTables lshTables;
	private int nbLshQueries;
	private double probedPct;

	public LSHIndex(ImageDatabase db, SignatureDistance<VectorSignature> distance) {
		super();
		this.distance = distance;
		this.db = db;
		probedPct = 0;
		nbLshQueries = 0;
	}

	public Response externalKnnQuery(VectorSignature externalQuery, int k) throws SignatureException {
		return knnQuery(lshTables.h(externalQuery), externalQuery, k);
	}

	public double getGlobalProbedPct() {
		return probedPct / nbLshQueries;
	}

	public int getNbLshQueries() {
		return nbLshQueries;
	}

	public Response internalKnnQuery(long internalQuery, int k) throws SignatureException {
		BitsetSignature qi = (BitsetSignature) (db.get(internalQuery).getIndexed());
		VectorSignature q = db.get(internalQuery).getSig();

		return knnQuery(qi, q, k);
	}

	private Response knnQuery(BitsetSignature qi, VectorSignature q, int k) throws SignatureException {
		Response result = new Response();

		long probed = 0;

		for (Entry e : db) {
			BitsetSignature si = (BitsetSignature) (e.getIndexed());

			if (si != null) {
				if (lshTables.collision(qi, si)) {
					probed++;
					double dtq = distance.computeDistance(e.getSig(), q);
					if ((dtq < result.getCurrentMin()) || (result.size() < k)) {
						ResponseUnit ru = new ResponseUnit(e, dtq);
						result.add(ru);
						result.sortAndTruncate(k);
					}
				}
			}
		}

		nbLshQueries++;
		double pct = (100 * (double) probed) / db.size();
		probedPct += pct;

		result.sortAndTruncate(k);

		return result;
	}

	public void setLshTables(LSHTables lshTables) {
		this.lshTables = lshTables;
	}
}
