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
public final class KMServerRpcImpl implements KMServerRpc {

	private final static Logger log = Logger.getLogger(KMServerRpcImpl.class.getName());
	private static KMServerRpcImpl instance = null;
	private static KMServerRpc kmstub;
	private static Registry registry;
	
	private KMServerRpcImpl() {
		
	}
	public static KMServerRpcImpl getInstance() {
		if (instance == null) {
			instance = new KMServerRpcImpl();
		}
		return instance;
	}
	
	public void startRpcServer() {
		try {
			kmstub = (KMServerRpc) UnicastRemoteObject.exportObject(instance, 0);
			registry = LocateRegistry.createRegistry(Constants.KMSPORT);
			registry.bind(Constants.KMSRPC_NAME, kmstub);
			log.info("KMServer RPC ready!");

		} catch (RemoteException | AlreadyBoundException e) {
			log.error(e.getMessage());
		}
	}
	
	public SecretKeySpec requestKey(String remotepath) throws RemoteException {
		log.info("generate key for: " + remotepath);
		DataKeyGenerator keyGen = new DataKeyGenerator();
		SecretKeySpec key = keyGen.gen(remotepath);
		return key;
	}
}
