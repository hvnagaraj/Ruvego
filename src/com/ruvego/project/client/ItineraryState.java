package com.ruvego.project.client;

import java.util.Date;

import com.google.gwt.user.client.Cookies;


public class ItineraryState {
	private static ItineraryState page;

	private static boolean ACTIVE_ITINERARY = false;

	protected static String ITINERARY_NAME = "";

	private static int NUM_DAYS = 10;

	private static DayActivityPlan entry;
	private static int entryNum;

	public static void setEntry(DayActivityPlan entry, int entryNum) {
		ItineraryState.entry = entry;
		ItineraryState.entryNum = entryNum;
	}

	public static DayActivityPlan getEntry() {
		return entry;
	}
	
	public static int getEntryNum() {
		return entryNum;
	}

	public static boolean isItineraryActive() {
		return ACTIVE_ITINERARY;
	}

	public static int getNumDays() {
		return NUM_DAYS;
	}

	public static ItineraryState getPage() {
		if (page == null) {
			page = new ItineraryState();
		}
		return page;
	}

	private ItineraryState() {
		if (LoginModule.isUserAuthenticated() == false) {
			return;
		}
		
		String cookieValue = Cookies.getCookie("itinerary");
		if (cookieValue != null) {
			System.out.println("Cookie found !!!");
			ITINERARY_NAME = cookieValue;
			NUM_DAYS = Integer.parseInt(Cookies.getCookie("numdays"));
			Ruvego.setItineraryText(ITINERARY_NAME);
			
			ACTIVE_ITINERARY = true;
		} else {
			System.out.println("Cookie not found !!!");
		}
	}

	public static void setNumDays(int numDays) {
		NUM_DAYS = numDays;
		assert(ACTIVE_ITINERARY == true);
		
		long DURATION = 1000 * 60 * 60 * 24 * 14; //duration remembering login. 2 weeks in this example.
		Date expires = new Date(System.currentTimeMillis() + DURATION);
		Cookies.setCookie("numdays", String.valueOf(NUM_DAYS), expires, null, "/", false);
		
		ResultsActivityMenu.setItineraryActive();
	}

	public static void setName(String itineraryName) {
		ITINERARY_NAME = itineraryName;
		
		long DURATION = 1000 * 60 * 60 * 24 * 14; //duration remembering login. 2 weeks in this example.
		Date expires = new Date(System.currentTimeMillis() + DURATION);
		Cookies.setCookie("itinerary", ITINERARY_NAME, expires, null, "/", false);

		ACTIVE_ITINERARY = true;
	}
}
