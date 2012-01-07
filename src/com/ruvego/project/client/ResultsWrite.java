package com.ruvego.project.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("writeResults")
public interface ResultsWrite extends RemoteService {
	boolean writeResults(WritePlacePacket writeData);

	boolean writeResults(WriteCategoryPacket writeData);

	boolean writeResults(WriteActivityPacket writeData);
}
