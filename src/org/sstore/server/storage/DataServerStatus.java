package org.sstore.server.storage;

public class DataServerStatus {

	private boolean active = false;
	
	public boolean isActive(){
		return active;
	}
	
	public void setActive(boolean active){
		this.active = active;
	}
}
