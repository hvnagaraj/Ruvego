package com.ruvego.project.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.ruvego.project.shared.ItineraryDataPacket;

@RemoteServiceRelativePath("readResults")
public interface ResultsFetch extends RemoteService {
	ResultsPacket[] fetchResults(String place, String withinMiles,
			int timeOfTheDay, String prevType, String request);

	String[] fetchCategoryColumns(String category, String categoryType);

	String[] fetchCategoryList();

	String[] fetchTypeOptions(String string, String categoryType);

	String[] fetchSubCategoryList(String itemText);

	String[] fetchPlaceList();

	CategoryPacket fetchCategoryResults(String place, String withinMiles,
			int timeOfTheDay);

	CategoryPacket fetchSubcategoryResults(String place, String withinMiles,
			int timeOfTheDay);

	ResultsBriefPanelPacket fetchBriefPanelResults(String name);

	boolean authenticateUser(String username, String password);

	ItineraryDataPacket fetchItineraryData(String itineraryName, String string);
}

