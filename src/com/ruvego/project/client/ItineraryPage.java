package com.ruvego.project.client;

import java.util.Date;

import javax.swing.Scrollable;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.CalendarUtil;

public class ItineraryPage {
	private static ItineraryPage page;
	
	private static ScrollPanel itineraryPanel;
	
	private static VerticalPanel vPanel;

	protected static DayActivityPlan[] itineraryPlan;
	
	private static Label btnRoute;
	
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

	}
	
	public void setupItinerary(int numDays, Date START_DATE, String[] entry, int boxValueCount) {
		int i = 0;
		DateTimeFormat format = DateTimeFormat.getFormat("MM/dd/yyyy");
		Date date;
		

		/* Clear previous itinerary entries */
		vPanel.clear();
		
		itineraryPlan = new DayActivityPlan[numDays];
		
		for (i = 0; i < numDays; i++) {
			System.out.println("In loop : " + i);
			itineraryPlan[i] = new DayActivityPlan(vPanel);
			
			date = (Date) START_DATE.clone();
			CalendarUtil.addDaysToDate(date, i);
			itineraryPlan[i].addDayDatePanel(i + 1, format.format(date).toString());
			itineraryPlan[i].dayName = "Day " + (i + 1);

			if (i == 0) {
				itineraryPlan[0].setupSrcBoxPanel();
			}
			
			if (boxValueCount != 0) {
				itineraryPlan[i].addResults(entry, RuvegoBoxPage.boxValueCount);
			}
			
			
			if (i == (numDays - 1)) {
				itineraryPlan[i].setupDstBoxPanel();
			}
		}
	}
}
