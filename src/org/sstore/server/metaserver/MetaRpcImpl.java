package org.sstore.server.metaserver;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashSet;
import java.util.List;

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

	private final static Logger log = Logger.getLogger(MetaRpcImpl.class.getName());

	private static MetaServer metaserver;

	public void startRpcServer() {
		try {
			metaserver = new MetaServer();
			MetaRpcImpl obj = new MetaRpcImpl();
			MetaRpc stub = (MetaRpc) UnicastRemoteObject.exportObject(obj, 0);
			Registry registry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
			registry.bind(Constants.METARPC_NAME, stub);
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
		// get all replicas.
		String replicas = metaserver.lookupF2DSTable(remote);
		// return primary only.
		log.info(replicas);
		if (replicas != null)
			return replicas;
		return "file not found";
	}

	/**  */
	public String assignDataServer(String remote) {

		String replicas = metaserver.assignReplica(remote);
		if (replicas != null) {
			metaserver.updateF2DSTable(remote, replicas);
			log.info("assign replicas" + replicas);
			return replicas;
		}
		return null;
	}

	/** Receive heart beat block info from dataserver. */
	public String heartBeat(String msg) {
		String sid = msg.split(",")[1];
		String[] filestr = msg.split(",")[2].split("-");

		metaserver.updateDS2FTable(sid, filestr);
		for (String fname : filestr) {
			metaserver.updateF2DSTable(fname, sid);
		}
		metaserver.updateDSStatus(sid);
		log.info("receive heartbeat from " + sid);
		return "ack";
	}

	/** more general heartbeat message, in-progress. */
	public void heartBeat(Message msg) {
		switch (msg.getType()) {
		case Constants.BLOCKMSG:
			break;
		default:
			break;
		}
	}

	/** test method */
	public byte[] readFile(List<Block> blks, int start, int end) throws RemoteException {
		String test = "read from rpc";
		return test.getBytes();
	}

	/** test method */
	public String readTest() {
		return "read from rpc";
	}

}
