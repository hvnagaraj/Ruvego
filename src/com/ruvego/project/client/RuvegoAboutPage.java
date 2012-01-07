package com.ruvego.project.client;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;

public class RuvegoAboutPage {
	static private RuvegoAboutPage page;
	
	private static AbsolutePanel aboutPanel;

	private RuvegoAboutPage() {
		System.out.println("Creating an object of type RuvegoAboutPage");
		aboutPanel = new AbsolutePanel();
		Ruvego.getRootPanel().add(aboutPanel, (Window.getClientWidth() - 1000)/2, Ruvego.getOtherWidgetTop());
		
		aboutPanel.setStyleName("contributePanelBG");

		HTML about = new HTML("About");
		about.setStyleName("about");

		HTML aboutContent = new HTML("Ruvego is a activity planning company. It provides all the necessary features and ");
		aboutContent.setStyleName("aboutContent");
		
		aboutPanel.add(about, 20, 20);
		
		aboutPanel.add(aboutContent, 20, 50);
		
		Window.addResizeHandler(new ResizeHandler() {
			public void onResize(ResizeEvent event) {
				if (History.getToken().equalsIgnoreCase("aboutPage")) {
					ruvegoAboutAlignments();
				}
			}
		});

		ruvegoAboutAlignments();
	}
	
	public static RuvegoAboutPage getPage() {
		if (page == null) {
			page = new RuvegoAboutPage();
		}
		return page;
	}
	
	static protected void ruvegoAboutAlignments() {
		aboutPanel.setPixelSize(1000 - Ruvego.getIndent(), 400);
		Ruvego.setMinimumPageHeight(aboutPanel.getOffsetHeight() + Ruvego.getFooterHeight() + Ruvego.getOtherWidgetTop());
		Ruvego.ruvegoPanelAlignments();
		Ruvego.rootPanel.setWidgetPosition(aboutPanel, Ruvego.getIndent() + (Ruvego.getMapsPanel().getOffsetWidth() - aboutPanel.getOffsetWidth())/2, 
				Ruvego.getOtherWidgetTop());
	}
	
	public void clearContent() {
		aboutPanel.setVisible(false);
	}
	
	public void panelsView() {
		aboutPanel.setVisible(true);
		ruvegoAboutAlignments();
	}

}
