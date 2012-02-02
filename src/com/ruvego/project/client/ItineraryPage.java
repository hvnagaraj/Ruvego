package com.ruvego.project.client;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import javax.swing.Scrollable;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
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

	private static Label btnRoute;

	private static AsyncCallback<ItineraryDataPacket> callbackItineraryResults;

	public static ItineraryPage getPage() {
		if (page == null) {
			page = new ItineraryPage();
		}
		return page;
	}

	private ItineraryPage() {
		System.out.println("Creating an object of type ItineraryPage");
		vPanel = new VerticalPanel();
		vPanel.setWidth("100%");
		itineraryPanel = new ScrollPanel(vPanel);

		RootPanel.get().add(itineraryPanel, Ruvego.getIndent(), Ruvego.getOtherWidgetTop());
		itineraryPanel.setStyleName("boxBG");
		itineraryPanel.setPixelSize(DayActivityPlan.BOX_PANEL_WIDTH, Window.getClientHeight() - Ruvego.getOtherWidgetTop() - 
				Ruvego.getFooterHeight() - 3);


		btnRoute = new Label("Route");
		btnRoute.setStyleName("btnRoute");
		Ruvego.getMapsPanel().add(btnRoute, Ruvego.getMapsPanel().getOffsetWidth() - 90, 12);

		btnRoute.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				//boxPlan.mapRoute();
			}
		});

		callbackItineraryResults = new AsyncCallback<ItineraryDataPacket>() {
			public void onFailure(Throwable caught) {
				System.out.println("Failure");
			}

			@Override
			public void onSuccess(ItineraryDataPacket result) {
				System.out.println("Client: Loading Itinerary Results");
				setupItinerary(result);
			}
		};
	}

	public void setupItinerary(ItineraryDataPacket result) {
		DateTimeFormat format = DateTimeFormat.getFormat("MM/dd/yyyy");
		Date date = format.parse(result.getStartDate());

		/* Clear previous itinerary entries */
		vPanel.clear();

		itineraryPlan = new DayActivityPlan[result.getNumDays()];

		for (int i = 0; i < result.getNumDays(); i++) {
			System.out.println("In loop : " + i);
			itineraryPlan[i] = new DayActivityPlan(vPanel);

			itineraryPlan[i].dayName = "Day " + (i + 1);
			
			CalendarUtil.addDaysToDate(date, i);
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
		Ruvego.getMapsPanel().setWidgetPosition(btnRoute, Ruvego.getMapsPanel().getOffsetWidth() - 80, Ruvego.getMapsPanel().getOffsetHeight() - 60);
		Ruvego.setMapsPosition(DayActivityPlan.BOX_PANEL_WIDTH, RuvegoBoxPage.BOTTOM_BLANK_HEIGHT);
		itineraryPanel.setPixelSize(DayActivityPlan.BOX_PANEL_WIDTH, Window.getClientHeight() - Ruvego.getOtherWidgetTop() - 
				Ruvego.getFooterHeight() - RuvegoBoxPage.BOTTOM_BLANK_HEIGHT - 3);
	}

	public static void clearContent() {
		itineraryPanel.setVisible(false);
	}

	public void panelsView() {
		itineraryPanel.setVisible(true);
		panelResizeAlignments();
	}

	public void fetchResults(String parameter) {
		// TODO Auto-generated method stub
		Ruvego.getResultsFetchAsync().fetchItineraryData(ItineraryState.ITINERARY_NAME, callbackItineraryResults);
	}

}
