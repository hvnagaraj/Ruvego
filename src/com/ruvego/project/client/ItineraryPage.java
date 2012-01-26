package com.ruvego.project.client;

import javax.swing.Scrollable;

import com.google.gwt.user.client.ui.ScrollPanel;

public class ItineraryPage {
	private static ItineraryPage page;
	
	private static ScrollPanel itineraryPanel;
	
	public static ItineraryPage getPage() {
		if (page == null) {
			page = new ItineraryPage();
		}
		return page;
	}
	
	private ItineraryPage() {
		itineraryPanel = new ScrollPanel();
		
		
	}

}
