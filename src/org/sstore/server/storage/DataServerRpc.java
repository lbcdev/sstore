package org.sstore.server.storage;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DataServerRpc extends Remote {

	public void startRpcServer(int port) throws RemoteException;

	public void sendHeartBeat(String host) throws RemoteException;

	public void sendHeartBeat(String host, int port) throws RemoteException;

	public byte[] get(String remote) throws RemoteException;

	public void put(String fname, byte[] data) throws RemoteException;

	public void forwardToReplicas(String fname, String[] replicas) throws RemoteException;

	public String readTest() throws RemoteException;
}
