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
package name.herve.imagematch.db;

import plugins.nherve.toolbox.Algorithm;
import plugins.nherve.toolbox.image.db.ImageDatabaseIndexer;
import plugins.nherve.toolbox.image.db.IndexingConfiguration;
import plugins.nherve.toolbox.image.feature.DefaultSegmentableImage;

/**
 * @author Nicolas HERVE - n.herve@laposte.net
 */
public class SiftDescriptorsConfiguration extends Algorithm implements IndexingConfiguration<DefaultSegmentableImage> {

	@Override
	public void populate(ImageDatabaseIndexer<DefaultSegmentableImage> idxr) {
		SiftDetectorAndDescriptor d = new SiftDetectorAndDescriptor(isLogEnabled());
		idxr.addRegionFactory(getName(), d);
		idxr.addLocalDescriptor(getName(), getName(), d);
	}

	@Override
	public String getName() {
		return "Sift";
	}

}
