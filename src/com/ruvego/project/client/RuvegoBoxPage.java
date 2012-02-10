package com.ruvego.project.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.maps.client.geom.LatLngBounds;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;


public class RuvegoBoxPage {
	static private RuvegoBoxPage page;

	final static int BOTTOM_BLANK_HEIGHT = 30;
	
	protected static DayActivityPlan boxPlan;
	
	private static VerticalPanel vPanel;

	protected static ScrollPanel scrollPanel;
	
	protected static int boxValueCount;

	private static CreateItinerary createItinerary;

	private static Label btnRoute;

	private static HTML htmlBoxItineraryInfo1;
	private static HTML htmlBoxItineraryInfo2;
	private static HTML htmlBoxClickHere;
	
	private static ItineraryCommon itineraryCommon;
	
	protected static AsyncCallback<Boolean> callbackAddEntry;


	private RuvegoBoxPage() {
		System.out.println("Creating an object of type RuvegoBoxPage");
		vPanel = new VerticalPanel();
		vPanel.setWidth("100%");
				
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

		htmlBoxItineraryInfo1 = new HTML("* To plan your multi-day activities, create an itinerary using the menu.");
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
		
		callbackAddEntry = new AsyncCallback<Boolean>() {
			
			@Override
			public void onSuccess(Boolean result) {
				if (result == false) {
					Ruvego.errorDisplayWithTimer("Unable to save the itinerary");
					return;
				}
				History.newItem("itineraryPage/" + ItineraryState.ITINERARY_NAME);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Ruvego.errorDisplayWithTimer("Server returned an error when trying to save the itinerary. Try again after some time");
			}
		};
	}


	public void fetchBoxResults() {
		Ruvego.setMapsPosition(DayActivityPlan.BOX_PANEL_WIDTH, BOTTOM_BLANK_HEIGHT);
		
		/* Clearing any previous Box or Itinerary entries */
		vPanel.clear();
		Ruvego.getMapWidget().clearOverlays();
		ItineraryCommon.bounds = LatLngBounds.newInstance();

		String cookieItemCount = Ruvego.readCookie("itemcount");

		if (Integer.parseInt(cookieItemCount) != 0) {
			boxValueCount = Integer.parseInt(cookieItemCount);

			System.out.println("No of items in the cookie : " + boxValueCount);
			System.out.println(Ruvego.readCookie("itemsdata"));
			String[] entry;
			entry = Ruvego.parseString(Ruvego.readCookie("itemsdata"), "<;;>");

			String[] nameList = new String[boxValueCount];
			String[] addressList = new String[boxValueCount];
			String[] objectIdList = new String[boxValueCount];

			String[] fields;
			for (int i = 0; i < boxValueCount; i++) {
				fields = Ruvego.parseString(entry[i], "<;>");
				nameList[i] = fields[0];
				addressList[i] = fields[1];
				objectIdList[i] = fields[2];
				System.out.println("ID : --------------- : " + objectIdList[i]);
			}

			boxPlan = new DayActivityPlan(vPanel, true);
			boxPlan.setupSrcBoxPanel();
			boxPlan.addResults(nameList, addressList, objectIdList, boxValueCount);
			boxPlan.dayName = "Box";
			boxPlan.setupDstBoxPanel();
		} else {
			Ruvego.errorDisplayWithTimer("No entries in the Box. Redirecting to Home Page");
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


	public static void writeDataToServer() {
		boxPlan.writeDataToServer();
	}

}
