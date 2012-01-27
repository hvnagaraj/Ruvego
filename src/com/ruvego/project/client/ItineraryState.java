package com.ruvego.project.client;

public class ItineraryState {
	private static ItineraryState page;
	
	private static boolean ACTIVE_ITINERARY = true;
	
	private static String itineraryName = "";
	
	private static int NUM_DAYS = 10;
	
	public boolean isItineraryActive() {
		return ACTIVE_ITINERARY;
	}
	
	public int getNumDays() {
		return NUM_DAYS;
	}
	
	public static ItineraryState getPage() {
		if (page == null) {
			page = new ItineraryState();
		}
		return page;
	}
	
	private ItineraryState() {
		
	}

}
