package org.sstore.server.kms.policy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.sstore.server.kms.KeyStatus;
import org.sstore.utils.Constants;

/**
 * Least recent used algorithm for key caching.
 * 
 * @author lbchen
 *
 */
public class LRU {
	public static String[] select(String[] apsList, HashMap<String, KeyStatus> kstatus) {
		List<String>[] sortedlist = new ArrayList[Constants.LAT_DIST];
		int listsize = (int) (Constants.DATABUF_SIZE * Constants.RELEASE_PORTION);
		String[] latlist = new String[listsize];
		// dictionary sort based on last access time gap.
		for (int i = 0; i < apsList.length; i++) {
			String filename = apsList[i];
			KeyStatus status = kstatus.get(filename);
			long latgap = status.getLastUsed() - System.currentTimeMillis();
			// convert lat gap to seconds.
			latgap = latgap / Constants.LAT_DISTUNIT;
			if (latgap < Constants.LAT_DIST) {
				sortedlist[(int) latgap].add(filename);
			}
		}
		int count = 0;
		for (int i = 0; i < sortedlist.length; i++) {
			if (count >= listsize)
				break;
			if (sortedlist[i].size() > 0) {
				for (int j = 0; j < sortedlist[i].size(); j++) {
					latlist[count] = sortedlist[i].get(j);
					count++;
				}
			}
		}

		return latlist;
	}
}
