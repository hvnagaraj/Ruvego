package com.ruvego.project.client;

import java.io.Serializable;

import com.google.gwt.safehtml.shared.SafeUri;

public class ResultsBriefPanelPacket implements Serializable {

	private String name;
	private String address;
	private String brief;
	private String website;
	private String imagePath;
	private String miscInfo;
	private String timings;
	private String contact;
	
	public ResultsBriefPanelPacket() {
		
	}
	
	public ResultsBriefPanelPacket(String name, String address, String brief, String website, String rating, String imagePath, String miscInfo, String timings,
			String contact) {

		this.name = name;
		this.brief = brief;
		this.address = address;
		this.website = website;
		this.imagePath = imagePath;
		this.miscInfo = miscInfo;
		this.timings = timings;
		this.contact = contact;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getBrief() {
		return this.brief;
	}
	
	public String getaddress() {
		return this.address;
	}
	
	public String getWebsite() {
		return this.website;
	}
	
	public String getTimings() {
		return this.timings;
	}
	
	public String getMiscinfo() {
		return this.miscInfo;
	}
	
	public String getImagepath() {
		return this.imagePath;
	}

	public String getContact() {
		return this.contact;
	}

}

