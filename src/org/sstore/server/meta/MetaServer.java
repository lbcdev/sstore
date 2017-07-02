package org.sstore.server.meta;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * Metaserver serves as the manager of the system and stores only metadata of
 * data and storage servers.
 * 
 * The metaserver maintains two key tables: 1) filename -> blocks (persistent on
 * disk) 2) block -> dataserver list (received from dataserver via heartbeat
 * messages)
 * 
 * @author lbchen
 * 
 */
public class MetaServer {

	public static final Logger log = Logger.getLogger(MetaServer.class.getName());

	private static Map<Long, Set<Long>> b2dsTable;
	private static Map<String, String> f2bTable;
	public static void main(String[] args) {
		MetaServer metaserver = new MetaServer();
		metaserver.initialize();
	}

	void initialize() {
		b2dsTable = new HashMap<Long, Set<Long>>();
		f2bTable = new HashMap<String, String>();
		startServer();
	}


	/** synchronized update on the block-server table. */
	static synchronized void updateDSTable(long serverId, Set<Long> blkIds) {
		if (b2dsTable != null)
			b2dsTable.put(serverId, blkIds);
	}

	void startServer() {
		MetaRpcImpl metarpc = new MetaRpcImpl();
		metarpc.startRpcServer();
	}
}
