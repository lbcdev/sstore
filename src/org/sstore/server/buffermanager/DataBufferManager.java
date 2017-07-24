package org.sstore.server.buffermanager;

/**
 * Data buffer manager is responsible for data swapping between buffers and
 * disks on dataservers. Its algorithms aim at keeping "hot" data in the buffer
 * to speed up access.
 * 
 * @author lbchen
 *
 */
public class DataBufferManager {

	/**
	 * scan the whole buffer, release buffer space for new data.
	 */
	public void shuffle() {
		
	}
}
