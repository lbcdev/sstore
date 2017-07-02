package org.sstore.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.apache.log4j.Logger;
import org.sstore.server.meta.MetaRpc;
import org.sstore.server.storage.DataServerRpc;

public class ClientRpcImpl implements ClientRpc {

	private final static Logger log = Logger.getLogger(ClientRpcImpl.class.getName());

	public static void main(String[] args) {
		ClientRpcImpl clientrpc = new ClientRpcImpl();
		String local = "resources/in1.jpg";
		String remote = "in1.jpg";
		String hostaddr = "localhost:1100";
		clientrpc.putFile(local, remote, hostaddr);
	}
	/** send put request to metaserv4er */
	public void putReq(String localpath) {
		try {
			final Registry registry = LocateRegistry.getRegistry("localhost");
			MetaRpc stub = (MetaRpc) registry.lookup("metarpc");
			String response = stub.readTest();
			log.info(response);
		} catch (RemoteException | NotBoundException e) {
			log.error("Client exception: " + e.toString());

		}
	}
	
	/** put file to dataserver */
	public void putFile(String local, String remote, String hostaddr) {

		int port = Integer.parseInt(hostaddr.split(":")[1]);
		String rpcname = "dsrpc";
		try {
			final Registry registry = LocateRegistry.getRegistry("localhost", port);
			DataServerRpc stub = (DataServerRpc) registry.lookup(rpcname);
			stub.put(remote, getFileData(local));
		} catch (RemoteException | NotBoundException e) {
			log.error("Client exception: " + e.toString());

		}
	}

	byte[] getFileData(String filename) {
		byte[] data = null;
		try {
			File file = new File(filename);
			FileInputStream in = new FileInputStream(file);
			data = new byte[(int)file.length()];
			in.read(data);
			in.close();
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		return data;
	}
}
