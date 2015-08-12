package name.herve.imagematch.video;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class TrackSegment extends TrackRange implements Iterable<TrackPosition> {
	private List<TrackPosition> pos;

	public TrackSegment() {
		super();
		pos = new ArrayList<TrackPosition>();
	}

	public boolean add(TrackPosition e) {
		return pos.add(e);
	}

	public boolean isEmpty() {
		return pos.isEmpty();
	}

	@Override
	public Iterator<TrackPosition> iterator() {
		return pos.iterator();
	}

	public boolean remove(TrackPosition o) {
		return pos.remove(o);
	}

	public int size() {
		return pos.size();
	}

	@Override
	public String toString() {
		return "TrackSegment [getScore()=" + getScore() + ", getTcIn()=" + getTcIn() + ", getTcOut()=" + getTcOut() + "]";
	}

	@Override
	public void wrap() {
		setScore(0);
		Collections.sort(pos);

		for (TrackPosition p : pos) {
			updateRangeAndScore(p.getTc(), p.getScore());
		}
	}

}
