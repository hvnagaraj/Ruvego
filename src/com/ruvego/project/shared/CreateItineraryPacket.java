package com.ruvego.project.shared;

import java.io.Serializable;

public class CreateItineraryPacket implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String itineraryName;
	private String numDays;
	private String startDate;
	private String endDate;
	private String username;
	
	public CreateItineraryPacket() {
	}
	
	public CreateItineraryPacket(String name, String numDays, String startDate,
			String endDate, String username) {
		this.itineraryName = name;
		this.numDays = numDays;
		this.startDate = startDate;
		this.endDate = endDate;
		this.username = username;
	}
	
	public String getUsername() {
		return this.username;
	}

	public String getItineraryName() {
		return this.itineraryName;
	}

	public String getNumDays() {
		return this.numDays;
	}

	public String getStartDate() {
		return this.startDate;
	}

	public String getEndDate() {
		return this.endDate;
	}

}
