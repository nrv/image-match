package name.herve.imagematch;

public class MyPoint implements Cloneable {
	private float x;

	private float y;

	public MyPoint() {
		super();
	}

	public MyPoint(float x, float y) {
		this();
		this.x = x;
		this.y = y;
	}

	@Override
	public MyPoint clone() {
		return new MyPoint(x, y);
	}

	public float distance(MyPoint other) {
		float dx = x - other.x;
		float dy = y - other.y;
		return (float) Math.sqrt((dx * dx) + (dy * dy));
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public void setX(float x) {
		this.x = x;
	}

	public void setY(float y) {
		this.y = y;
	}
}
