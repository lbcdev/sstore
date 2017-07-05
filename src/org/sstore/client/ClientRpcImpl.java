package org.sstore.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
	private Registry registry; // metaserver rpc registry.
	private Registry dsregistry; // dataserver rpc registry.

	public ClientRpcImpl() {
		try {
			registry = LocateRegistry.getRegistry("localhost");
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		ClientRpcImpl clientrpc = new ClientRpcImpl();
		String local = "resources/in1.jpg";
		String remote = "in704.jpg";
		String hostaddr = "localhost:1100";
		clientrpc.putReq(local, remote);
		local = "/Users/lbchen/out704.jpg";
		clientrpc.getReq(remote, local);
	}

	public byte[] getReq(String remote, String local) {
		byte[] data = null;
		try {
			MetaRpc stub = (MetaRpc) registry.lookup("metarpc");
			String hostaddr = stub.findDataServer(remote);
			log.info(hostaddr);
			getFile(remote, local, hostaddr);
			
		} catch (NotBoundException | RemoteException e) {
			log.error(e.getMessage());
		}
		return data;
	}

	void getFile(String remote, String local, String hostaddr) {
		int port = Integer.parseInt(hostaddr.split(":")[1]);
		String rpcname = "dsrpc";
		try {
			final Registry registry = LocateRegistry.getRegistry("localhost", port);
			DataServerRpc stub = (DataServerRpc) registry.lookup(rpcname);
			writeToLocal(local, stub.get(remote));
		} catch (RemoteException | NotBoundException e) {
			log.error("Client exception: " + e.toString());

		}
	}

	/** send put request to metaserv4er */
	public void putReq(String local, String remote) {
		try {
			final Registry registry = LocateRegistry.getRegistry("localhost");
			MetaRpc stub = (MetaRpc) registry.lookup("metarpc");
			String hostaddr = stub.assignDataServer(remote);
			log.info(hostaddr);
			putFile(local, remote, hostaddr);
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
			stub.put(remote, getFromLocal(local));
		} catch (RemoteException | NotBoundException e) {
			log.error("Client exception: " + e.toString());

		}
	}

	/** read bytes from local file */
	byte[] getFromLocal(String filename) {
		byte[] data = null;
		try {
			File file = new File(filename);
			FileInputStream in = new FileInputStream(file);
			data = new byte[(int) file.length()];
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
}
