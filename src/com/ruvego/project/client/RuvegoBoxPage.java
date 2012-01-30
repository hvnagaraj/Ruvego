package com.ruvego.project.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;


public class RuvegoBoxPage {
	static private RuvegoBoxPage page;

	final static int BOTTOM_BLANK_HEIGHT = 30;
	
	protected static DayActivityPlan boxPlan;
	
	private static DayActivityPlan testPlan;
	
	private static VerticalPanel vPanel;

	protected static ScrollPanel scrollPanel;
	
	protected static int boxValueCount;

	private static CreateItinerary createItinerary;

	private static Label btnRoute;

	private static HTML htmlBoxItineraryInfo1;
	private static HTML htmlBoxItineraryInfo2;
	private static HTML htmlBoxClickHere;
	
	private static ItineraryCommon itineraryCommon;

	private RuvegoBoxPage() {
		System.out.println("Creating an object of type RuvegoBoxPage");
		vPanel = new VerticalPanel();
		vPanel.setWidth("100%");
		
		boxPlan = new DayActivityPlan(vPanel);		
		scrollPanel = new ScrollPanel(vPanel);

		RootPanel.get().add(scrollPanel, Ruvego.getIndent(), Ruvego.getOtherWidgetTop());
		scrollPanel.setStyleName("boxBG");
		scrollPanel.setPixelSize(DayActivityPlan.BOX_PANEL_WIDTH, Window.getClientHeight() - Ruvego.getOtherWidgetTop() - 
				Ruvego.getFooterHeight() - BOTTOM_BLANK_HEIGHT - 3);



		btnRoute = new Label("Route");
		btnRoute.setStyleName("btnRoute");
		Ruvego.getMapsPanel().add(btnRoute, Ruvego.getMapsPanel().getOffsetWidth() - 90, 12);

		btnRoute.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				boxPlan.mapRoute();
			}
		});

		htmlBoxItineraryInfo1 = new HTML("*To plan your multi-day activities, create an itinerary using the menu.");
		htmlBoxClickHere = new HTML("<a href=\"javascript:undefined;\">Click Here</a>");
		htmlBoxItineraryInfo2 = new HTML("to save this as a 1-day itinerary");

		htmlBoxItineraryInfo1.setStyleName("boxItineraryInfo");
		htmlBoxClickHere.setStyleName("boxItineraryInfo");
		htmlBoxItineraryInfo2.setStyleName("boxItineraryInfo");


		RootPanel.get().add(htmlBoxItineraryInfo1, Ruvego.getIndent(), Window.getClientHeight() - Ruvego.getFooterHeight() - 20);
		RootPanel.get().add(htmlBoxClickHere, htmlBoxItineraryInfo1.getAbsoluteLeft() + htmlBoxItineraryInfo1.getOffsetWidth() + 5, 
				Window.getClientHeight() - Ruvego.getFooterHeight() - 20);
		RootPanel.get().add(htmlBoxItineraryInfo2, htmlBoxClickHere.getAbsoluteLeft() + htmlBoxClickHere.getOffsetWidth() + 5, 
				Window.getClientHeight() - Ruvego.getFooterHeight() - 20);
		
		htmlBoxClickHere.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				createItinerary = CreateItinerary.getPage();
				createItinerary.panelsOneDayView();
			}
		});


		Window.addResizeHandler(new ResizeHandler() {

			@Override
			public void onResize(ResizeEvent event) {
				if (History.getToken().equalsIgnoreCase("boxView")) {
					panelResizeAlignments();
				}
			}
		});
	}


	public void fetchBoxResults() {
		Ruvego.setMapsPosition(DayActivityPlan.BOX_PANEL_WIDTH, BOTTOM_BLANK_HEIGHT);
		Ruvego.getMapWidget().clearOverlays();
		String cookieItemCount = Cookies.getCookie("itemcount");


		if (cookieItemCount != null && Integer.parseInt(cookieItemCount) != 0) {
			boxValueCount = Integer.parseInt(cookieItemCount);

			System.out.println("No of items in the cookie : " + boxValueCount);
			if (boxValueCount > 25) {
				System.out.println("Box has more than 25 items");
				return;
			}

			String entryDelims = "<;;>";
			String[] entry;
			String cookieItems = Cookies.getCookie("itemsdata");

			entry = cookieItems.split(entryDelims);
			boxPlan.setupSrcBoxPanel();
			boxPlan.addResults(entry, boxValueCount);
			boxPlan.dayName = "Box";
			
			testPlan = new DayActivityPlan(vPanel);
			testPlan.addResults(entry, 3);
			testPlan.dayName = "Day 2";
			testPlan.setupDstBoxPanel();
		}

		panelAlignments();
	}


	private void panelAlignments() {
		Ruvego.setMinimumPageHeight(RuvegoHomePage.HOMEPAGE_PAGE_HEIGHT);
		Ruvego.panelAlignments();
		panelResizeAlignments();
	}

	public static void panelResizeAlignments() {
		scrollPanel.setHeight((Ruvego.getClientHeight() - Ruvego.getOtherWidgetTop() - Ruvego.getFooterHeight() - BOTTOM_BLANK_HEIGHT - 3) + "px");
		Ruvego.getMapsPanel().setWidgetPosition(btnRoute, Ruvego.getMapsPanel().getOffsetWidth() - 80, Ruvego.getMapsPanel().getOffsetHeight() - 60);
		Ruvego.setMapsPosition(DayActivityPlan.BOX_PANEL_WIDTH, BOTTOM_BLANK_HEIGHT);
		scrollPanel.setPixelSize(DayActivityPlan.BOX_PANEL_WIDTH, Window.getClientHeight() - Ruvego.getOtherWidgetTop() - 
				Ruvego.getFooterHeight() - BOTTOM_BLANK_HEIGHT - 3);
	}

	public static RuvegoBoxPage getPage() {
		if (page == null) {
			page = new RuvegoBoxPage();
		}
		return page;
	}

	public void panelsView() {
		scrollPanel.setVisible(true);
		btnRoute.setVisible(true);
		htmlBoxClickHere.setVisible(true);
		htmlBoxItineraryInfo1.setVisible(true);
		htmlBoxItineraryInfo2.setVisible(true);
	}

	public static void panelsItineraryView() {
		scrollPanel.setVisible(true);
		btnRoute.setVisible(true);
		htmlBoxClickHere.setVisible(false);
		htmlBoxItineraryInfo1.setVisible(false);
		htmlBoxItineraryInfo2.setVisible(false);
	}

	public void clearContent() {
		scrollPanel.setVisible(false);
		ItineraryCommon.routeBriefPanel.setVisible(false);
		btnRoute.setVisible(false);
		htmlBoxClickHere.setVisible(false);
		htmlBoxItineraryInfo1.setVisible(false);
		htmlBoxItineraryInfo2.setVisible(false);
		Ruvego.errorDisplayClear();
		Ruvego.setMapsPosition(0, 0);
	}
}
