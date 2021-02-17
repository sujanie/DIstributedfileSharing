package com.distributed.UI;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;


public class SearchResultView {
	private SimpleIntegerProperty id;
	private SimpleStringProperty address;
	private SimpleStringProperty filename;
	private  SimpleIntegerProperty nodeID;
	private SimpleStringProperty time;
	public SearchResultView(int id,  String filename,String address,
			int nodeID, String time) {
		
		this.id = new SimpleIntegerProperty(id);
		this.filename = new SimpleStringProperty(filename);
		this.address = new SimpleStringProperty(address);
		this.nodeID =new SimpleIntegerProperty( nodeID);
		this.time = new SimpleStringProperty(time);
	}
	public int getId() {
		return id.get();
	}
	public void setId(int id) {
		this.id.set(id);
	}
	public String getAddress() {
		return address.get();
	}
	public void setAddress(String address) {
		this.address.set(address);
	}
	public String getFilename() {
		return filename.get();
	}
	public void setFilename(String filename) {
		this.filename.set(filename);
	}
	public int getNodeID() {
		return nodeID.get();
	}
	public void setNodeID(int nodeID) {
		this.nodeID.set(nodeID);
	}
	public String getTime() {
		return time.get();
	}
	public void setTime(String time) {
		this.time.set(time);
	}
	
	
	
}
