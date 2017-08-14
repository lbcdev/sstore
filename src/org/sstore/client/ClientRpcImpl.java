package org.sstore.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;
import org.sstore.server.kms.KMServerRpc;
import org.sstore.server.metaserver.MetaRpc;
import org.sstore.server.storage.DataServerRpc;
import org.sstore.utils.Constants;

/**
 * rpc client handles rpc calls to metaserver and dataserver. The calls include:
 * put, get, and delete.
 * 
 * @author lbchen
 *
 */
public class ClientRpcImpl implements ClientRpc {

	private final static Logger log = Logger.getLogger(ClientRpcImpl.class.getName());
	private Registry registry; // metaserver rpc registry.
	private Registry dsregistry; // dataserver rpc registry.
	private static long clientId;
	private boolean secureMode;

	public ClientRpcImpl(String metahost) {
		try {
			registry = LocateRegistry.getRegistry(metahost);
			registry();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		String metahost = "localhost";
		ClientRpcImpl clientrpc = new ClientRpcImpl(metahost);
		String local = "resources/in.jpg";
		String remote = "in.jpg";
		clientrpc.putReq(local, remote);
		local = "/Users/lbchen/out705.jpg";
		clientrpc.getReq(remote, local);

		remote = "in1.jpg";
		clientrpc.putReq(local, remote);
		local = "/Users/lbchen/out718.jpg";
		clientrpc.getReq(remote, local);
	}

	public void registry() {
		try {
			MetaRpc stub = (MetaRpc) registry.lookup(Constants.METARPC_NAME);
			clientId = stub.registry();
		} catch (NotBoundException | RemoteException e) {
			log.info(e.getMessage());
		}
	}

	/** send get request to metaserver via rpc. */
	public byte[] getReq(String remote, String local) {
		byte[] data = null;
		try {
			MetaRpc stub = (MetaRpc) registry.lookup("metarpc");
			String replicas = stub.findDataServer(remote);
			String primary = replicas.split(",")[0];
			log.info(replicas);
			getFile(remote, local, primary);

		} catch (NotBoundException | RemoteException e) {
			log.error(e.getMessage());
		}
		return data;
	}

	/** request secret key from KMServer */
	public SecretKeySpec keyReq(String remotefile) {
		SecretKeySpec skey = null;
		try {
			KMServerRpc stub = (KMServerRpc) registry.lookup(Constants.KMSRPC_NAME);
			skey = stub.requestKey(remotefile);
			log.info("Get secret key");

		} catch (NotBoundException | RemoteException e) {
			log.error(e.getMessage());
		}
		return skey;
	}

	void getFile(String remote, String local, String primary) {
		int port = Integer.parseInt(primary.split(":")[1]);
		String rpcname = Constants.DATARPC_NAME;
		try {
			final Registry registry = LocateRegistry.getRegistry("localhost", port);
			DataServerRpc stub = (DataServerRpc) registry.lookup(rpcname);
			byte[] data = stub.get(remote, clientId);
			log.info("read data size: " + data.length);
			// writeToLocal(local, data);
		} catch (RemoteException | NotBoundException e) {
			log.error("Client exception: " + e.toString());

		}
	}

	/** send put request to metaserver via rpc. */
	public void putReq(String local, String remote) {
		try {
			final Registry registry = LocateRegistry.getRegistry("localhost");
			MetaRpc stub = (MetaRpc) registry.lookup("metarpc");
			String replicas = stub.assignDataServer(remote);
			if (replicas == null) {
				log.error("No dataserver active.");
				System.exit(0);
			}
			log.info(replicas);
			putFile(local, remote, replicas);
		} catch (RemoteException | NotBoundException e) {
			log.error("Client exception: " + e.toString());

		}
	}

	/** put file to the primary dataserver */
	public void putFile(String local, String remote, String replicas) {

		String[] replicaArr = replicas.split(",");
		if (replicaArr.length > 0) {
			String primary = replicaArr[0];
			int port = Integer.parseInt(primary.split(":")[1]);
			try {
				final Registry registry = LocateRegistry.getRegistry("localhost", port);
				DataServerRpc stub = (DataServerRpc) registry.lookup(Constants.DATARPC_NAME);
				stub.put(remote, getFromLocal(local), clientId);
				/*
				 * command the primary to forward data to replicas if replicas >
				 * 1, reduce client I/O
				 */
				if (replicaArr.length > 1) {
					stub.forwardToReplicas(remote, replicaArr, clientId);
				}
			} catch (RemoteException | NotBoundException e) {
				log.error("Client exception: " + e.toString());
			}
		}
	}

	/** read bytes from local file */
	byte[] getFromLocal(String filename) {
		byte[] data = null;
		try {
			File file = new File(filename);
			FileInputStream in = new FileInputStream(file);
			data = new byte[(int) file.length()];
			log.info("getFromLocal size " + data.length);
			in.read(data);
			in.close();
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		return data;
	}

	/** write bytes to local file */
	void writeToLocal(String local, byte[] bytes) {
		File file = new File(local);
		try {
			FileOutputStream out = new FileOutputStream(file);
			out.write(bytes);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setMode(boolean mode) {
		secureMode = mode;
	}

	public boolean getMode() {
		return secureMode;
	}
}
