package com.ruvego.project.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.ruvego.project.shared.ItineraryDataPacket;

public interface ResultsFetchAsync {

	void fetchResults(String place, String withinMiles, int timeOfTheDay, String prevType, String request, AsyncCallback<ResultsPacket[]> callback);

	void fetchCategoryColumns(String category, String categoryType, AsyncCallback<String[]> callbackCategoryColumns);

	void fetchCategoryList(AsyncCallback<String[]> callbackCategoryList);

	void fetchTypeOptions(String string,
			String categoryType, AsyncCallback<String[]> callbackTypeOptions);

	void fetchSubCategoryList(String itemText,
			AsyncCallback<String[]> callbackSubCategoryList);

	void fetchPlaceList(AsyncCallback<String[]> callbackPlaceList);

	void fetchCategoryResults(String place, String withinMiles, int timeOfTheDay,
			AsyncCallback<CategoryPacket> callback);

	void fetchSubcategoryResults(String place, String withinMiles,
			int timeOfTheDay, AsyncCallback<CategoryPacket> callback);

	void fetchBriefPanelResults(String name,
			AsyncCallback<ResultsBriefPanelPacket> callbackBriefPanelResults);

	void authenticateUser(String username, String password, AsyncCallback<Boolean> callbackAuthenticate);

	void fetchItineraryData(String itineraryName,
			AsyncCallback<ItineraryDataPacket> callbackItineraryResults);

}
