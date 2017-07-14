package org.sstore.utils;

public class Constants {
	
	public final static int LOCALPORT = 1100;
	
	/** message type */
	public final static int BLOCKMSG = 1;
	public final static int STATUSMSG = 2;
	
	/** default dir */
	public final static String METAROOTDIR = "/Users/lbchen/meta/";
	
	/** replication policy */
	public final static String TRIPLE_REPLIC = "triple-r";
	public final static String ERASURE_CODING = "erasure";
	public final static int REPLIC_FACTOR = 3;
	
	// Heartbeat interval in milliseconds.
	public final static int HEARTBEAT_INTERVAL = 10000;
	// default TTL value = 3 times of heartbeat interval.
	public final static int HEARTBEAT_TTL = 1;
	
	public final static String METARPC_NAME = "metarpc";
	public final static String DATARPC_NAME = "dsrpc";
	
	/** message */
	public final static String NODATASERVER_MSG = "No dataserver active";
	public final static String INSUFFICIENT_DS = "No enough dataserver";
	
}
