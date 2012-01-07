package com.ruvego.project.client;

import java.io.Serializable;
import java.util.LinkedList;

public class WriteCategoryPacket implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public int MAX_VAR_COLS = 3;
	
	/* All the variables of this datastructure start from here */
	String type;
	String name;
	String category;
	String imagepath;
	String[] col = new String[MAX_VAR_COLS];
	LinkedList<String> typeOptions = new LinkedList<String>();
	
	public WriteCategoryPacket() {
	}

	public WriteCategoryPacket(String name, String col0, String col1, String col2, LinkedList<String> typeOptions, String category, 
			String imagepath) {
		this.col[0] = col0;
		this.col[1] = col1;
		this.col[2] = col2;
		this.name = name;
		this.typeOptions = typeOptions;
		this.category = category;
		this.imagepath = imagepath;
	}
	
	public String getType() {
		return this.type;
	}


	public String getCol(int i) {
		return col[i];
	}

	public String getName() {
		return this.name;
	}

	public LinkedList<String> getTypeOptions() {
		return this.typeOptions;
	}
	
	public String getCategory() {
		return this.category;
	}
	
	public String getImagePath() {
		return this.imagepath;
	}

}
