package name.herve.imagematch.video;

import fr.ina.research.rex.commons.tc.RexTimeCode;

public abstract class TrackRange {
	private RexTimeCode tcIn;
	private RexTimeCode tcOut;
	private double score;

	public TrackRange() {
		super();

		tcIn = null;
		tcOut = null;
		score = 0;
	}

	public double getScore() {
		return score;
	}

	public RexTimeCode getTcIn() {
		return tcIn;
	}

	public RexTimeCode getTcOut() {
		return tcOut;
	}

	public void setScore(double score) {
		this.score = score;
	}

	protected void updateRangeAndScore(RexTimeCode tc, double score) {
		updateRangeAndScore(tc, tc, score);
	}

	protected void updateRangeAndScore(RexTimeCode ti, RexTimeCode to, double score) {
		if (tcIn == null) {
			tcIn = new RexTimeCode(ti.getSecond());
		} else if (ti.getSecond() < tcIn.getSecond()) {
			tcIn.setSecond(ti.getSecond());
		}

		if (tcOut == null) {
			tcOut = new RexTimeCode(to.getSecond());
		} else if (to.getSecond() > tcOut.getSecond()) {
			tcOut.setSecond(to.getSecond());
		}
		
		this.score = Math.max(score, this.score);
	}

	public abstract void wrap();
}
