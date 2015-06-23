package name.herve.imagematch;

import java.io.IOException;

import name.herve.imagematch.db.SiftDescriptorsConfiguration;
import name.herve.imagematch.impl.MyFeaturePersistence;
import plugins.nherve.toolbox.PersistenceToolbox;
import plugins.nherve.toolbox.concurrent.TaskManager;
import plugins.nherve.toolbox.image.DefaultImageLoader;
import plugins.nherve.toolbox.image.db.DatabaseConfiguration;
import plugins.nherve.toolbox.image.db.DatabaseManager;
import plugins.nherve.toolbox.image.db.ImageDatabase;
import plugins.nherve.toolbox.image.db.IndexingConfiguration;
import plugins.nherve.toolbox.image.feature.DefaultSegmentableImage;

public class TestDatabase {
	public final static String ROOT = "/home/nherve/Travail/Data/Perso/image_db";

	public static DatabaseConfiguration getDatabaseConfiguration() {
		DatabaseConfiguration dbConf = new DatabaseConfiguration();
		dbConf.setName("Test");
		dbConf.setExtension(".JPG");
		dbConf.setRoot(ROOT);
		dbConf.setPictures("pictures");
		dbConf.setSignatures("signatures");

		return dbConf;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			TaskManager.initAll();

			DatabaseConfiguration dbConf = getDatabaseConfiguration();

			IndexingConfiguration<DefaultSegmentableImage> idxConf = new SiftDescriptorsConfiguration();

			DatabaseManager<DefaultSegmentableImage> mgr = new DatabaseManager<DefaultSegmentableImage>(true);
			ImageDatabase<DefaultSegmentableImage> db = mgr.create(dbConf);

			mgr.index(db, new DefaultImageLoader(), idxConf, true, 5, false);
			
			PersistenceToolbox.registerSignaturePersistenceHook(new MyFeaturePersistence());
			mgr.save(db);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			TaskManager.shutdownAll();
			System.exit(0);
		}

	}

}
