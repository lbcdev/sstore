package org.sstore.server.kms;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;
import org.sstore.security.encryption.DataKeyGenerator;
import org.sstore.utils.Constants;

/**
 * Key management server rpc handles all rpc calls for key services.
 * 
 * @author lbchen
 *
 */
public class KMServerRpcImpl implements KMServerRpc {

	private final static Logger log = Logger.getLogger(KMServerRpcImpl.class.getName());

	public void startRpcServer() {
		try {
			KMServerRpcImpl obj = new KMServerRpcImpl();
			KMServerRpc kmstub = (KMServerRpc) UnicastRemoteObject.exportObject(obj, 0);
			Registry registry = LocateRegistry.createRegistry(Constants.KMSPORT);
			registry.bind(Constants.KMSRPC_NAME, kmstub);
			log.info("KMServer RPC ready!");

		} catch (RemoteException | AlreadyBoundException e) {
			log.error(e.getMessage());
		}
	}

	public byte[] requestKey(long fid) throws RemoteException {
		byte[] key = null;
		return key;
	}
	
	public SecretKeySpec requestKey(String remotepath) throws RemoteException {
		DataKeyGenerator keyGen = new DataKeyGenerator();
		SecretKeySpec key = keyGen.gen(remotepath);
		return key;
	}
}
