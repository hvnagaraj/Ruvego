package com.ruvego.project.client;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import javax.swing.Scrollable;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
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

		btnRouteItineraryPopUpPanel = new PopupPanel(true, true);
		btnRouteItineraryPopUpPanel.setStyleName("popUpPanel");

		RootPanel.get().add(itineraryPanel, Ruvego.getIndent(), Ruvego.getOtherWidgetTop());
		itineraryPanel.setStyleName("boxBG");
		itineraryPanel.setPixelSize(DayActivityPlan.BOX_PANEL_WIDTH, Window.getClientHeight() - Ruvego.getOtherWidgetTop() - 
				Ruvego.getFooterHeight() - 3);

		Label lblRouteAll = new Label("Route All");
		lblRouteAll.setStyleName("menuItemText");
		lblRouteAll.setWidth("100%");

		vRoutePanel.add(lblRouteAll);

		lblRouteAll.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				menuHide();
			}
		});


		HorizontalPanel routeDayPanel = new HorizontalPanel();

		Label lblRouteDay = new Label("Route : ");
		lblRouteDay.setStyleName("menuItemTextNormal");

		listDays = new ListBox();

		routeDayPanel.add(lblRouteDay);
		routeDayPanel.add(listDays);

		routeDayPanel.setSpacing(5);

		vRoutePanel.add(routeDayPanel);
		btnRouteItineraryPopUpPanel.add(vRoutePanel);
		RootPanel.get().add(btnRouteItineraryPopUpPanel);

		btnRouteItinerary = new Label("Route");
		btnRouteItinerary.setStyleName("btnRoute");
		Ruvego.getMapsPanel().add(btnRouteItinerary);//, Ruvego.getMapsPanel().getOffsetWidth() - 90, 12);
		//RootPanel.get().add(btnRouteItinerary);

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

		btnRouteItineraryPopUpPanel.addAutoHidePartner(btnRouteItinerary.getElement());


		btnRouteItineraryPanelalignments();

		menuHide();

		panelResizeAlignments();
	}

	public void setupItinerary(ItineraryDataPacket result) {
		DateTimeFormat format = DateTimeFormat.getFormat("MM/dd/yyyy");
		Date date = format.parse(result.getStartDate());

		ItineraryState.setNumDays(result.getNumDays());
		/* Clear previous itinerary entries */
		vPanel.clear();
		Ruvego.getMapWidget().clearOverlays();

		itineraryPlan = new DayActivityPlan[result.getNumDays()];

		for (int i = 0; i < result.getNumDays(); i++) {
			System.out.println("In loop : " + i);
			itineraryPlan[i] = new DayActivityPlan(vPanel);

			itineraryPlan[i].dayName = "Day " + (i + 1);

			CalendarUtil.addDaysToDate(date, 1);
			itineraryPlan[i].addDayDatePanel(i + 1, format.format(date).toString());

			if (i == 0) {
				itineraryPlan[0].setupSrcBoxPanel();
			}

			if (result.getNumEntries(i) != 0) {
				itineraryPlan[i].addResults(result.getNameList(i), result.getAddressList(i), result.getNumEntries(i));
			}


			if (i == (result.getNumDays() - 1)) {
				itineraryPlan[i].setupDstBoxPanel();
			}
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
