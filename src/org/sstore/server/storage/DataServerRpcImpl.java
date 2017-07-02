package org.sstore.server.storage;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import org.apache.log4j.Logger;
import org.sstore.server.meta.MetaRpc;
import org.sstore.server.meta.MetaRpcImpl;

public class DataServerRpcImpl implements DataServerRpc, Runnable {

	private final static Logger log = Logger.getLogger(DataServerRpc.class.getName());

	private final static int port = 1100;
	
	public void startRpcServer() {
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
	
	public void sendHeartBeat() {
		try {
			// System.setSecurityManager(new RMISecurityManager());
			final Registry registry = LocateRegistry.getRegistry("localhost");
			MetaRpc stub = (MetaRpc) registry.lookup("metarpc");

			DataServer dataserver = new DataServer();
//			String bmsg = "1,1,{1,2,3,4}";
			String response = stub.heartBeat(dataserver.buildBlockMessage());
			log.info(response);
			// HelloRMI obj = (HelloRMI)
			// Naming.lookup("//localhost/HelloRMIImpl");
			// System.out.println(obj.sayHello("lbc"));

		} catch (RemoteException | NotBoundException e) {
			log.error("Client exception: " + e.toString());

		}
	}

	public void run() {
		while (true) {
			sendHeartBeat();
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
