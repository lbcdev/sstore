package org.sstore.server.kms;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;

import org.apache.log4j.Logger;

/**
 * Key management server rpc handles all rpc calls for key services.
 * 
 * @author lbchen
 *
 */
public class KMServerRpcImpl implements KMServerRpc {
	private final static Logger log = Logger.getLogger(KMServerRpcImpl.class.getName());

	public void startRpcServer(){
		Registry registry = LocalRegistry
	}
	public byte[] requestKey(long fid) throws RemoteException {
		byte[] key = null;
		return key;
	}
}
