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


import plugins.nherve.toolbox.image.db.ImageDatabase;
import plugins.nherve.toolbox.image.db.ImageEntry;
import plugins.nherve.toolbox.image.db.QueryManager;
import plugins.nherve.toolbox.image.feature.DefaultSegmentableImage;
import plugins.nherve.toolbox.image.feature.SignatureDistance;
import plugins.nherve.toolbox.image.feature.signature.SignatureException;
import plugins.nherve.toolbox.image.feature.signature.VectorSignature;

/**
 * @author Nicolas HERVE - n.herve@laposte.net
 */
public class LSHIndex extends QueryManager<DefaultSegmentableImage> {
	private ImageDatabase<DefaultSegmentableImage> db;
	private SignatureDistance<VectorSignature> distance;
	private LSHTables lshTables;
	private int nbLshQueries;
	private double probedPct;

	public LSHIndex(ImageDatabase<DefaultSegmentableImage> db, SignatureDistance<VectorSignature> distance) {
		super(true);
		this.distance = distance;
		this.db = db;
		probedPct = 0;
		nbLshQueries = 0;
	}

//	public QueryManager<DefaultSegmentableImage>.Response externalKnnQuery(VectorSignature externalQuery, int k) throws SignatureException {
//		return knnQuery(lshTables.h(externalQuery), externalQuery, k);
//	}

	public double getGlobalProbedPct() {
		return probedPct / nbLshQueries;
	}

	public int getNbLshQueries() {
		return nbLshQueries;
	}

//	public QueryManager<DefaultSegmentableImage>.Response internalKnnQuery(long internalQuery, int k) throws SignatureException {
//		BitsetSignature qi = (BitsetSignature) (db.get(internalQuery).getIndexed());
//		VectorSignature q = db.get(internalQuery).getSig();
//
//		return knnQuery(qi, q, k);
//	}

//	private QueryManager<DefaultSegmentableImage>.Response knnQuery(BitsetSignature qi, VectorSignature q, int k) throws SignatureException {
//		QueryManager<DefaultSegmentableImage>.Response result = new Response("");
//
//		long probed = 0;
//
//		for (ImageEntry<DefaultSegmentableImage> e : db) {
//			BitsetSignature si = (BitsetSignature) (e.getIndexed());
//
//			if (si != null) {
//				if (lshTables.collision(qi, si)) {
//					probed++;
//					double dtq = distance.computeDistance(e.getSig(), q);
//					if ((dtq < result.getCurrentMin()) || (result.size() < k)) {
//						ResponseUnit ru = new ResponseUnit();
//						ru.setDistanceToQuery(dtq);
//						ru.setEntry(e);
//						result.add(ru);
//						result.sortAndTruncate(k);
//					}
//				}
//			}
//		}
//
//		nbLshQueries++;
//		double pct = (100 * (double) probed) / db.size();
//		probedPct += pct;
//
//		result.sortAndTruncate(k);
//
//		return result;
//	}

	public void setLshTables(LSHTables lshTables) {
		this.lshTables = lshTables;
	}
}
