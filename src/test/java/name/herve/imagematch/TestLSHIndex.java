package name.herve.imagematch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import name.herve.imagematch.db.SiftDescriptorsConfiguration;
import name.herve.imagematch.impl.MyFeaturePersistence;
import name.herve.imagematch.lsh.LSHTables;
import plugins.nherve.toolbox.Algorithm;
import plugins.nherve.toolbox.PerfMonitor;
import plugins.nherve.toolbox.PersistenceToolbox;
import plugins.nherve.toolbox.concurrent.TaskException;
import plugins.nherve.toolbox.concurrent.TaskManager;
import plugins.nherve.toolbox.image.DefaultImageLoader;
import plugins.nherve.toolbox.image.db.DatabaseConfiguration;
import plugins.nherve.toolbox.image.db.DatabaseManager;
import plugins.nherve.toolbox.image.db.ImageDatabase;
import plugins.nherve.toolbox.image.db.ImageEntry;
import plugins.nherve.toolbox.image.db.IndexingConfiguration;
import plugins.nherve.toolbox.image.feature.DefaultSegmentableImage;
import plugins.nherve.toolbox.image.feature.signature.BagOfSignatures;
import plugins.nherve.toolbox.image.feature.signature.VectorSignature;

/**
 * 
 * @author Nicolas HERVE - nherve@ina.fr
 */
public class TestLSHIndex extends Algorithm {
	public class LSHWorker implements Callable<Boolean> {
		private final ImageEntry e;
		private final LSHTables lsh;

		public LSHWorker(LSHTables lsh, ImageEntry e) {
			super();
			this.e = e;
			this.lsh = lsh;
		}

		@Override
		public Boolean call() throws Exception {
			// e.setIndexed(lsh.h(e.getLocalSignatures().get(key)));
			Map<String, BagOfSignatures<VectorSignature>> sigs = e.getLocalSignatures(); 
			BagOfSignatures<VectorSignature> bag = sigs.get("Sift"); 
			for (VectorSignature vs : bag) {
				lsh.h(vs);
			}
			return true;
		}
	}

	public static void main(String[] args) {
		try {
			TaskManager.initAll();

			DatabaseConfiguration dbConf = TestDatabase.getDatabaseConfiguration();
			DatabaseManager<DefaultSegmentableImage> mgr = new DatabaseManager<DefaultSegmentableImage>(true);
			
			PersistenceToolbox.registerSignaturePersistenceHook(new MyFeaturePersistence());
			ImageDatabase<DefaultSegmentableImage> db = mgr.load(dbConf);
			
			TestLSHIndex algo = new TestLSHIndex();
			algo.work(db, 2, 2, 128);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (TaskException e) {
			e.printStackTrace();
		} finally {
			TaskManager.shutdownAll();
			System.exit(0);
		}

	}

	public void work(ImageDatabase<DefaultSegmentableImage> db, int k, int L, int dim) throws IOException, TaskException {
		TaskManager tm = new TaskManager();
		try {
			PerfMonitor perf = new PerfMonitor(PerfMonitor.MONITOR_ALL_THREAD_ROUGHLY);
			perf.setMonitorMemory(true);
			perf.start();

			List<Callable<Boolean>> lshTasks = new ArrayList<Callable<Boolean>>();
			LSHTables lsh = new LSHTables(k, L, dim);
			lsh.generateProjection();

			for (ImageEntry e : db) {
				lshTasks.add(new LSHWorker(lsh, e));
			}
			tm.waitResults(tm.submitAll(lshTasks), "lsh", 1000l);

			perf.stop();
			System.out.println(perf);
			
//			Persistence p = new Persistence();
//			p.dumpLSHTables(lsh, p.getLSHTablesFile(root));
//			p.dumpSignatures(db, p.getLSHIndexFile(root), true);
//			System.out.println("Files dumped");
		} catch (InterruptedException e) {
			throw new TaskException(e);
		} finally {
			tm.shutdown();
		}
	}

}
