package com.ruvego.project.client;

import java.io.Serializable;

public class CategoryPacket implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String[] imagePath;
	private String[] imageCaption;
	private String[] next;
	private String[] prev;
	private int numElems;
	
	public CategoryPacket() {
	}
	
	public CategoryPacket(int numElems) {
		this.imagePath = new String[numElems];
		this.imageCaption = new String[numElems];
		this.next = new String[numElems];
		this.prev = new String[numElems];
		this.numElems = numElems;
	}
	
	public String getImagePath(int elemNum) {
		return this.imagePath[elemNum];
	}
	
	public String getImageCaption(int elemNum) {
		return this.imageCaption[elemNum];
	}
	
	public String getNext(int elemNum) {
		return this.next[elemNum];
	}
	
	public String getPrev(int elemNum) {
		return this.prev[elemNum];
	}
	
	public int getNumElems() {
		return this.numElems;
	}
	
	public void setImagePath(int elemNum, String imagePath) {
		this.imagePath[elemNum] = imagePath;
	}
	
	public void setImageCaption(int elemNum, String imageCaption) {
		this.imageCaption[elemNum] = imageCaption;
	}
	
	public void setNext(int elemNum, String next) {
		this.next[elemNum] = next;
	}
	
	public void setPrev(int elemNum, String prev) {
		this.prev[elemNum] = prev;
	}
	
	public void setNumElems(int numElems) {
		this.numElems = numElems;
	}

}
