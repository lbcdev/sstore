package org.sstore.server.kms.cache;

import java.util.HashMap;

import javax.crypto.spec.SecretKeySpec;

public class KeyCache {

	private volatile HashMap<String, SecretKeySpec> cache;

	public KeyCache() {
		cache = new HashMap<String, SecretKeySpec>();
	}

	public void put(String id, SecretKeySpec skey) {
		if (cache == null) {
			cache = new HashMap<String, SecretKeySpec>();
		}
		cache.put(id, skey);
	}

	public SecretKeySpec get(String id) {
		return cache.get(id);
	}
}
