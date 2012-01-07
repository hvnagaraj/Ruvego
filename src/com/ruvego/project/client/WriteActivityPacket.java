package com.ruvego.project.client;

import java.io.Serializable;

import com.google.gwt.user.client.ui.TextBox;

public class WriteActivityPacket implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public int MAX_VAR_COLS = 3;
	
	/* All the variables of this datastructure start from here */
	String type;
	String name;
	String place;
	String brief;
	String address;
	String website;
	String rating;
	String imagePath;
	String entryFee;
	String toll;
	String parking;
	String timings;
	String category;
	String subCategory;
	String contact;
	int checkBoxValue;
	double latN, latS, latE, latW;
	double lonN, lonS, lonE, lonW;
	String[] col = new String[MAX_VAR_COLS];
	
		
	public WriteActivityPacket() {
	}

	/* Type, Place, Name, Brief, Address, Website, Rating, TextBox1, TextBox2, TextBox3, Upload, Entry Fee, 
	 * Toll, Free Parking, Paid Parking, Additional Info 
	 */
	public WriteActivityPacket(String type, String place, String name, String brief,
			String address, String website, String rating, String col1, String col2, String col3, String imagePath, 
			String entryFee, String toll, String parking, String timings, String category, String subCategory, 
			int checkBoxValue, double latN, double latS, double latE, double latW, double lonN, double lonS, double lonE, double lonW, String contact) {
		this.type = type;
		this.name = name;
		this.place = place;
		this.brief = brief;
		this.address = address;
		this.website = website;
		this.rating = rating;
		this.imagePath = imagePath;
		this.entryFee = entryFee;
		this.toll = toll;
		this.parking = parking;
		this.timings = timings;
		this.col[0] = col1;
		this.col[1] = col2;
		this.col[2] = col3;
		this.category = category;
		this.subCategory = subCategory;
		this.checkBoxValue = checkBoxValue;
		this.latN = latN;
		this.latS = latS;
		this.latE = latE;
		this.latW = latW;
		this.lonN = lonN;
		this.lonS = lonS;
		this.lonE = lonE;
		this.lonW = lonW;
		this.contact = contact;
	}
	
	public String getType() {
		return this.type;
	}

	public String getName() {
		return this.name;
	}

	public String getCol(int i) {
		return this.col[i];
	}

	public String getPlace() {
		return this.place;
	}

	public String getBrief() {
		return this.brief;
	}

	public String getAddress() {
		return this.address;
	}

	public String getWebsite() {
		return this.website;
	}

	public String getRating() {
		return this.rating;
	}

	public String getImagePath() {
		return this.imagePath;
	}

	public String getEntryFee() {
		return this.entryFee;
	}

	public String getToll() {
		return this.toll;
	}

	public String getParking() {
		return this.parking;
	}

	public String getCategory() {
		return this.category;
	}
	
	public String getSubCategory() {
		return this.subCategory;
	}
	
	public int getcheckBoxValue() {
		return this.checkBoxValue;
	}

	public String getTimings() {
		return this.timings;
	}

	public double getLatN() {
		return this.latN;
	}

	public double getLatS() {
		return this.latS;
	}

	public double getLatE() {
		return this.latE;
	}

	public double getLatW() {
		return this.latW;
	}

	public double getLonN() {
		return this.lonN;
	}

	public double getLonS() {
		return this.lonS;
	}

	public double getLonE() {
		return this.lonE;
	}

	public double getLonW() {
		return this.lonW;
	}

	public String getContact() {
		return this.contact;
	}

}
