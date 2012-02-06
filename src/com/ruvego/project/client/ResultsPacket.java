package com.ruvego.project.client;

import java.io.Serializable;

public class ResultsPacket implements Serializable {

	private static final long serialVersionUID = 1L;
	private String[] columnData = new String[8];
	private int num_cols;
	private int num_elem;
	private int id;
	private String next;
	

	/* Header of the table */
	public ResultsPacket(String[] columnData, int num_cols, int num_elem) {
		for (int i = 0; i < num_cols; i++) {
			this.columnData[i] = columnData[i];
		}
		this.num_cols = num_cols;
		this.num_elem = num_elem;
	}
	
	/* Content of the table */
	public ResultsPacket(String[] columnData, int num_cols, String typeNext) {
		for (int i = 0; i < num_cols; i++) {
			this.columnData[i] = columnData[i];
			this.next = typeNext;
		}
	}

	public ResultsPacket() {
	}

	public String getColumnData(int index) {
		return this.columnData[index];
	}
	
	public String[] getColumnData() {
		return this.columnData;
	}

	public int getNumElem() {	
		return this.num_elem;
	}

	public int getNumCols() {
		return this.num_cols;
	}
	
	public int getId() {
		return this.id;
	}
	
	public String getNext() {
		return this.next;
	}
	
}
