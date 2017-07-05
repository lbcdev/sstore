package org.sstore.server.meta;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.sstore.protocol.Block;
import org.sstore.protocol.Message;
import org.sstore.utils.Constants;

/**
 * This class handles all rpc calls. It is created, started, and stopped by
 * {@link MetaServer}.
 * 
 * @author lbchen
 *
 */
public class MetaRpcImpl implements MetaRpc {

	private final static Logger log = Logger.getLogger(MetaServer.class.getName());

	private static Map<Integer, Set<Long>> dstable;

	public void startRpcServer() {
		try {
			MetaRpcImpl obj = new MetaRpcImpl();
			MetaRpc stub = (MetaRpc) UnicastRemoteObject.exportObject(obj, 0);
			Registry registry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
			registry.bind("metarpc", stub);
			log.info("RPC server ready");

		} catch (Exception e) {
			log.error("RPC server err: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * find a replica server that holds the remote file and return the address
	 * to the client.
	 */
	public String findDataServer(String remote) {
		// String hostaddr = "localhost:1100";
		String hostaddr = MetaServer.lookupF2DSTable(remote);
		if (hostaddr != null)
			return hostaddr;
		return "file not found";
	}

	public String assignDataServer(String remote) {
		String host = "localhost";
		int port = 1100;
		String dsaddr = host + ":" + port;
		MetaServer.updateF2DSTable(remote, dsaddr);
		log.info("update file-dataserver table");
		return dsaddr;
	}

	/** Receive heart beat block info from dataserver. */
	public String heartBeat(String msg) {
		String sid = msg.split(",")[0];
		String[] blockstrs = msg.split(",")[2].split("-");
		HashSet<Long> blockset = new HashSet<Long>();
		for (String str : blockstrs) {
			blockset.add(Long.parseLong(str));
		}
		MetaServer.updateDSTable(Long.parseLong(sid), blockset);
		readTest();
		log.info("receive block info: " + sid);
		return "ack";
	}

	/** Receive heart beat message from dataserver. */
	public void heartBeat(Message msg) {

		switch (msg.getType()) {

		case Constants.BLOCKMSG:
			break;

		default:
			break;
		}
	}

	public byte[] readFile(List<Block> blks, int start, int end) throws RemoteException {
		String test = "read from rpc";
		return test.getBytes();
	}

	public String readTest() {
		return "read from rpc";
	}

}
