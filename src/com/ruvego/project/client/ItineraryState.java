package com.ruvego.project.client;


public class ItineraryState {
	private static ItineraryState page;

	private static boolean ACTIVE_ITINERARY = true;

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
