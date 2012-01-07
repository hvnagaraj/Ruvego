package com.ruvego.project.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ResultsWriteAsync {

	void writeResults(WritePlacePacket writeData,
			AsyncCallback<Boolean> callbackWritePlace);

	void writeResults(WriteCategoryPacket writeData,
			AsyncCallback<Boolean> callbackWriteCategory);

	void writeResults(WriteActivityPacket writeData,
			AsyncCallback<Boolean> callbackWriteActivity);

}
