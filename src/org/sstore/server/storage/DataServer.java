package org.sstore.server.storage;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.sstore.protocol.BlockMessage;
import org.sstore.utils.Constants;

/**
 * Dataserver maintains data blocks and sends the latest list of block
 * informaton to tht metaserver.
 * 
 * 
 * @author lbchen
 *
 */
public class DataServer {

	private final static Logger log = Logger.getLogger(DataServer.class.getName());
	private static Set<Long> blockIds;
	private static String serverId = "";

	public static void main(String[] args) {
		DataServer dataserver = new DataServer();
		String metahost = "";
		int localport = 0;
		
		log.info(args.length);
		
		switch (args.length) {
		case 1: // no local port defined, use the default one.
			metahost = args[0];
			localport = Constants.LOCALPORT;
			serverId = "localhost:" + localport;
			dataserver.initialize(metahost, localport);
			break;
		case 2: // localport defined.
			metahost = args[0];
			localport = Integer.parseInt(args[1]);
			serverId = "localhost:" + localport;
			dataserver.initialize(metahost, localport);
			break;
		default: // invalid options.
			log.error("invalid option(s)");
			System.exit(0);
			break;
		}

	}

	void initialize(String metahost, int localport) {
		startServer(localport);
		startRpcThread(metahost, localport);
	}

	void startServer(int localport) {
		DataServerRpcImpl dsrpc = new DataServerRpcImpl();
		dsrpc.startRpcServer(localport);
	}

	/** start a thread to send critical heartbeat message periodically */
	void startRpcThread(String metahost, int localport) {
		Thread heartbeatTh = new Thread(new DataServerRpcImpl(metahost, localport));
		heartbeatTh.start();
	}

	public Set<Long> getBlockIds() {

		blockIds = new HashSet<Long>();
		blockIds.add((long) 1);
		blockIds.add((long) 2);
		blockIds.add((long) 3);

		return blockIds;
	}

	public String buildBlockMessage() {
		BlockMessage msg = new BlockMessage(getServerId(), getBlockIds());
		log.info("build block message: " + msg.toString());
		return msg.toString();
	}

	public String getServerId() {
		return serverId;
	}

}
