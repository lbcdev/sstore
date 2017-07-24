package org.sstore.server.buffermanager;

import org.sstore.server.storage.DataStatusBuffer;

/**
 * Data buffer manager is responsible for data swapping between buffers and
 * disks on dataservers. Its algorithms aim at keeping "hot" data in the buffer
 * to speed up access.
 * 
 * @author lbchen
 *
 */
public class DataBufferManager {

	private static DataStatusBuffer dsbuffer;
	
	
	public DataBufferManager() {
		dsbuffer = new DataStatusBuffer();
	}

	/**
	 * scan the whole buffer, release buffer space for new data.
	 */
	public void shuffle() {
		String[] sortedApsList = LeastRecentAccess.sort(dsbuffer);
		String[] finalList = LastAccessPolicy.sort(sortedApsList, dsbuffer);
		
		DataStatusBuffer.removeByCol(finalList);
		DataBuffer.removeByCol(finalList);
	}
}
