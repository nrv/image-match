package name.herve.imagematch;

public class MyPointMatch {
	private MyPoint p1;
	private MyPoint p2;
	private MyPoint p1t;
	private MyPoint p2t;
	private double featureDistance;
	private float distance;

	public MyPointMatch(MyFeature f1, MyFeature f2, double featureDistance) {
		super();
		p1 = f1.getPoint();
		p2 = f2.getPoint();
		p1t = p1.clone();
		p2t = p2.clone();
		this.featureDistance = featureDistance;
	}

	public void applyModel(MyModel m) {
		p1t = m.apply(p1);
		distance = p1t.distance(p2t);
	}

	public float getDistance() {
		return distance;
	}

	public double getFeatureDistance() {
		return featureDistance;
	}

	public MyPoint getP1() {
		return p1;
	}

	public MyPoint getP1t() {
		return p1t;
	}

	public MyPoint getP2() {
		return p2;
	}

	public MyPoint getP2t() {
		return p2t;
	}

}
