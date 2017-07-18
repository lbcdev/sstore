package org.sstore.server.metaserver;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;
import org.sstore.client.ClientInfo;
import org.sstore.security.auth.AuthManager;
import org.sstore.server.management.MetaDataManager;
import org.sstore.server.storage.DataServerStatus;
import org.sstore.utils.Constants;

/**
 * Metaserver serves as the manager of the system and stores only metadata of
 * data and storage servers.
 * 
 * The metaserver maintains two key tables:
 * 
 * 1) filename -> blocks (persistent on disk);
 * 
 * 2) block -> dataserver list (received from dataserver via heartbeat messages)
 * 
 * @author lbchen
 * 
 */
public class MetaServer {

	public static final Logger log = Logger.getLogger(MetaServer.class.getName());

	// block to dataserver table, unused now
	private static Map<Long, Set<String>> b2dsTable;
	// file to block table, unused now.
	private static Map<String, String> f2bTable;
	// file to primary replica table
	private static Map<String, String> f2dsTable;
	// file to other replicas table, unused no
	private static Map<String, String> f2rplTable;

	// dataserver status table
	private static HashMap<String, DataServerStatus> dsTable;
	// dataserver index table, for assigning replicas
	private static List<String> dsList;
	// dataserver file list.
	private static Map<String, String[]> ds2fTable;

	/** Security-related data structures */
	// client registry table
	private static Map<Long, ClientInfo> clientTable;

	// secure mode is on by default.
	private static boolean secureMode = false;

	public static void main(String[] args) {
		MetaServer metaserver = new MetaServer();
		metaserver.initialize();
	}

	void initialize() {
		b2dsTable = new HashMap<Long, Set<String>>();
		f2bTable = new HashMap<String, String>();
		f2dsTable = new HashMap<String, String>();
		dsTable = new HashMap<String, DataServerStatus>();

		dsList = new ArrayList<String>();
		ds2fTable = new HashMap<String, String[]>();

		clientTable = new HashMap<Long, ClientInfo>();

		startServer();
		startMetaManager();
	}

	/** start a new thread for metadata manager. */
	void startMetaManager() {
		Thread thread = new Thread(new MetaDataManager());
		thread.start();
	}

	/** start metaserver rpc service */
	void startServer() {
		MetaRpcImpl metarpc = new MetaRpcImpl();
		metarpc.startRpcServer();
	}

	/** client program registry. */
	public long registry() {
		long clientId = AuthManager.registry();
		ClientInfo clientInfo = new ClientInfo();
		clientTable.put(clientId, clientInfo);
		return clientId;
	}

	public boolean getSecureMode() {
		return secureMode;
	}

	/**
	 * return files stored on the dataserver.
	 * 
	 * @param sid
	 *            dataserver id
	 * @return a list of files
	 */
	public String[] getFilesOnDataServer(String sid) {
		return ds2fTable.get(sid);
	}

	/**
	 * update dataserver to files table.
	 * 
	 * @param dsid
	 *            dataserver id
	 * @param flist
	 *            list of files on the dataserver
	 */
	public void updateDS2FTable(String dsid, String[] flist) {
		if (ds2fTable == null) {
			ds2fTable = new HashMap<String, String[]>();
		}
		ds2fTable.put(dsid, flist);
	}

	/**
	 * return dataserver status.
	 * 
	 * @return dsTable. dataserver status table.
	 */
	public HashMap<String, DataServerStatus> getDSTable() {
		return dsTable;
	}

	/**
	 * update dataserver status, only active state enabled now, more status to
	 * be added
	 * 
	 * @param sid
	 *            dataserver id.
	 */
	public void updateDSStatus(String sid) {
		DataServerStatus status = new DataServerStatus();
		status.setActive(true);
		status.setTTL(Constants.HEARTBEAT_TTL);
		dsTable.put(sid, status);
		if (!dsList.contains(sid))
			dsList.add(sid);
		printDSList();
	}

	/** assign replicas to a file, triple-replication by default */
	String assignReplica(String filename) {
		long seed = System.currentTimeMillis();
		Random rand = new Random(seed);
		if (dsList.size() <= 0) {
			return null;
		} else {
			int primary = rand.nextInt(dsList.size());
			if (dsList.size() >= 3) {
				int r2 = (primary + Constants.REPLIC_FACTOR) % dsList.size();
				int r3 = (r2 + Constants.REPLIC_FACTOR) % dsList.size();
				String replicas = dsList.get(primary) + "," + dsList.get(r2) + "," + dsList.get(r3);
				return replicas;
			} else
				return dsList.get(primary);
		}
	}

	/**
	 * lookup file-dataserver table by file name and return dataserver address
	 * if find.
	 */
	public String lookupF2DSTable(String filename) {
		return f2dsTable.get(filename);
	}

	/** thread-safe, update file-dataserver table */
	public synchronized void updateF2DSTable(String filename, String sid) {
		// if file and its replicas exist, add new dataserver to the existing
		// pair; if not, create a new pair.
		if (f2dsTable.get(filename) != null) {
			String replicas = f2dsTable.get(filename);
			if (!replicas.contains(sid)) {
				replicas = replicas + "," + sid;
			}
			f2dsTable.put(filename, replicas);
		} else {
			f2dsTable.put(filename, sid);
		}
//		flushF2DSTable();
//		printTable(f2dsTable);
	}

	/**
	 * remove dataserver dsid from the replica list of a file.
	 * 
	 * @param filename
	 *            file
	 * @param dsid
	 *            dataserver id
	 */
	public synchronized void removeReplicaOfFile(String filename, String dsid) {
		// remove dataserver from dsTable, dsList.
		dsTable.remove(dsid);
		dsList.remove(dsid);
		String replicas = f2dsTable.get(filename);
		log.info("removeReplicaOfFile old replicas: " + replicas);
		log.info("removeReplicaOfFile removing: " + dsid);
		String[] replicaarr = replicas.split(",");
		// create new replica list without dsid
		StringBuffer sbuf = new StringBuffer();
		for (String replica : replicaarr) {
			if (!replica.equals(dsid)) {
				log.info("removeReplicaOfFile live " + replica);
				sbuf.append(replica);
				sbuf.append(",");
			}
		}
		String newReplicas = "";
		if (sbuf.length() > 0) {
			// remove the last ',' and update new replica list.
			newReplicas = sbuf.substring(0, sbuf.length() - 1).toString();
		}
		f2dsTable.put(filename, newReplicas);
		log.info("removeReplicaOfFile new replicas" + newReplicas);
//		printTable(f2dsTable);
	}

	/** flush file-dataserver table to disk */
	void flushF2DSTable() {
		Iterator<String> iter = f2dsTable.keySet().iterator();
		try {
			FileWriter out = new FileWriter(Constants.METAROOTDIR + "f2dsTable");
			while (iter.hasNext()) {
				String key = iter.next();
				String value = f2dsTable.get(key);
				String rows = key + "-" + value;
				out.write(rows + "\n");
			}
			out.flush();
			out.close();
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	/** synchronized update on the block-server table. */
	synchronized void updateDSTable(long serverId, Set<String> files) {
		if (b2dsTable != null)
			b2dsTable.put(serverId, files);
	}

	static void printTable(Map<String, String> table) {
		log.info("------ print table ------");
		Iterator<String> iter = table.keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			log.info(key + ":" + table.get(key));
		}
	}

	void printDSList() {
		log.info("------- print dataserver list --------");
		for (String ds : dsList) {
			log.info(ds);
		}
	}
}
