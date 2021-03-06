package org.sstore.server.metaserver;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import org.sstore.protocol.Block;
import org.sstore.protocol.BlockMessage;

public interface MetaRpc extends Remote {

	public long registry() throws RemoteException;
	
	public boolean getSecureMode() throws RemoteException;
	
	public String heartBeat(String msg) throws RemoteException;

	public String findDataServer(String remote) throws RemoteException;
	
	public String assignDataServer(String filepath) throws RemoteException;
	
	public String readTest() throws RemoteException;

	public byte[] readFile(List<Block> blks, int start, int end) throws RemoteException;
}
