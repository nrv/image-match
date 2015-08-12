package name.herve.imagematch.video;

import fr.ina.research.rex.commons.tc.RexTimeCode;

public class TrackPosition implements Comparable<TrackPosition> {
	private double xc, yc, hw, hh, o;
	private double score;
	private RexTimeCode tc;

	public TrackPosition() {
		super();
	}

	@Override
	public int compareTo(TrackPosition o) {
		return tc.compareTo(o.tc);
	}

	public double getHh() {
		return hh;
	}

	public double getHw() {
		return hw;
	}

	public double getO() {
		return o;
	}

	public double getScore() {
		return score;
	}

	public RexTimeCode getTc() {
		return tc;
	}

	public double getXc() {
		return xc;
	}

	public double getYc() {
		return yc;
	}

	public void setHh(double hh) {
		this.hh = hh;
	}

	public void setHw(double hw) {
		this.hw = hw;
	}

	public void setO(double o) {
		this.o = o;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public void setTc(RexTimeCode tc) {
		this.tc = tc;
	}

	public void setXc(double xc) {
		this.xc = xc;
	}

	public void setYc(double yc) {
		this.yc = yc;
	}
}
