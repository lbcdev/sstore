package org.sstore.server.storage;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DataServerRpc extends Remote {

	public void startRpcServer(int port) throws RemoteException;

	public void sendHeartBeat(String host) throws RemoteException;

	public void sendHeartBeat(String host, int port) throws RemoteException;

	public byte[] get(String remote, long clientId) throws RemoteException;

	public void put(String fname, byte[] data, long clientId) throws RemoteException;

	public void forwardToReplicas(String fname, String[] replicas, long clientId) throws RemoteException;

	public String readTest() throws RemoteException;
}
