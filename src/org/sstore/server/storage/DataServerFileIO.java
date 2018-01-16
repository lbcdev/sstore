package org.sstore.server.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;
import org.sstore.security.encryption.CipherHandler;
import org.sstore.server.asyncio.AsyncWrite;
import org.sstore.server.storage.monitor.SecureMonitor;
import org.sstore.server.storage.object.DataObject;
import org.sstore.utils.Constants;

/**
 * DataServerFileIO handles all input/output operations like put, get, and
 * delete on the data.
 * 
 * All operations on files should be written in this class.
 * 
 * @author lbchen
 *
 */
public class DataServerFileIO {

	private static String rootdir = Constants.DATAROOTDIR;
	private final static Logger log = Logger.getLogger(DataServerFileIO.class.getName());
	private DataBuffer buffer;
	private static Hashtable<String, DataObject> objTable;
	protected static Hashtable<String, SecretKeySpec> keyTable;
	private static SecureMonitor secureMonitor;
	private CipherHandler cipherHandler;
	protected static boolean monitorOn;
	private static boolean lazyOn;

	// default constructor.
	public DataServerFileIO() {
		// super();
		buffer = new DataBuffer();
		objTable = new Hashtable<String, DataObject>();
	}

	// constructor with specific sub path.
	public DataServerFileIO(String subpath) {
		rootdir = rootdir + subpath;
		buffer = new DataBuffer();
		objTable = new Hashtable<String, DataObject>();
		keyTable = new Hashtable<String, SecretKeySpec>();
		secureMonitor = SecureMonitor.getInstance();
		monitorOn = false;
		lazyOn = true;
	}

	/** get all files under dir */
	public Set<String> getFiles(String subdir) {
		String dir = rootdir + subdir;
		log.info("Read directory: " + dir);
		File file = new File(rootdir);
		File[] files = file.listFiles();
		Set<String> fileset = new HashSet<String>();
		if (files != null) {
			for (File f : files) {
				fileset.add(f.getName());
			}
		}
		return fileset;
	}

	/** return data bytes in secure mode. */
	public byte[] secureGet(SecretKeySpec skey, String remote) {
		DataObject dataObj = objTable.get(remote);
		log.info("objtable size: " + objTable.size());
		/* check if it is cached and unencrypted. */
		if (dataObj != null) {
			if (!dataObj.isEncrypted()) {
				return dataObj.getData();
			} else {
				cipherHandler = new CipherHandler(skey);
				byte[] data = cipherHandler.decipher(dataObj.getData());
				/*
				 * Atomic operation: cache back unencrypted data and update
				 * metadata
				 */
				synchronized (this) {
					if (lazyOn) {
						dataObj.setData(data);
						dataObj.setTtl(Constants.lazyTTL);
						dataObj.setEncrypted(false);
						objTable.put(remote, dataObj);
						keyTable.put(remote, skey);
					}
					
					/* collect lazy time. */
					if (monitorOn) {
						if (secureMonitor.getLazyTable(remote) != null) {
							int lazyTime = secureMonitor.getLazyTable(remote);
							secureMonitor.putLazyTable(remote, lazyTime + Constants.lazyTTL);
						} else {
							secureMonitor.putLazyTable(remote, Constants.lazyTTL);
						}
					}
				}
				return data;
			}
		}
		/* if not cached. */
		cipherHandler = new CipherHandler(skey);
		byte[] cdata = get(remote);
		byte[] data = cipherHandler.decipher(cdata);
		
		dataObj = new DataObject();
		dataObj.setId(remote);
		if (lazyOn) {
			// synchronized (this) {
			dataObj.setData(data);
			dataObj.setTtl(Constants.lazyTTL);
			dataObj.setEncrypted(false);
			keyTable.put(remote, skey);
			objTable.put(remote, dataObj);
		}
//		else {
//			dataObj.setData(cdata);
//			dataObj.setTtl(0);
//			dataObj.setEncrypted(true);
//		}
		

		if (monitorOn) {
			if (secureMonitor.getLazyTable(remote) != null) {
				int lazyTime = secureMonitor.getLazyTable(remote);
				secureMonitor.putLazyTable(remote, lazyTime + Constants.lazyTTL);
			} else {
				secureMonitor.putLazyTable(remote, Constants.lazyTTL);
			}
		}
		// }
		return data;
	}

	/** return bytes of the requested file by name */
	public byte[] get(String remote) {
		String fname = rootdir + remote;
		File file = new File(fname);
		log.info("get file: " + fname);
		byte[] data = new byte[(int) file.length()];
		log.info("get data length: " + data.length);
		try {
			FileInputStream in = new FileInputStream(file);
			in.read(data);
			in.close();
			return data;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}

	public void asyncPut(String filename, byte[] bytes) {
		// DataBuffer.cache(filename, bytes);
		Thread asyncThread = new Thread(new AsyncWrite(rootdir + filename, bytes));
		asyncThread.start();
	}

	/** write bytes to a file defined by filename */
	public void put(String filename, byte[] bytes) {
		try {
			FileOutputStream out = new FileOutputStream(rootdir + filename);
			out.write(bytes);
			out.flush();
			log.info("put size " + bytes.length);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Hashtable<String, DataObject> getObjTable() {
		return objTable;
	}

	public void updateObjTable(String k, DataObject v) {
		objTable.put(k, v);
	}

	public Hashtable<String, Integer> getMonitorRslts() {

		return null;
	}

	public void monitorOn() {
		monitorOn = true;
	}

	public void monitorOff() {
		monitorOn = false;
	}

	public void startLazyCounter() {
		if (lazyOn) {
			Thread th = new Thread(new LazyCounter());
			th.start();
			log.info("lazyCounter Start.");
		}
	}

	class LazyCounter implements Runnable {

		// public LazyCounter(){
		// DataServerFileIO dsfile = new DataServerFileIO();
		// }
		public void run() {
			while (true) {
				/*
				 * periodically check if lazy time expires, if yes, encrypt
				 * data.
				 */
				// synchronized (this) {

				System.out.println("objTable size: " + objTable.size());

				objTable.forEach((k, v) -> {
					/* if expires */
					if (v.getTtl() > 0) {
						v.setTtl(v.getTtl() - (Constants.lazyTTL / 3));
					} else if (!v.isEncrypted() && keyTable.get(k) != null) {
						SecretKeySpec skey = keyTable.get(k);
						CipherHandler chandle = new CipherHandler(skey);
						byte[] cdata = chandle.cipher(v.getData());
						v.setData(cdata);
						v.setEncrypted(true);
						/* update objTable and remove key from keyTable */
						keyTable.remove(k);
					}
					/* calculate encryption rate */
					int lazyTime = secureMonitor.getLazyTable(k);
					long cTime = v.getCTime();
					float eRate = 1 - (float) lazyTime / (System.currentTimeMillis() - cTime);
					v.seteRate(eRate);
					objTable.put(k, v);
					System.out.println(k + ": " + v.toString());
				});
				try {
					Thread.sleep(Constants.HEARTBEAT_INTERVAL / 10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			// }
		}
	}
}
