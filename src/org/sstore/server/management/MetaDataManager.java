package org.sstore.server.management;

/**
 * Metadata manager manages key tables such as file-to-replicas mapping,
 * dataserver status, and etc. It removes stale table entries when dataservers
 * fail or files are remove.
 * 
 * @author lbchen
 *
 */
public class MetaDataManager implements Runnable {

	public void run(){
		
	}
	/** detect failed dataserver. */
	public void checkDSStatus(){
		
	}

	/**
	 * clean up F2DSTable based on failed dataserver.
	 * 
	 * @param dsname
	 */
	public void cleanF2DSTableByDS(String dsname) {

	}
}
