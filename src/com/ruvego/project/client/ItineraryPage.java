package com.ruvego.project.client;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;

import javax.swing.Scrollable;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.maps.client.geocode.DirectionQueryOptions;
import com.google.gwt.maps.client.geocode.DirectionResults;
import com.google.gwt.maps.client.geocode.Directions;
import com.google.gwt.maps.client.geocode.DirectionsCallback;
import com.google.gwt.maps.client.geocode.Waypoint;
import com.google.gwt.maps.client.geom.LatLngBounds;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.ruvego.project.client.ResultsDataGridView.ResultsColumnData;
import com.ruvego.project.shared.ItineraryDataPacket;

public class ItineraryPage {
	private static ItineraryPage page;

	private static ScrollPanel itineraryPanel;

	private static VerticalPanel vPanel;

	protected static DayActivityPlan[] itineraryPlan;

	private static Label btnRouteItinerary;

	private static AsyncCallback<ItineraryDataPacket> callbackItineraryResults;

	private static PopupPanel btnRouteItineraryPopUpPanel;

	private static ListBox listDays;

	private static VerticalPanel vRoutePanel;

	private static LinkedList<Waypoint> waypointsAll;

	private static DirectionQueryOptions opts;

	private static DirectionsCallback directionsCallback;

	public static ItineraryPage getPage() {
		if (page == null) {
			page = new ItineraryPage();
			assert(page != null);
		}
		return page;
	}

	private ItineraryPage() {
		System.out.println("Creating an object of type ItineraryPage");
		vPanel = new VerticalPanel();
		vPanel.setWidth("100%");
		itineraryPanel = new ScrollPanel(vPanel);
		vRoutePanel = new VerticalPanel();
		vRoutePanel.setWidth("150px");
		vRoutePanel.setStyleName("btnRoutePanel");
		vRoutePanel.setSpacing(5);

		btnRouteItineraryPopUpPanel = new PopupPanel(true, true);
		btnRouteItineraryPopUpPanel.setStyleName("popUpPanel");

		RootPanel.get().add(itineraryPanel, Ruvego.getIndent(), Ruvego.getOtherWidgetTop());
		itineraryPanel.setStyleName("boxBG");
		itineraryPanel.setPixelSize(DayActivityPlan.BOX_PANEL_WIDTH, Window.getClientHeight() - Ruvego.getOtherWidgetTop() - 
				Ruvego.getFooterHeight() - 3);

		Label lblRouteAll = new Label("Route All");
		lblRouteAll.setStyleName("btnRouteItemText");
		lblRouteAll.setWidth("100%");

		vRoutePanel.add(lblRouteAll);

		lblRouteAll.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				menuHide();
				waypointsAll.clear();
				for (int i = 0; i < ItineraryState.getNumDays(); i++) {
					waypointsAll.addAll(itineraryPlan[i].waypointLL);
				}

				mapRoute();
			}
		});


		HorizontalPanel routeDayPanel = new HorizontalPanel();

		Label lblRouteDay = new Label("Route : ");
		lblRouteDay.setStyleName("btnRouteItemTextNormal");

		listDays = new ListBox();

		routeDayPanel.add(lblRouteDay);
		routeDayPanel.add(listDays);

		routeDayPanel.setSpacing(5);

		vRoutePanel.add(routeDayPanel);
		btnRouteItineraryPopUpPanel.add(vRoutePanel);
		RootPanel.get().add(btnRouteItineraryPopUpPanel);

		btnRouteItinerary = new Label("Route");
		btnRouteItinerary.setStyleName("btnRouteItinerary");
		Ruvego.getMapsPanel().add(btnRouteItinerary);//, Ruvego.getMapsPanel().getOffsetWidth() - 90, 12);


		waypointsAll = new LinkedList<Waypoint>();

		btnRouteItinerary.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (btnRouteItineraryPopUpPanel.isVisible()) {
					menuHide();
				} else {
					menuShow();
				}

				//boxPlan.mapRoute();
			}
		});

		callbackItineraryResults = new AsyncCallback<ItineraryDataPacket>() {
			public void onFailure(Throwable caught) {
				History.newItem("homePage");
			}

			@Override
			public void onSuccess(ItineraryDataPacket result) {
				System.out.println("Client: Loading Itinerary Results");
				if (result.getReturnVal() == 0) {
					setupItinerary(result);
				} else {
					Ruvego.errorDisplayWithTimer("Access Denied. Redirecting to Home Page");
				}
			}
		};

		btnRouteItineraryPopUpPanel.addCloseHandler(new CloseHandler<PopupPanel>() {

			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				menuHide();
			}
		});

		listDays.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				menuHide();
				itineraryPlan[listDays.getSelectedIndex() - 1].mapRoute();
			}
		});

		directionsCallback = new DirectionsCallback() {

			@Override
			public void onSuccess(DirectionResults result) {
				ItineraryCommon.setTotalDistDuration(result.getDistance().inLocalizedUnits(), result.getDuration().inLocalizedUnits());
			}

			@Override
			public void onFailure(int statusCode) {
				Ruvego.errorDisplay("Google Maps: Route not found for 1 or more addresses in your list"); 
				ItineraryCommon.routeBriefPanel.setVisible(false);
			}
		};

		opts = new DirectionQueryOptions(Ruvego.getMapWidget(), 
				ItineraryCommon.directionsPanel);

		btnRouteItineraryPopUpPanel.addAutoHidePartner(btnRouteItinerary.getElement());


		btnRouteItineraryPanelalignments();

		menuHide();

		panelResizeAlignments();
	}

	protected void mapRoute() {
		ItineraryCommon.routeBriefPanel.setVisible(false);
		Ruvego.getMapWidget().clearOverlays();

		if (waypointsAll.size() == 0) {
			Ruvego.errorDisplay("Google Maps: No activities present");
			return;
		}

		if (waypointsAll.size() == 1) {
			Ruvego.errorDisplay("Google Maps: Only one activity present");
			return;
		}

		Ruvego.errorDisplayClear();
		Directions.loadFromWaypoints(waypointsAll.toArray(new Waypoint[waypointsAll.size()]), opts, directionsCallback);
	}

	public void setupItinerary(ItineraryDataPacket result) {
		DateTimeFormat format = DateTimeFormat.getFormat("MM/dd/yyyy");
		Date date = format.parse(result.getStartDate());

		ItineraryState.setName(result.getItineraryName());
		ItineraryState.setNumDays(result.getNumDays());
		/* Clear previous itinerary entries */
		vPanel.clear();
		Ruvego.getMapWidget().clearOverlays();

		itineraryPlan = new DayActivityPlan[result.getNumDays()];

		listDays.clear();

		for (int i = 0; i < result.getNumDays(); i++) {
			System.out.println("In loop : " + i);
			itineraryPlan[i] = new DayActivityPlan(vPanel, false);

			itineraryPlan[i].dayName = "Day " + (i + 1);

			if (i > 0) {
				CalendarUtil.addDaysToDate(date, 1);
			}
			itineraryPlan[i].addDayDatePanel(i + 1, format.format(date).toString());

			if (i == 0) {
				itineraryPlan[0].setupSrcBoxPanel();
			}

			if (result.getNumEntries(i) != 0) {
				itineraryPlan[i].addResults(result.getNameList(i), result.getAddressList(i), result.getObjectIdList(i), result.getNumEntries(i));
			}

			if (i == (result.getNumDays() - 1)) {
				itineraryPlan[i].setupDstBoxPanel();
			}
		}

		listDays.addItem("Choose");
		for (int i = 0; i < result.getNumDays(); i++) {
			listDays.addItem("Day " + (i + 1));
		}

	}

	public static void panelResizeAlignments() {
		itineraryPanel.setHeight((Ruvego.getClientHeight() - Ruvego.getOtherWidgetTop() - Ruvego.getFooterHeight() - RuvegoBoxPage.BOTTOM_BLANK_HEIGHT - 3) + "px");
		Ruvego.setMapsPosition(DayActivityPlan.BOX_PANEL_WIDTH, RuvegoBoxPage.BOTTOM_BLANK_HEIGHT);
		itineraryPanel.setPixelSize(DayActivityPlan.BOX_PANEL_WIDTH, Window.getClientHeight() - Ruvego.getOtherWidgetTop() - 
				Ruvego.getFooterHeight() - RuvegoBoxPage.BOTTOM_BLANK_HEIGHT - 3);
		Ruvego.panelAlignments();
		Ruvego.getMapsPanel().setWidgetPosition(btnRouteItinerary, Ruvego.getMapsPanel().getOffsetWidth() - 80, Ruvego.getMapsPanel().getOffsetHeight() - 60);
	}

	public void clearContent() {
		itineraryPanel.setVisible(false);
		btnRouteItinerary.setVisible(false);
		ItineraryCommon.routeBriefPanel.setVisible(false);
	}

	public void panelsView() {
		System.out.println("Itinerary view panels view");
		itineraryPanel.setVisible(true);
		btnRouteItinerary.setVisible(true);
		panelResizeAlignments();
	}

	public void fetchResults() {
		String[] output;
		output = Ruvego.parseString(History.getToken(), "/");

		ItineraryCommon.bounds = LatLngBounds.newInstance();
		Ruvego.getResultsFetchAsync().fetchItineraryData(output[1], LoginModule.getUsername(), callbackItineraryResults);
	}

	protected void menuShow() {	
		btnRouteItineraryPopUpPanel.setVisible(true);
		btnRouteItineraryPopUpPanel.show();
		//btnRouteItinerary.setStyleName("activityBtnMoreClick");
		listDays.setSelectedIndex(0);
		btnRouteItineraryPanelalignments();
	}


	protected static void menuHide() {
		btnRouteItineraryPopUpPanel.setVisible(false);
		//btnRouteItinerary.setStyleName("activityBtnMore");	
	}

	protected void btnRouteItineraryPanelalignments() {
		RootPanel.get().setWidgetPosition(btnRouteItineraryPopUpPanel, btnRouteItinerary.getAbsoluteLeft() - btnRouteItineraryPopUpPanel.getOffsetWidth() + 
				btnRouteItinerary.getOffsetWidth(), 
				btnRouteItinerary.getAbsoluteTop() - btnRouteItineraryPopUpPanel.getOffsetHeight());
	}

}
