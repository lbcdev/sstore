package org.sstore.server.buffermanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.sstore.server.storage.BufferedDataStatus;
import org.sstore.utils.Constants;

/**
 * This provides sort algorithm for all data in the buffer based on last access
 * time.
 * 
 * @author lbchen
 *
 */
public class LeastAverageAccess {

	public static List<String> select(HashMap<String, BufferedDataStatus> dsbuffer) {

		/*
		 * Use dictionary sort, count least N data and return them as a list.*
		 */
		@SuppressWarnings("unchecked")
		List<String>[] sortedList = new ArrayList[Constants.DATABUF_SIZE];
		Iterator<String> iter = dsbuffer.keySet().iterator();
		while (iter.hasNext()) {
			String filename = iter.next();
			BufferedDataStatus dstatus = dsbuffer.get(filename);
			sortedList[dstatus.getAps()].add(filename);
		}
		// calculate the size of LAA list.
		int listsize = (int) (Constants.DATABUF_SIZE * Constants.RELEASE_PORTION * 2);
		List<String> laalist = new ArrayList<String>();
		int count = 0;
		for (int i = 0; i < sortedList.length; i++) {
			if (count > listsize)
				break;
			if (sortedList[i].size() > 0) {
				for (int j = 0; j < sortedList[i].size(); j++) {
					laalist.add(sortedList[i].get(j));
					count++;
				}
			}
		}
		return laalist;
	}

	private void sort(HashMap<String, BufferedDataStatus> selectedBuffer) {

	}
}
