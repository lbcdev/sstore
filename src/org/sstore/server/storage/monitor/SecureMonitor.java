package org.sstore.server.storage.monitor;

import java.util.Hashtable;

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
		System.out.println("lazyTable: " + lazyTable.size());
		lazyTable.forEach((k, v) -> {
			System.out.println(k + ":" + v);
		});
	}
}
