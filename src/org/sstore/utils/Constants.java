package org.sstore.utils;

public class Constants {

	public final static int LOCALPORT = 1100;

	/** message type */
	public final static int BLOCKMSG = 1;
	public final static int STATUSMSG = 2;

	/** default dir */
	public final static String METAROOTDIR = "/Users/lbchen/meta/";
	public final static String DATAROOTDIR = "/Users/lbchen/data/";

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

	/** message contents */
	public final static String NODATASERVER_MSG = "No dataserver active";
	public final static String INSUFFICIENT_DS = "No enough dataserver";

	/** key length */
	public final static int DEFAULT_KEY_LENGTH = 16;

	/** buffer */
	// last access time distribution size
	public final static int LAT_DIST = 1000;
	// Intervals to auto-release part of the buffer.
	public final static int RLS_INTERVAL = 2000;
	public final static int DATABUF_SIZE = 500;
	public final static float RELEASE_PORTION = 1 / 4;
	public final static int RELEASE_THRESHOLD = (int) (DATABUF_SIZE * (1 - RELEASE_PORTION));
}
