package org.sstore.protocol;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;

import org.sstore.utils.Constants;

public class BlockMessage implements Message, Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7694473841706027117L;
	private static long id = System.currentTimeMillis();
	private int serverId;
	private static int type = Constants.BLOCKMSG ;
	
	public int getType(){
		return type;
	}
	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

	private Set<Long> blockIds;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Set<Long> getBlockIds() {
		return blockIds;
	}

	public void setBlockIds(Set<Long> blockIds) {
		this.blockIds = blockIds;
	}

	public BlockMessage(int serverId, Set<Long> blockIds){
		this.serverId = serverId;
		this.blockIds = blockIds;
	}
	public String toString(){
		
		StringBuffer sbuf = new StringBuffer();
		sbuf.append(id + ",");
		sbuf.append(serverId + ",");
		
		Iterator<Long> iter = blockIds.iterator();
		while(iter.hasNext()){
			sbuf.append(iter.next() + "-");
		}
		String msg = sbuf.substring(0, sbuf.length()-1);
		
		return msg;
	}
}
