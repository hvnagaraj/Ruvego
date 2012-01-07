package com.ruvego.project.client;

import com.google.gwt.user.client.ui.ScrollPanel;

public class RuvegoBoxPage {
	static private RuvegoBoxPage page;

	private static ScrollPanel scrollPanel;

	private RuvegoBoxPage() {
		System.out.println("Creating an object of type RuvegoAboutPage");
		scrollPanel = new ScrollPanel();
		Ruvego.getRootPanel().add(scrollPanel, Ruvego.getIndent(), Ruvego.getOtherWidgetTop());
		scrollPanel.setStyleName("contributePanelBG");
	}
	
	public static RuvegoBoxPage getPage() {
		if (page == null) {
			page = new RuvegoBoxPage();
		}
		return page;
	}

}
