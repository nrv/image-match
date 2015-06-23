package name.herve.imagematch.lsh;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.LongBuffer;
import java.nio.channels.FileChannel;

import org.apache.lucene.util.OpenBitSet;

import plugins.nherve.toolbox.PersistenceToolbox;
import fr.otmedia.plagiarism.lsh.BitsetSignature;
import fr.otmedia.plagiarism.lsh.LSHTables;
import fr.otmedia.plagiarism.lsh.RandomProjection;

public class ExtendedPersistence extends Persistence {
	private final static int BITSET_TYPE = 16;
	
	private void dumpLSHTables(LSHTables lsh, File f) throws IOException {
		RandomAccessFile raf = null;
		try {
			raf = getFile(f, true);
			FileChannel fc = raf.getChannel();
			PersistenceToolbox.dumpString(fc, LSHTables.VERSION);

			PersistenceToolbox.dumpInt(fc, lsh.getK());
			PersistenceToolbox.dumpInt(fc, lsh.getL());
			PersistenceToolbox.dumpInt(fc, lsh.getInputDim());
			dumpRandomProjection(lsh.getRp(), fc);
		} finally {
			if (raf != null) {
				raf.close();
			}
		}
	}

	public void dumpRandomProjection(RandomProjection rp, File f) throws IOException {
		RandomAccessFile raf = null;
		try {
			raf = getFile(f, true);
			FileChannel fc = raf.getChannel();
			dumpRandomProjection(rp, fc);
		} finally {
			if (raf != null) {
				raf.close();
			}
		}
	}

	private void dumpRandomProjection(RandomProjection rp, FileChannel fc) throws IOException {
		PersistenceToolbox.dumpString(fc, RandomProjection.VERSION);

		PersistenceToolbox.dumpInt(fc, rp.getNb());
		PersistenceToolbox.dumpInt(fc, rp.getInputDim());
		double[][] projection = rp.getProjection();

		for (int t = 0; t < rp.getNb(); t++) {
			ByteBuffer bb = ByteBuffer.allocate(PersistenceToolbox.DOUBLE_NB_BYTES * rp.getInputDim());
			DoubleBuffer db = bb.asDoubleBuffer();
			db.put(projection[t]);
			db.flip();
			fc.write(bb);
		}
	}

	public BitsetSignature loadBitsetSignature(FileChannel fc) throws IOException {
		int sz = PersistenceToolbox.loadInt(fc);

		ByteBuffer bb = ByteBuffer.allocate(PersistenceToolbox.LONG_NB_BYTES * sz);
		fc.read(bb);
		bb.flip();
		LongBuffer lb = bb.asLongBuffer();
		long[] data = new long[sz];
		lb.get(data);

		OpenBitSet v = new OpenBitSet();
		v.setBits(data);
		return new BitsetSignature(sz, v);
	}

	public LSHTables loadLSHTables(File f, boolean full) throws IOException {
		RandomAccessFile raf = null;
		try {
			raf = getFile(f, false);
			FileChannel fc = raf.getChannel();

			String version = PersistenceToolbox.loadString(fc);

			if (!LSHTables.VERSION.equals(version)) {
				throw new IOException("Incompatible LSH version (" + version + "/" + LSHTables.VERSION + ")");
			}

			int k = PersistenceToolbox.loadInt(fc);
			int l = PersistenceToolbox.loadInt(fc);
			int d = PersistenceToolbox.loadInt(fc);

			LSHTables lsh = new LSHTables(k, l, d);

			if (full) {
				lsh.setRp(loadRandomProjection(fc));
			}

			return lsh;
		} finally {
			if (raf != null) {
				raf.close();
			}
		}
	}

	private RandomProjection loadRandomProjection(FileChannel fc) throws IOException {
		String version = PersistenceToolbox.loadString(fc);

		if (!RandomProjection.VERSION.equals(version)) {
			throw new IOException("Incompatible RandomProjection version (" + version + "/" + RandomProjection.VERSION + ")");
		}

		int nb = PersistenceToolbox.loadInt(fc);
		int dim = PersistenceToolbox.loadInt(fc);
		double[][] projection = new double[nb][dim];

		for (int t = 0; t < nb; t++) {
			ByteBuffer bb = ByteBuffer.allocate(PersistenceToolbox.DOUBLE_NB_BYTES * dim);
			fc.read(bb);
			bb.flip();
			DoubleBuffer db = bb.asDoubleBuffer();
			db.get(projection[t], 0, dim);
		}

		RandomProjection rp = new RandomProjection(nb, dim);
		rp.setProjection(projection);

		return rp;
	}

	public Signature loadSignature(FileChannel fc) throws IOException {
		int type = PersistenceToolbox.loadInt(fc);
		switch (type) {
		case BITSET_TYPE:
			return loadBitsetSignature(fc);
		default:
			return super.loadSignature(fc);
		}
	}

	public RandomProjection loadRandomProjection(File f) throws IOException {
		RandomAccessFile raf = null;
		try {
			raf = getFile(f, false);
			FileChannel fc = raf.getChannel();
			return loadRandomProjection(fc);
		} finally {
			if (raf != null) {
				raf.close();
			}
		}
	}

	public void dumpSignature(FileChannel fc, Signature s) throws IOException {
		if ((s != null) && (s instanceof BitsetSignature)) {
			OpenBitSet bs = ((BitsetSignature) s).getBitSet();
			PersistenceToolbox.dumpInt(fc, BITSET_TYPE);

			PersistenceToolbox.dumpInt(fc, bs.getNumWords());
			ByteBuffer bb = ByteBuffer.allocate(PersistenceToolbox.LONG_NB_BYTES * bs.getNumWords());
			LongBuffer lb = bb.asLongBuffer();
			lb.put(bs.getBits());
			lb.flip();
			fc.write(bb);
		} else {
			super.dumpSignature(fc, s);
		}
	}
}
