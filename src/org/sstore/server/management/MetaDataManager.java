package org.sstore.server.management;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.sstore.server.metaserver.MetaServer;
import org.sstore.server.storage.DataServerStatus;
import org.sstore.utils.Constants;

/**
 * Metadata manager manages key tables such as file-to-replicas mapping,
 * dataserver status, and etc. It removes stale table entries when dataservers
 * fail or files are remove.
 * 
 * @author lbchen
 *
 */
public class MetaDataManager implements Runnable {

	private final static Logger log = Logger.getLogger(MetaDataManager.class.getName());
	private static MetaServer metaserver;

	public MetaDataManager() {
		metaserver = MetaServer.getInstance();
	}

	public void run() {
		while (true) {
			List<String> failedList = checkDSStatus();
			if (failedList.size() > 0) {
				for (String dsname : failedList) {
					cleanF2DSTableByDS(dsname);
				}
			}
			try {
				Thread.sleep(Constants.HEARTBEAT_INTERVAL);
			} catch (InterruptedException e) {
				log.error(e.getMessage());
			}
		}
	}

	/** detect failed dataserver. */
	public List<String> checkDSStatus() {
		List<String> failedList = new ArrayList<String>();
		HashMap<String, DataServerStatus> dsTable = metaserver.getDSTable();

		if (dsTable.size() == 0) {
			log.error(Constants.NODATASERVER_MSG);
			return failedList;
		}
		if (dsTable.size() < 3) {
			log.error(Constants.INSUFFICIENT_DS);
		}
		Iterator<String> iter = dsTable.keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			DataServerStatus status = dsTable.get(key);
			if (status.getTTL() < 0) {
				failedList.add(key);
			}
			// decrease ttl by 1.
			status.setTTL(status.getTTL() - 1);
		}
		log.info("checkDSStatus: get " + failedList.size() + " failed dataserver");
		return failedList;
	}

	/**
	 * clean up F2DSTable based on failed dataserver.
	 * 
	 * @param dsname
	 *            dataserver id
	 */
	public void cleanF2DSTableByDS(String dsid) {
		log.info("cleanF2DSTableByDS start...");
		String[] files = metaserver.getFilesOnDataServer(dsid);
		// remove dataserver dsid from replica list of file.
		for (String fname : files) {
			metaserver.removeReplicaOfFile(fname, dsid);
		}
	}
	

}
