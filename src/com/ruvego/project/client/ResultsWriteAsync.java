package com.ruvego.project.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.ruvego.project.shared.CreateItineraryPacket;

public interface ResultsWriteAsync {

	void writeResults(WritePlacePacket writeData,
			AsyncCallback<Boolean> callbackWritePlace);

	void writeResults(WriteCategoryPacket writeData,
			AsyncCallback<Boolean> callbackWriteCategory);

	void writeResults(WriteActivityPacket writeData,
			AsyncCallback<Boolean> callbackWriteActivity);

	void writeCreateItinerary(CreateItineraryPacket createItineraryPacket,
			AsyncCallback<Boolean> callbackCreateItinerary);

	void addEntry(String itineraryName, String day, String objectId,
			String username, AsyncCallback<Boolean> callbackAddEntry);

	void addEntries(String itineraryName, String day, String[] objectIdList,
			String username, AsyncCallback<Boolean> callbackAddEntry);
}
