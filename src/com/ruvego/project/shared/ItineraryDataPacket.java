package com.ruvego.project.shared;

import java.io.Serializable;


public class ItineraryDataPacket implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8690749567025950823L;
	private int numDays;
	private String startDate;
	
	private DayDataPacket[] dayData;
	
	public ItineraryDataPacket() {
	}

	public ItineraryDataPacket(int numDays, String startDate) {
		this.dayData = new DayDataPacket[numDays];
		this.numDays = numDays;
		this.startDate = startDate;
	}
	
	public void setData(int day, int numEntries, String[] name, String[] address) {
		this.dayData[day] = new DayDataPacket(numEntries, name, address);
	}

	public int getNumEntries(int day) {
		return this.dayData[day].numEntries;
	}

	/** parameter day starts from 0 */
	public String[] getNameList(int day) {
		return this.dayData[day].name;
	}
	
	/** parameter day starts from 0 */
	public String[] getAddressList(int day) {
		return this.dayData[day].address;
	}

	/** parameter day starts from 0 */
	public String getStartDate() {
		return this.startDate;
	}
	
	public int getNumDays() {
		return this.numDays;
	}


}
