package org.sstore.server.buffermanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.sstore.server.storage.BufferedDataStatus;
import org.sstore.utils.Constants;

public class LastAccessPolicy {

	public static String[] select(List<String> apsList, HashMap<String, BufferedDataStatus> dsbuffer) {
		List<String>[] sortedlist = new ArrayList[Constants.LAT_DIST];
		
		return null;
	}
}
