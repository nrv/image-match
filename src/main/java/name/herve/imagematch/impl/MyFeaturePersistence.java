package name.herve.imagematch.impl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.channels.FileChannel;

import plugins.nherve.toolbox.PersistenceToolbox;
import plugins.nherve.toolbox.SignaturePersistenceHook;
import plugins.nherve.toolbox.image.feature.signature.VectorSignature;

/**
 * @author Nicolas HERVE - n.herve@laposte.net
 */
public class MyFeaturePersistence implements SignaturePersistenceHook<MyFeature> {

	@Override
	public void dumpSignature(FileChannel fc, VectorSignature s) throws IOException {
		MyFeature f = (MyFeature) s;

		PersistenceToolbox.dumpInt(fc, f.getId());
		PersistenceToolbox.dumpInt(fc, f.getDesc().length);
		ByteBuffer bb = ByteBuffer.allocate(PersistenceToolbox.FLOAT_NB_BYTES * f.getDesc().length);
		FloatBuffer fb = bb.asFloatBuffer();
		fb.put(f.getDesc());
		fb.flip();
		fc.write(bb);
		PersistenceToolbox.dumpFloat(fc, f.getOrientation());
		PersistenceToolbox.dumpFloat(fc, f.getScale());
		PersistenceToolbox.dumpFloat(fc, f.getPoint().getX());
		PersistenceToolbox.dumpFloat(fc, f.getPoint().getY());
	}

	@Override
	public Class<MyFeature> getSignatureClass() {
		return MyFeature.class;
	}

	@Override
	public int getTypeCode() {
		return 13;
	}

	@Override
	public VectorSignature loadSignature(FileChannel fc) throws IOException {
		MyFeature f = new MyFeature();

		f.setId(PersistenceToolbox.loadInt(fc));
		int sz = PersistenceToolbox.loadInt(fc);
		ByteBuffer bb = ByteBuffer.allocate(PersistenceToolbox.FLOAT_NB_BYTES * sz);
		fc.read(bb);
		bb.flip();
		FloatBuffer fb = bb.asFloatBuffer();
		float[] desc = new float[sz];
		fb.get(desc);
		f.setDesc(desc);
		f.setOrientation(PersistenceToolbox.loadFloat(fc));
		f.setScale(PersistenceToolbox.loadFloat(fc));
		float x = PersistenceToolbox.loadFloat(fc);
		float y = PersistenceToolbox.loadFloat(fc);
		f.setPoint(new MyPoint(x, y));

		return f;
	}
}
