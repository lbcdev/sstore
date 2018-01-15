package org.sstore.server.metaserver.monitor;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.sstore.server.metaserver.MetaServer;
import org.sstore.utils.Constants;

/**
 * Monitor object liveness, replica status, and etc.
 * 
 * @author lbchen
 *
 */
public class OjbectLiveMonitor implements Runnable {

	private final Logger log = Logger.getLogger(OjbectLiveMonitor.class);
	private MetaServer meta = null;
	private static boolean isChecking = false;

	public OjbectLiveMonitor() {
		meta = MetaServer.getInstance();
	}

	public void run() {

		while (true) {
			removeStaleObj();

			try {
				TimeUnit.MILLISECONDS.sleep(Constants.HEARTBEAT_INTERVAL);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void removeStaleObj() {
		// isChecking = true;
		log.info("begin of checking for stale objects...");

		Map<String, String[]> ds2f = meta.getDs2f();
		Map<String, String> f2ds = meta.getF2ds();
		Set<String> objList = f2ds.keySet();
		Iterator<String> iter = objList.iterator();
		while (iter.hasNext()) {
			String obj = iter.next();
			if (ds2f.containsValue(obj)) {
				continue;
			} else {
				iter.remove();
				meta.removeFromF2ds(obj);
				log.info("remove obj: " + obj);
			}
		}

		// isChecking = false;
		log.info("end of checking for stale objects...");

	}
}
