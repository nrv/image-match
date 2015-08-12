package name.herve.imagematch.video;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.ina.research.amalia.AmaliaException;
import fr.ina.research.amalia.model.LocalisationBlock;
import fr.ina.research.amalia.model.MetadataBlock;
import fr.ina.research.amalia.model.MetadataBlock.MetadataType;
import fr.ina.research.amalia.model.MetadataFactory;
import fr.ina.research.amalia.model.jaxb.ShapeType;

public class TrackedObject extends TrackRange implements Iterable<TrackSegment> {
	public static MetadataBlock createTrackingMetadataBlock(TrackedObject to, String id) throws AmaliaException {
		MetadataBlock m = MetadataFactory.createMetadataBlock(id, MetadataType.VISUAL_TRACKING, to.getTcIn(), to.getTcOut());

		for (TrackSegment s : to) {
			LocalisationBlock slb = MetadataFactory.createLocalisationBlock(s.getTcIn(), s.getTcOut());
			m.addToRootLocalisationBlock(slb);
			// LocalisationBlock slb = m.getRootLocalisationBlock();

			for (TrackPosition p : s) {
				LocalisationBlock spatial = MetadataFactory.createSpatialBlock(p.getTc(), p.getXc(), p.getYc(), p.getHw(), p.getHh(), p.getO(), to.getShapeType());
				slb.addLocalisationBlock(spatial);
			}
		}

		return m;
	}

	private List<TrackSegment> seg;
	private ShapeType shapeType;

	public TrackedObject() {
		super();
		seg = new ArrayList<TrackSegment>();
		shapeType = ShapeType.RECTANGLE;
	}

	public boolean add(TrackSegment e) {
		return seg.add(e);
	}

	public ShapeType getShapeType() {
		return shapeType;
	}

	@Override
	public Iterator<TrackSegment> iterator() {
		return seg.iterator();
	}

	public boolean remove(TrackSegment o) {
		return seg.remove(o);
	}

	public void setShapeType(ShapeType shapeType) {
		this.shapeType = shapeType;
	}

	@Override
	public void wrap() {
		setScore(0);
		for (TrackSegment s : seg) {
			s.wrap();
			updateRangeAndScore(s.getTcIn(), s.getTcOut(), s.getScore());
		}
	}

}
