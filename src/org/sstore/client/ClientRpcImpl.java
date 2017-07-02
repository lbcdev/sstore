package org.sstore.client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.apache.log4j.Logger;
import org.sstore.server.meta.MetaRpc;
import org.sstore.server.storage.DataServerRpc;

public class ClientRpcImpl implements ClientRpc{
	
	private final static Logger log = Logger.getLogger(ClientRpcImpl.class.getName());
	
	public static void main(String[] args){
		try {
			// System.setSecurityManager(new RMISecurityManager());
			final Registry registry = LocateRegistry.getRegistry("localhost", 1100);
			DataServerRpc stub = (DataServerRpc) registry.lookup("dsrpc");
			String response = stub.readTest();
			log.info(response);
//			HelloRMI obj = (HelloRMI) Naming.lookup("//localhost/HelloRMIImpl");
//			System.out.println(obj.sayHello("lbc"));

		} catch (Exception e) {
			log.error("Client exception: " + e.toString());

		}
	}
	
	public void putReq(String localpath){
		try {
			final Registry registry = LocateRegistry.getRegistry("localhost");
			MetaRpc stub = (MetaRpc) registry.lookup("metarpc");
			String response = stub.readTest();
			log.info(response);
		} catch (Exception e) {
			log.error("Client exception: " + e.toString());

		}
	}
	
}
