package com.ruvego.project.client;

import java.io.Serializable;

public class WritePlacePacket implements Serializable {
	private static final long serialVersionUID = 1L;
	
	String place;
	String state;
	double lat;
	double lon;
	
	public WritePlacePacket() {
	}

	public WritePlacePacket(String place, String state, double lat, double lon) {
		this.place = place;
		this.state = state;
		this.lat = lat;
		this.lon = lon;
	}

	public String getPlaceName() {
		return this.place;
	}
	
	public String getState() {
		return this.state;
	}
	
	public double getLon() {
		return this.lon;
	}

	public double getLat() {
		return this.lat;
	}

}
