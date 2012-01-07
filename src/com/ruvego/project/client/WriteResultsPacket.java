package com.ruvego.project.client;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import org.bson.BSONObject;

import com.google.gwt.user.client.ui.Image;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class WriteResultsPacket implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String name;
	String type;

	private class typePlace {
		
		private typePlace(String name, String state) {
			this.name = name;
			this.state = state;
		}
		
		private typePlace() {
		}
		
		@SuppressWarnings("unused")
		private String getPlaceName() {
			return this.name;
		}
		
		@SuppressWarnings("unused")
		private String getPlaceState() {
			return this.state;
		}
		
		String name = null;
		String state = null;
	}
	 
	
	
	public class typeCategory {
		String name;
		String col1;
		String col2;
		String col3;
		String col4;
		String col5;
		String col6;
		String col7;
	}
	
	public class typeActivity {
		String name;
		String col1;
		String col2;
		String col3;
		String col4;
		String col5;
		String col6;
		String col7;
		Image image;
	}
	
	public class typeSubActivity {
		String name;
		String col1;
		String col2;
		String col3;
		String col4;
		String col5;
		String col6;
		String col7;
		Image image;
	}
	
	public WriteResultsPacket() {
	}
	
	public WriteResultsPacket(String name, String state, String type) {
		//place = new typePlace();
		typePlace place = new typePlace(name, state);
		this.type = type;
	}

	
	
	public String getType() {
		return this.type;
	}
	
	public String getPlaceName() {
		return this.getPlaceName();
	}
	
	public String getPlaceState() {
		return null;
	}
	
}

