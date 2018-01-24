package org.sstore.server.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;
import org.sstore.security.encryption.CipherHandler;
import org.sstore.server.asyncio.AsyncWrite;
import org.sstore.server.storage.eviction.RandomLRU;
import org.sstore.server.storage.monitor.SecureMonitor;
import org.sstore.server.storage.object.DataObject;
import org.sstore.utils.Constants;
import org.sstore.utils.SstoreConfig;

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
	private SstoreConfig sconfig;
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

		sconfig = new SstoreConfig(Constants.CONFIG_PATH);
		lazyOn = sconfig.getBoolean(Constants.SSTORE_LAZY);
		monitorOn = sconfig.getBoolean(Constants.SSTORE_MONITOR);
		log.info("lazy state: " + lazyOn);
		log.info("monitor state: " + monitorOn);

		startLazyCounter();
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

	/**
	 * Quick check if the data is lazy and return it if true.
	 * 
	 * @return
	 */
	public byte[] lazyGet(String remote) {
		DataObject dataObj = objTable.get(remote);
		if (dataObj != null) {
			log.info(remote + "lazyGet hits ");
			// record object's last operation time.
			dataObj.setLastOper(System.currentTimeMillis());
			objTable.put(remote, dataObj);
			return dataObj.getData();
		}
		return null;
	}

	/** return data bytes in secure mode. */
	public byte[] secureGet(SecretKeySpec skey, String remote) {
		DataObject dataObj = objTable.get(remote);
		log.info("lazy state: " + lazyOn);
		log.info("objtable size: " + objTable.size());

		// printObjTable();

		/* check if it is cached and unencrypted. */
		if (dataObj != null) {
			byte[] data = null;
			if (!dataObj.isEncrypted()) {
				log.info(remote + " hits");
				data = dataObj.getData();
			} else {
				cipherHandler = new CipherHandler(skey);
				data = cipherHandler.decipher(dataObj.getData());
				/*
				 * Atomic operation: cache back unencrypted data and update
				 * metadata
				 */
				synchronized (this) {
					if (lazyOn) {
						dataObj.setData(data);
						dataObj.setTtl(Constants.lazyTTL);
						dataObj.setEncrypted(false);
						keyTable.put(remote, skey);
						log.info(remote + " is lazy now.");

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
				}
			}
			// record object's last operation time.
			dataObj.setLastOper(System.currentTimeMillis());
			objTable.put(remote, dataObj);
			return data;
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
			// System.out.println(remote + " is lazy now.");

			keyTable.put(remote, skey);
			if (monitorOn) {
				if (secureMonitor.getLazyTable(remote) != null) {
					int lazyTime = secureMonitor.getLazyTable(remote);
					secureMonitor.putLazyTable(remote, lazyTime + Constants.lazyTTL);
				} else {
					secureMonitor.putLazyTable(remote, Constants.lazyTTL);
				}
			}
		} else {
			dataObj.setData(cdata);
			dataObj.setTtl(0);
			dataObj.setEncrypted(true);
		}
		dataObj.setLastOper(System.currentTimeMillis());
		// if the cache is full, eviction some then proceed.
		if (objTable.size() >= Constants.OBJTABLE_SIZE) {
			objTableEviction();
		}
		objTable.put(remote, dataObj);
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

	public void printObjTable() {
		objTable.forEach((k, v) -> {
			System.out.println(v);
		});
	}

	public synchronized void objTableEviction() {
		List<String> evictList = RandomLRU.select(objTable);
		log.info("Eviction size: " + evictList.size());
		for (String id : evictList) {
			log.info("Evict " + id);
			objTable.remove(id);
		}
	}

	public static Hashtable<String, DataObject> getObjTable() {
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
		/**
		 * The the average encryption rate of objects.
		 * 
		 * @return
		 */
		public float getAvgRate() {
			float total = 0f;
			Map<String, DataObject> objTable = DataServerFileIO.getObjTable();
			float size = objTable.size();
			Collection<DataObject> objects = objTable.values();
			for (DataObject obj : objects) {
				float rate = obj.geteRate();
				total = total + rate;
			}
			return total / size;
		}

		/**
		 * The maximum encryption rate.
		 * 
		 * @return
		 */
		public float getMinRate() {
			float minRate = 1f;
			Map<String, DataObject> objTable = DataServerFileIO.getObjTable();
			Collection<DataObject> objects = objTable.values();
			for (DataObject obj : objects) {
				float rate = obj.geteRate();
				minRate = Math.min(rate, minRate);
			}
			return minRate;
		}

		public void run() {
			while (true) {
				/*
				 * periodically check if lazy time expires, if yes, encrypt
				 * data.
				 */
				try {
					Thread.sleep(Constants.lazyTTL);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
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
					if (monitorOn) {
						int lazyTime = secureMonitor.getLazyTable(k);
						long cTime = v.getCTime();
						long eclipse = System.currentTimeMillis() - cTime;
						float eRate = 0f;
						if (lazyTime <= eclipse) {
							eRate = 1 - (float) lazyTime / eclipse;
						}
						v.seteRate(eRate);
					}
					objTable.put(k, v);
				});
				if (objTable.size() > 0) {
					printObjTable();
					float minRate = getMinRate();
					float avgRate = getAvgRate();
					System.out.println("Min. rate: " + minRate);
					System.out.println("Avg. rate: " + avgRate);
				}

			}
			// }
		}
	}
}
