package mpi.cbg.fly;

/**
 * <p>Title: Sift</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
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
 * @author Jonathan Odul
 * @link http://www.jidul.com
 * @version 0.1
 */

import java.util.Vector;
import java.util.List;
import java.util.Collections;

import java.awt.Image;

public class SIFT
{
    // steps
    private static int steps = 5;
    public static int steps() { return steps; }
    public static void set_steps(int s) { steps=s; }

    // initial sigma
    private static float initial_sigma = 1.6f;
    public static float initial_sigma() { return initial_sigma; }
    public static void set_initial_sigma(float is) { initial_sigma=is; }

    // feature descriptor size
    private static int fdsize = 4;
    public static int fdsize() { return fdsize; }
    public static void set_fdsize(int fs) { fdsize=fs; }

    // feature descriptor orientation bins
    private static int fdbins = 8;
    public static int fdbins() { return fdbins; }
    public static void fdbins(int fb) { fdbins=fb; }

    // size restrictions for scale octaves, use octaves < max_size and > min_size only
    private static int min_size = 64;
    public static int min_size() { return min_size(); }
    public static void set_min_size(int ms) { min_size=ms; }

    private static int max_size = 1024;
    public static int max_size() { return max_size(); }
    public static void set_max_size(int ms) { max_size=ms; }

    /**
     * @author Jonathan ODUL 2009
     * @link http://www.jidul.com
     * @version 1.0
     * @param w width of the picture
     * @param h height of the picture
     * @param pixels[] tab of pixels rgb color (ex: red 0xff0000)
     * @return vector of features of the picture
     */

    public static Vector< Feature > getFeatures(int w, int h, int pixels[])
    {
        FloatArray2D fa = ImageArrayConverter.ArrayToFloatArray2D( w, h, pixels );

        return getFeatures(fa);
    }
    
    /**
     * @author Jonathan ODUL 2009
     * @link http://www.jidul.com
     * @version 1.0
     * @param imp picture
     * @return vector of features of the picture
     */
	 
    public static Vector< Feature > getFeatures(Image imp)
    {
        if (imp==null)  { System.err.println( "There are no images open" ); return null; }

        FloatArray2D fa = ImageArrayConverter.ImageToFloatArray2D( imp );

        return getFeatures(fa);
    }

    public static Vector< Feature > getFeatures(FloatArray2D fa)
    {
        Vector< Feature > fs1;

        FloatArray2DSIFT sift = new FloatArray2DSIFT( fdsize, fdbins );
        
        Filter.enhance( fa, 1.0f );

        fa = Filter.computeGaussianFastMirror( fa, ( float )Math.sqrt( initial_sigma * initial_sigma - 0.25 ) );

        long start_time = System.currentTimeMillis();
       // System.out.print( "processing SIFT ..." );
        sift.init( fa, steps, initial_sigma, min_size, max_size );
        fs1 = sift.run( max_size );
        Collections.sort( fs1 );
      //  System.out.println( " took " + ( System.currentTimeMillis() - start_time ) + "ms" );

        //System.out.println( fs1.size() + " features identified and processed" );

        return fs1;
    }

}