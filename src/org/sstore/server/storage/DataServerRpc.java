package org.sstore.server.storage;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DataServerRpc extends Remote{
	
	public void startRpcServer() throws RemoteException;
	
	public void sendHeartBeat() throws RemoteException;
	
	public void put(String fname, byte[] data) throws RemoteException;
	
	public String readTest() throws RemoteException;
}
