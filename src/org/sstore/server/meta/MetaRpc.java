package org.sstore.server.meta;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import org.sstore.structure.Block;

public interface MetaRpc extends Remote {
	public byte[] readFile(List<Block> blks, int start, int end) throws RemoteException;
}
