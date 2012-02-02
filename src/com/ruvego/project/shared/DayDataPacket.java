package com.ruvego.project.shared;

import java.io.Serializable;

public class DayDataPacket implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6924413125055916799L;
	protected int numEntries;
	protected String[] name;
	protected String[] address;

	public DayDataPacket() {
	}

	public DayDataPacket(int numEntries, String[] name, String[] address) {
		this.numEntries = numEntries;
		this.name = name;
		this.address = address;
	}
}
