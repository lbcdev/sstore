package org.sstore.server.storage;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Hashtable;

import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;
import org.sstore.security.encryption.CipherHandler;
import org.sstore.server.buffer.KeyCache;
import org.sstore.server.kms.KMServerRpc;
import org.sstore.server.metaserver.MetaRpc;
import org.sstore.server.storage.monitor.SecureMonitor;
import org.sstore.utils.Constants;
import org.sstore.utils.SstoreConfig;

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
	private SstoreConfig sconfig;
	private Registry kmsRegistry; // dataserver rpc registry.
	private static DataServerFileIO dsfileio;
	private static KeyCache keybuf;
	private static CipherHandler cipherHandler;
	private SecureMonitor smon;
	private String metahost;
	private int localport;
	private static boolean lazyOn = false;
	private HashMap<String, Integer> lazyTable;

	public DataServerRpcImpl() {
		super();
	}

	public DataServerRpcImpl(String metahost, int localport) {
		this.metahost = metahost;
		this.localport = localport;
		String rootpath = "localhost-" + localport + "/";
		dsfileio = new DataServerFileIO(rootpath);
		keybuf = new KeyCache();
		lazyTable = new HashMap<String, Integer>();
		smon = SecureMonitor.getInstance();

		log.info("Register key server");
		sconfig = new SstoreConfig(Constants.CONFIG_PATH);
		lazyOn = sconfig.getBoolean(Constants.SSTORE_LAZY);
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

	/** request secret key from KMServer */
	public SecretKeySpec keyReq(String remotefile) {
		SecretKeySpec skey = null;
		try {
			kmsRegistry = LocateRegistry.getRegistry(metahost, Constants.KMSPORT);
			KMServerRpc stub = (KMServerRpc) kmsRegistry.lookup(Constants.KMSRPC_NAME);
			skey = stub.requestKey(remotefile);
			log.info("Get secret key" + skey);

		} catch (NotBoundException | RemoteException e) {
			log.error(e.getMessage());
		}
		return skey;
	}

	/** forward data to other replicas except the primary itself. */
	public void forwardToReplicas(String filename, String[] replicas, long clientId) {
		byte[] data = dsfileio.get(filename);
		// skip the primary by starting at index 1, method needs to be improved.
		for (int i = 1; i < replicas.length; i++) {
			forward(filename, data, replicas[i], clientId);
		}
	}

	/** forward data to a replica by address */
	public void forward(String remote, byte[] data, String hostaddr, long clientId) {
		String[] addrarr = hostaddr.split(":");
		String host = addrarr[0];
		int port = Integer.parseInt(addrarr[1]);
		try {
			final Registry dsregistry = LocateRegistry.getRegistry(host, port);
			DataServerRpc stub = (DataServerRpc) dsregistry.lookup(Constants.DATARPC_NAME);
			stub.put(remote, data, clientId);
		} catch (RemoteException | NotBoundException e) {
			log.info(e.getMessage());
		}
	}

	public byte[] get(String remote, long clientId) {
		return dsfileio.get(remote);
	}

	public byte[] secureGet(String remote) {
//		System.out.println("lazy state: " + lazyOn);

		// quick get if the data is lazy.
		if (lazyOn) {
			if (dsfileio.lazyGet(remote) != null) {
				return dsfileio.lazyGet(remote);
			}
		}
//		System.out.println(remote + " lazyGet misses");
		// otherwise get a key first, then proceed.
		SecretKeySpec skey = keyReq(remote);
		return dsfileio.secureGet(skey, remote);
	}

	public void put(String filename, byte[] data, long clientId) {
		// if (secureMode) {
		// securePut(filename, data, clientId);
		// } else {
		dsfileio.asyncPut(filename, data);
		// }
	}

	public void securePut(SecretKeySpec skey, String filename, byte[] data) {
		// DataKeyGenerator keyGen = new DataKeyGenerator();
		// byte[] key = keyGen.genKey(filename, clientId,
		// Constants.DEFAULT_KEY_LENGTH);
		cipherHandler = new CipherHandler(skey);
		byte[] cdata = cipherHandler.cipher(data);
		log.info("generate ecp key " + skey + " for " + filename);
		/* cache encryption key for files */
		// keybuf.cache(filename, key);
		dsfileio.asyncPut(filename, cdata);
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
			boolean flag = stub.getSecureMode();
			// secureMode = flag;
			log.info(response);

		} catch (RemoteException | NotBoundException e) {
			log.error("Client exception: " + e.toString());
		}
	}

	/**
	 * thread to send heart beat every N seconds. print out lazy table for test.
	 */
	public void run() {
		while (true) {
			smon.printLazyTable();
			sendHeartBeat(metahost, localport);
			try {
				Thread.sleep(Constants.HEARTBEAT_INTERVAL);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public Hashtable<String, Integer> monitorRs() {
		return dsfileio.getMonitorRslts();
	}

	public void monitorOn() {

		dsfileio.monitorOn();
	}

	public void monitorOff() {

		dsfileio.monitorOff();
	}

	public String readTest() {
		return "read from dataserver rpc";
	}

	class LazyCountDown implements Runnable {
		public void run() {
			while (true) {
				lazyTable.forEach((k, v) -> {
					if (v > 0) {
						v--;
					}
				});
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
