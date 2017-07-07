package org.sstore.server.storage;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import org.apache.log4j.Logger;
import org.sstore.server.meta.MetaRpc;

public class DataServerRpcImpl implements DataServerRpc, Runnable {

	private final static Logger log = Logger.getLogger(DataServerRpc.class.getName());
	private static DataServerFileIO dsfileio = new DataServerFileIO();
	private String metahost;
	private int localport;

	public DataServerRpcImpl() {
		super();
	}

	public DataServerRpcImpl(String metahost, int localport) {
		this.metahost = metahost;
		this.localport = localport;
	}

	public void startRpcServer(int port) {
		try {
			DataServerRpcImpl obj = new DataServerRpcImpl();
			DataServerRpc stub = (DataServerRpc) UnicastRemoteObject.exportObject(obj, 0);
			Registry registry = LocateRegistry.createRegistry(port);
			registry.bind("dsrpc", stub);
			log.info("Dataserver RPC ready");

		} catch (Exception e) {
			log.error("Dataserver RPC err: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public byte[] get(String remote) {
		return dsfileio.get(remote);
	}

	public void put(String fname, byte[] data) {
		dsfileio.put(fname, data);
	}

	public void sendHeartBeat(String metahost) {
		sendHeartBeat(metahost, 0);
	}

	public void sendHeartBeat(String metahost, int localport) {
		try {
			// System.setSecurityManager(new RMISecurityManager());
			final Registry registry = LocateRegistry.getRegistry(metahost);
			MetaRpc stub = (MetaRpc) registry.lookup("metarpc");

			DataServer dataserver = new DataServer();
			String response = stub.heartBeat(dataserver.buildBlockMessage());
			log.info(response);

		} catch (RemoteException | NotBoundException e) {
			log.error("Client exception: " + e.toString());
		}
	}

	public void run() {
		while (true) {
			sendHeartBeat(metahost, localport);
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public String readTest() {
		return "read from dataserver rpc";
	}
}
