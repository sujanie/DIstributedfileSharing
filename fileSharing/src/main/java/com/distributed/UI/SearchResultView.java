package com.distributed.UI;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;


public class SearchResultView {
	private SimpleIntegerProperty DownloadID;
	private SimpleStringProperty address;
	private SimpleStringProperty filename;
	private  SimpleIntegerProperty portNo;
	private SimpleStringProperty time;
	private SimpleIntegerProperty hops;
	public SearchResultView(int id,  String filename,String address,
			int nodeID,int hops, String time) {
		
		this.DownloadID = new SimpleIntegerProperty(id);
		this.filename = new SimpleStringProperty(filename);
		this.address = new SimpleStringProperty(address);
		this.portNo =new SimpleIntegerProperty( nodeID);
		this.time = new SimpleStringProperty(time);
		this.hops=new SimpleIntegerProperty(hops);
		
	}
	public int getHops() {
		return hops.get();
	}
	public void setHops(int hops) {
		this.hops.set(hops); 
	}
	public int getDownloadID() {
		return DownloadID.get();
	}
	public void setDownloadID(int id) {
		this.DownloadID.set(id);
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
	public int getPortNo() {
		return portNo.get();
	}
	public void setPortNo(int nodeID) {
		this.portNo.set(nodeID);
	}
	public String getTime() {
		return time.get();
	}
	public void setTime(String time) {
		this.time.set(time);
	}
	
	
	
}
