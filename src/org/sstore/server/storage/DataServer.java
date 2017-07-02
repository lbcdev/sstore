package org.sstore.server.storage;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.sstore.protocol.BlockMessage;

/**
 * Dataserver maintains data blocks and sends the latest list of block informaton
 * to tht metaserver. 
 * 
 * 
 * @author lbchen
 *
 */
public class DataServer {
	
	private final static Logger log = Logger.getLogger(DataServer.class.getName());
	private static Set<Long> blockIds;
	private static int serverId = 1;
	
	
	public static void main(String[] args){
		DataServer dataserver = new DataServer();
		dataserver.initialize();
		
	}
	
	void initialize(){
		startServer();
		startRpcThread();
	}
	
	void startServer(){
		DataServerRpcImpl dsrpc = new DataServerRpcImpl();
		dsrpc.startRpcServer();
	}
	
	void startRpcThread(){
		Thread heartbeatTh = new Thread(new DataServerRpcImpl());
		heartbeatTh.start();
	}
	public Set<Long> getBlockIds(){
		
		blockIds = new HashSet<Long>();
		blockIds.add((long)1);
		blockIds.add((long)2);
		blockIds.add((long)3);
		
		return blockIds;
	}
	
	public String buildBlockMessage(){
		BlockMessage msg = new BlockMessage(getServerId(), getBlockIds());
		log.info("build block message: " + msg.toString());
		return msg.toString();
	}
	
	public int getServerId(){
		return serverId;
	}
	
}
