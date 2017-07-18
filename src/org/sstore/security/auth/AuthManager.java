package org.sstore.security.auth;

/**
 * Authentication manager authenticate client programs and control their access.
 * 
 * It has several key tasks:
 * 
 * 1) It registers client programs and assigns them unique client ID, which is
 * the key to all data operations.
 * 
 * @author lbchen
 *
 */
public class AuthManager {

	/** client registry method, returns unique client id. */
	public static long registry() {
		return System.currentTimeMillis();
	}

	
}
