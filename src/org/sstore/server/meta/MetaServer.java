package org.sstore.server.meta;

import org.apache.log4j.Logger;

/**
 * Metaserver serves as the manager of the system and stores only metadata of
 * data and storage servers.
 * 
 * The metaserver maintains two key tables:
 * 	1) filename -> blocks (persistent on disk)
 *  2) block -> dataserver list (received from dataserver via heartbeat messages) 
 * @author lbchen	
 * 
 */
public class MetaServer {
	
	public static final Logger log = Logger.getLogger(MetaServer.class.getName());
	
	public static void main(String[] args){
		MetaServer metaserver = new MetaServer();
		metaserver.startServer();
	}
	
	void startServer(){
		MetaRpcImpl metarpc = new MetaRpcImpl();
		metarpc.startRpcServer();
	}
}
