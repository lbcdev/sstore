package org.sstore.server.storage;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Hashtable;

import javax.crypto.spec.SecretKeySpec;

public interface DataServerRpc extends Remote {

	public void startRpcServer(int port) throws RemoteException;

	public void sendHeartBeat(String host) throws RemoteException;

	public void sendHeartBeat(String host, int port) throws RemoteException;

	public byte[] get(String remote, long clientId) throws RemoteException;

	public byte[] secureGet(SecretKeySpec skey, String remote) throws RemoteException;
	
	public void securePut(SecretKeySpec skey, String fname, byte[] data) throws RemoteException;
		
	public void put(String fname, byte[] data, long clientId) throws RemoteException;

	public void forwardToReplicas(String fname, String[] replicas, long clientId) throws RemoteException;

	public Hashtable<String, Integer> monitorRs() throws RemoteException;
	
	public void monitorOn() throws RemoteException;
	
	public void monitorOff() throws RemoteException;

	public String readTest() throws RemoteException;
}
