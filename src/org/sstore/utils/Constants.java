package org.sstore.utils;

public class Constants {

	/** ports */
	public static int LOCALPORT = 1100;
	public static int KMSPORT = 4001;

	/** root key */
	public static String ROOT_KEY = "fdjifE8@9*idfie1488dfidk3483483dfdjk";

	/** message type */
	public final static int BLOCKMSG = 1;
	public static int STATUSMSG = 2;

	/** default dir */
	public final static String METAROOTDIR = "/Users/lbchen/data/meta/";
	public final static String DATAROOTDIR = "/Users/lbchen/data/";

	/** replication policy */
	public final static String TRIPLE_REPLIC = "triple-r";
	public final static String ERASURE_CODING = "erasure";
	public final static int REPLIC_FACTOR = 3;

	// Heartbeat interval in milliseconds.
	public final static int HEARTBEAT_INTERVAL = 5000;
	// default TTL value = 3 times of heartbeat interval.
	public final static int HEARTBEAT_TTL = 1;

	public final static String METARPC_NAME = "metarpc";
	public final static String DATARPC_NAME = "dsrpc";
	public final static String KMSRPC_NAME = "kmsrpc";

	/** message contents */
	public final static String NODATASERVER_MSG = "No dataserver active";
	public final static String INSUFFICIENT_DS = "No enough dataserver";
	public final static String FILENOTFOUND = "file not found";
	
	/** key length */
	public final static int DEF_KEY_LEN = 32;

	/** object table */
	// last access time distribution unit, 1000 for seconds, 60000 for mins,
	// etc.
	public final static int LAT_DISTUNIT = 1000;
	// last access time distribution size
	public final static int LAT_DIST = 1000;
	// Intervals to auto-release part of the buffer.
	public final static int RLS_INTERVAL = 2000;
	public final static int DATABUF_SIZE = 500;
	public final static float RELEASE_PORTION = 0.1f;
	public final static int RELEASE_THRESHOLD = (int) (DATABUF_SIZE * (1 - RELEASE_PORTION));
	// random LRU pick range.
	public final static int RANDOMLRU_RANGE = 5;
	public final static int OBJTABLE_SIZE = 80;
	
	
	/** lazy config */
	public final static int lazyTTL = 325;
	
	/* Configuration */
	public final static String CONFIG_PATH = "sstore.properties";
	public final static String SSTORE_LAZY = "sstore.lazy";
	public final static String SSTORE_MONITOR = "sstore.monitor";
}
