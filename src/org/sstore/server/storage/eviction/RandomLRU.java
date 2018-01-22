package org.sstore.server.storage.eviction;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.sstore.server.storage.object.DataObject;
import org.sstore.utils.Constants;

/**
 * Random LRU policy for object-table eviction.
 * 
 * @author lbchen
 *
 */
public class RandomLRU {

	public static List<String> select(Hashtable<String, DataObject> objTable) {

		List<String> evictedList = new ArrayList<String>();
		Random rand = new Random(System.currentTimeMillis());
		int evictNum = (int) (objTable.size() * Constants.RELEASE_PORTION);
		Object[] objIds = objTable.keySet().toArray();
		
		while(evictNum-- > 0) {
			int id = rand.nextInt(objIds.length);
			String oldestId = (String) objIds[id];
			long oldest = objTable.get(oldestId).getLastOper();
			for(int i = 1; i <= Constants.RANDOMLRU_RANGE; i++) {
				String newId = (String) objIds[(i + id + evictNum) % objIds.length];
				long newTime = objTable.get(newId).getLastOper();
				if (newTime < oldest) {
					oldest = newTime;
					oldestId = newId;
				}
			}
			evictedList.add(oldestId);
		}

		return evictedList;
	}
}
