package name.herve.imagematch;

import mpi.cbg.fly.Feature;

import com.stromberglabs.jopensurf.SURFInterestPoint;

public class MyFeature {
	private float[] desc;
	private float orientation;
	private float scale;
	private MyPoint point;

	public MyFeature(Feature f) {
		super();
		point = new MyPoint(f.location[0], f.location[1]);
		orientation = f.orientation;
		scale = f.scale;
		desc = f.descriptor;
	}

	public MyFeature(SURFInterestPoint f) {
		super();
		point = new MyPoint(f.getX(), f.getY());
		scale = f.getScale();
		orientation = f.getOrientation();
		desc = f.getDescriptor();
	}

	public float[] getDesc() {
		return desc;
	}

	public float getOrientation() {
		return orientation;
	}

	public MyPoint getPoint() {
		return point;
	}

	public float getScale() {
		return scale;
	}

	public float getX() {
		return point.getX();
	}

	public float getY() {
		return point.getY();
	}

}
