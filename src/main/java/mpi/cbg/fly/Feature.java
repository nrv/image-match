package mpi.cbg.fly;

/**
 * <p>Title: Feature</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * License: GPL
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * @author Stephan Preibisch <preibisch@mpi-cbg.de> and Stephan Saalfeld <saalfeld@mpi-cbg.de>
 * @version 0.1b
 */

public class Feature implements Comparable< Feature >
{
	public float scale;
	public float orientation;
	public float[] location;
	public float[] descriptor;

	public Feature() {}
	
	public Feature( float s, float o, float[] l, float[] d )
	{
		scale = s;
		orientation = o;
		location = l;
		descriptor = d;
	}

	/**
	 * comparator for making Features sortable
	 * please note, that the comparator returns -1 for
	 * this.scale &gt; o.scale, to sort the features in a descending order  
	 */
	public int compareTo( Feature f )
	{
		return scale < f.scale ? 1 : scale == f.scale ? 0 : -1;
	}
	
	public float descriptorDistance( Feature f )
	{
		float d = 0;
		for ( int i = 0; i < descriptor.length; ++i )
		{
			float a = descriptor[ i ] - f.descriptor[ i ];
			d += a * a;
		}
		return ( float )Math.sqrt( d );
	}
}

