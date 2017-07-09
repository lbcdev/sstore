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
	private static Map<String, DataServerStatus> dsTable;
	// dataserver index table, for assigning replicas
	private static List<String> dsList;

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

		// updateDSList();
		startServer();
	}

	/** start metaserver rpc service */
	void startServer() {
		MetaRpcImpl metarpc = new MetaRpcImpl();
		metarpc.startRpcServer();
	}

	/**
	 * periodically update the list of dataservers, remove those are not alive
	 */
	void updateDSList() {
		dsList.add("localhost:1100");
		dsList.add("localhost:1101");
		dsList.add("localhost:1102");
		dsList.add("localhost:1103");
		dsList.add("localhost:1104");
	}

	/**
	 * update dataserver status, only active state enabled now, more status to
	 * be added
	 */
	void updateDSStatus(String sid) {
		DataServerStatus status = new DataServerStatus();
		status.setActive(true);
		dsTable.put(sid, status);
		if (!dsList.contains(sid))
			dsList.add(sid);
		printDSList();
	}

	/** assign replicas to a file, triple-replication by default */
	String assignReplica(String filename) {
		long seed = System.currentTimeMillis();
		Random rand = new Random(seed);
		int primary = rand.nextInt(dsList.size());
		int r2 = (primary + Constants.REPLIC_FACTOR) % dsList.size();
		int r3 = (r2 + Constants.REPLIC_FACTOR) % dsList.size();
		String replicas = dsList.get(primary) + "," + dsList.get(r2) + "," + dsList.get(r3);
		return replicas;
	}

	/**
	 * lookup file-dataserver table by file name and return dataserver address
	 * if find.
	 */
	String lookupF2DSTable(String filename) {
		return f2dsTable.get(filename);
	}

	/** thread-safe, update file-dataserver table */
	synchronized void updateF2DSTable(String filename, String sid) {
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
		flushF2DSTable();
		printTable(f2dsTable);
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
