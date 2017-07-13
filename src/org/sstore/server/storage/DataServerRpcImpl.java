package org.sstore.server.storage;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import org.apache.log4j.Logger;
import org.sstore.security.encryption.CipherHandler;
import org.sstore.server.metaserver.MetaRpc;
import org.sstore.utils.Constants;

/**
 * dataserver rpc handles all calls from clients and sends heartbeat message to
 * metaserver.
 * 
 * It runs heartbeat as a thread in background.
 * 
 * @author lbchen
 *
 */
public class DataServerRpcImpl implements DataServerRpc, Runnable {

	private final static Logger log = Logger.getLogger(DataServerRpc.class.getName());
	private static DataServerFileIO dsfileio;
	private CipherHandler cipherHandler;
	private String metahost;
	private int localport;
	private String seckey = "sstore";

	public DataServerRpcImpl() {
		super();
	}

	public DataServerRpcImpl(String metahost, int localport) {
		this.metahost = metahost;
		this.localport = localport;
		String rootpath = "localhost-" + localport + "/";
		dsfileio = new DataServerFileIO(rootpath);
		cipherHandler = new CipherHandler(seckey);
	}

	public void startRpcServer(int port) {
		try {
			DataServerRpcImpl obj = new DataServerRpcImpl();
			DataServerRpc stub = (DataServerRpc) UnicastRemoteObject.exportObject(obj, 0);
			Registry registry = LocateRegistry.createRegistry(port);
			registry.bind(Constants.DATARPC_NAME, stub);
			log.info("Dataserver RPC ready");

		} catch (Exception e) {
			log.error("Dataserver RPC err: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/** forward data to other replicas except the primary itself. */
	public void forwardToReplicas(String filename, String[] replicas) {
		byte[] data = dsfileio.get(filename);
		// skip the primary by starting at index 1, method needs to be improved.
		for (int i = 1; i < replicas.length; i++) {
			forward(filename, data, replicas[i]);
		}
	}

	/** forward data to a replica by address */
	public void forward(String remote, byte[] data, String hostaddr) {
		String[] addrarr = hostaddr.split(":");
		String host = addrarr[0];
		int port = Integer.parseInt(addrarr[1]);
		try {
			final Registry dsregistry = LocateRegistry.getRegistry(host, port);
			DataServerRpc stub = (DataServerRpc) dsregistry.lookup(Constants.DATARPC_NAME);
			stub.put(remote, data);
		} catch (RemoteException | NotBoundException e) {
			log.info(e.getMessage());
		}
	}

	public byte[] get(String remote) {
		return cipherHandler.decipher(dsfileio.get(remote));
	}

	public void put(String fname, byte[] data) {

		dsfileio.put(fname, cipherHandler.cipher(data));
	}

	public void sendHeartBeat(String metahost) {
		sendHeartBeat(metahost, 0);
	}

	/** send heartbeat message to metaserver, including dataserver status. */
	public void sendHeartBeat(String metahost, int localport) {
		try {
			final Registry registry = LocateRegistry.getRegistry(metahost);
			MetaRpc stub = (MetaRpc) registry.lookup(Constants.METARPC_NAME);

			DataServer dataserver = new DataServer();
			String response = stub.heartBeat(dataserver.buildHBMessage());
			log.info(response);

		} catch (RemoteException | NotBoundException e) {
			log.error("Client exception: " + e.toString());
		}
	}

	/** thread to send heart beat every N seconds. */
	public void run() {
		while (true) {
			sendHeartBeat(metahost, localport);
			try {
				Thread.sleep(Constants.HEARTBEAT_INTERVAL);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public String readTest() {
		return "read from dataserver rpc";
	}
}
