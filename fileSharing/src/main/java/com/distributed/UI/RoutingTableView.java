package com.distributed.UI;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class RoutingTableView {
	private SimpleIntegerProperty id;
	private SimpleStringProperty address;
	private SimpleIntegerProperty port;
	private SimpleIntegerProperty pings;
	public RoutingTableView(int id, String address, int port,
		int pings) {

		this.id = new SimpleIntegerProperty(id);
		this.address = new SimpleStringProperty(address);
		this.port = new SimpleIntegerProperty(port);
		this.pings = new SimpleIntegerProperty( pings);
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
	public int getPort() {
		return port.get();
	}
	public void setPort( int port) {
		this.port.set(port);
	}
	public int getPings() {
		return pings.get();
	}
	public void setPings(int pings) {
		this.pings.set(pings);
	}
	
	
	

}
