package org.sstore.server.storage.monitor;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;

import org.sstore.server.storage.DataServerFileIO;
import org.sstore.server.storage.object.DataObject;

/**
 * Monitor security factor of objects, i.e., encryption rate. Singleton class to
 * prevent multiple instantiation.
 * 
 * @author lbchen
 *
 */
public class SecureMonitor {

	private static SecureMonitor instance = null;
	private Hashtable<String, Integer> lazyTable;

	private SecureMonitor() {
		lazyTable = new Hashtable<String, Integer>();
	}

	public static SecureMonitor getInstance() {

		if (instance == null) {
			instance = new SecureMonitor();
		}
		return instance;
	}

	public Integer getLazyTable(String oid) {
		return lazyTable.get(oid);
	}

	public void putLazyTable(String oid, int ttl) {
		lazyTable.put(oid, ttl);
	}

	public void printLazyTable() {
//		System.out.println("lazyTable: " + lazyTable.size());
//		lazyTable.forEach((k, v) -> {
//			System.out.println(k + ":" + v);
//		});
	}

	/**
	 * The the average encryption rate of objects.
	 * @return
	 */
	public float getAvgRate() {
		float total = 0f;
		Map<String, DataObject> objTable = DataServerFileIO.getObjTable();
		float size = objTable.size();
		Collection<DataObject> objects = objTable.values();
		for (DataObject obj : objects) {
			float rate = obj.geteRate();
			total = total + rate;
		}
		return total / size;
	}

	/**
	 * The maximum encryption rate.
	 * @return
	 */
	public float getMinRate() {
		float minRate = 0f;
		Map<String, DataObject> objTable = DataServerFileIO.getObjTable();
		Collection<DataObject> objects = objTable.values();
		for (DataObject obj : objects) {
			float rate = obj.geteRate();
			minRate = Math.min(rate, minRate);
		}
		return minRate;
	}

	public float getMaxRate() {

		return 0f;
	}
}
