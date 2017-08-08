package org.sstore.server.buffermanager;

import org.sstore.server.buffer.KeyCache;
import org.sstore.server.storage.DataBuffer;
import org.sstore.server.storage.DataStatusBuffer;
import org.sstore.utils.Constants;

public class KeyCacheManager implements Runnable {
	private KeyCache kcache;

	public KeyCacheManager() {
		kcache = new KeyCache();
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
		if (kcache.size() > Constants.RELEASE_THRESHOLD) {
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
		String[] laaList = LeastAverageAccess.select(kcache.getCache());
		String[] finalList = LastAccessPolicy.select(laaList, kcache.getCache());

		/* sync two types of buffers. */
		DataStatusBuffer.removeByCol(finalList);
		DataBuffer.removeByCol(finalList);
	}
}
