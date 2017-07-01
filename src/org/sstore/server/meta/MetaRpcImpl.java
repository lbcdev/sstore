package org.sstore.server.meta;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;
import org.sstore.structure.Block;
/**
 * This class handles all rpc calls.
 * It is created, started, and stopped by {@link MetaServer}.
 * @author lbchen
 *
 */
public class MetaRpcImpl implements MetaRpc {
	
	private final static Logger log = Logger.getLogger(MetaRpcImpl.class.getName());
	
	public void startRpcServer() {
		try {
			MetaRpcImpl obj = new MetaRpcImpl();
			MetaRpc stub = (MetaRpc) UnicastRemoteObject.exportObject(obj, 0);
			Registry registry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
			registry.bind("metarpc", stub);
			log.info("RPC server ready");

		} catch (Exception e) {
			log.error("RPC server err: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public byte[] readFile(List<Block> blks, int start, int end) throws RemoteException {
		String test = "read from rpc";
		return test.getBytes();
	}
	public String readTest(){
		return "read from rpc";
	}
}
