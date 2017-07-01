package org.sstore.server.meta;

import java.util.logging.Logger;

/**
 * Metaserver serves as the manager of the system and stores only metadata of
 * data and storage servers.
 * 
 * The metaserver maintains two key tables:
 * 	1) filename -> blocks (persitent on disk)
 *  2) block -> dataserver list (received from dataserver via heartbeat messages) 
 * @author lbchen	
 *
 */
public class MetaServer {
	public static final Logger log = Logger.getLogger(MetaServer.class.getName());
	
	
}
