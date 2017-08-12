package org.sstore.server.kms.policy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.sstore.server.kms.KeyStatus;
import org.sstore.utils.Constants;

/**
 * Least frequent used algorithm.
 * 
 * @author lbchen
 *
 */
public class LFU {
	public static String[] select(HashMap<String, KeyStatus> kstatus) {
		/*
		 * Use dictionary sort, count least N data and return them as a list.*
		 */
		@SuppressWarnings("unchecked")
		List<String>[] sortedList = new ArrayList[Constants.DATABUF_SIZE];
		Iterator<String> iter = kstatus.keySet().iterator();
		while (iter.hasNext()) {
			String filename = iter.next();
			KeyStatus status = kstatus.get(filename);
			sortedList[status.getAps()].add(filename);
		}
		// calculate the size of LAA list.
		int listsize = (int) (Constants.DATABUF_SIZE * Constants.RELEASE_PORTION * 2);
		String[] laalist = new String[listsize];
		int count = 0;
		for (int i = 0; i < sortedList.length; i++) {
			if (count >= listsize)
				break;
			if (sortedList[i].size() > 0) {
				for (int j = 0; j < sortedList[i].size(); j++) {
					laalist[count] = sortedList[i].get(j);
					count++;
				}
			}
		}
		return laalist;
	}

}
