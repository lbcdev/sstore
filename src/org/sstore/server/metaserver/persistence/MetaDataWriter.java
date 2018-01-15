package org.sstore.server.metaserver.persistence;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.sstore.utils.Constants;

/**
 * Write metadata to disk.
 * 
 * @author lbchen
 *
 */
public class MetaDataWriter {

	private static Logger log = Logger.getLogger(MetaDataWriter.class);

	/** flush file-dataserver table to disk */
	public static void flushF2DSTable(Map<String, String> f2ds) {

//		Thread th = new Thread(new F2DSWriterThread(f2dsTable));
//		th.start();
		Iterator<String> iter = f2ds.keySet().iterator();
		try {
			FileWriter out = new FileWriter(Constants.METAROOTDIR + "f2dsTable");
			while (iter.hasNext()) {
				String key = iter.next();
				String value = f2ds.get(key);
				String rows = key + "@" + value;
				out.write(rows + "\n");
			}
			out.flush();
			out.close();
		} catch (IOException e) {

		}

	}

}

class F2DSWriterThread implements Runnable {
	Map<String, String> f2ds = new HashMap<String, String>();

	public F2DSWriterThread(Map<String, String> f2ds) {
		this.f2ds = f2ds;
	}

	public void run() {
		Iterator<String> iter = f2ds.keySet().iterator();
		try {
			FileWriter out = new FileWriter(Constants.METAROOTDIR + "f2dsTable");
			while (iter.hasNext()) {
				String key = iter.next();
				String value = f2ds.get(key);
				String rows = key + "-" + value;
				out.write(rows + "\n");
			}
			out.flush();
			out.close();
		} catch (IOException e) {

		}
	}
}
