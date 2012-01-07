package com.ruvego.project.client;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;

public class RuvegoAboutPage {
	static private RuvegoAboutPage page;
	
	AbsolutePanel aboutPanel;

	private RuvegoAboutPage() {
		System.out.println("Creating an object of type RuvegoAboutPage");
		aboutPanel = new AbsolutePanel();
		Ruvego.getRootPanel().add(aboutPanel, (Window.getClientWidth() - 1000)/2, Ruvego.getOtherWidgetTop());

	}
	
	public static RuvegoAboutPage getPage() {
		if (page == null) {
			page = new RuvegoAboutPage();
		}
		return page;
	}

}
