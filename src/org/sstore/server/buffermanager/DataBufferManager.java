package org.sstore.server.buffermanager;

import java.util.List;

import org.sstore.server.storage.DataBuffer;
import org.sstore.server.storage.DataStatusBuffer;
import org.sstore.utils.Constants;

/**
 * Data buffer manager is responsible for data swapping between buffers and
 * disks on dataservers. Its algorithms aim at keeping "hot" data in the buffer
 * to speed up access.
 * 
 * @author lbchen
 *
 */
public class DataBufferManager implements Runnable {

	private static DataStatusBuffer dsbuffer;

	public DataBufferManager() {
		dsbuffer = new DataStatusBuffer();
	}

	public void run() {
		while (true) {
			autoRelease();
			try {
				Thread.sleep(Constants.RLS_INTERVAL);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * auto-relaase buffer when reaching threshold.
	 */
	public void autoRelease() {
		if (dsbuffer.size() > Constants.RELEASE_THRESHOLD) {
			shuffle();
		}
	}

	/**
	 * scan the whole buffer, release buffer space for new data.
	 */
	public void shuffle() {
		/*
		 * two rounds of greedy algorithms select a subset of buffer to release.
		 */
		String[] lruList = LeastAverageAccess.select(dsbuffer.getBuffer());
		String[] finalList = LastAccessPolicy.select(lruList, dsbuffer.getBuffer());

		/* sync two types of buffers. */
		DataStatusBuffer.removeByCol(finalList);
		DataBuffer.removeByCol(finalList);
	}
}
