package org.sstore.server.kms;

import java.rmi.Remote;
import java.rmi.RemoteException;

import javax.crypto.spec.SecretKeySpec;

/**
 * KMS rpc interface, defines all rpc calls.
 * 
 * @author lbchen
 *
 */
public interface KMServerRpc extends Remote {
	
//	public byte[] requestKey(long fileId) throws RemoteException;
	
	public SecretKeySpec requestKey(String fileId) throws RemoteException;

}
