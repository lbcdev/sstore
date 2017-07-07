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
}
