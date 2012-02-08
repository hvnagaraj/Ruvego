package com.ruvego.project.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.ruvego.project.shared.CreateItineraryPacket;

@RemoteServiceRelativePath("writeResults")
public interface ResultsWrite extends RemoteService {
	boolean writeResults(WritePlacePacket writeData);

	boolean writeResults(WriteCategoryPacket writeData);

	boolean writeResults(WriteActivityPacket writeData);

	boolean writeCreateItinerary(CreateItineraryPacket createItineraryPacket);

	boolean addEntry(String itineraryName, String day, String objectId,
			String username);

	boolean addEntries(String itineraryName, String day, String[] objectIdList,
			String username);
}
