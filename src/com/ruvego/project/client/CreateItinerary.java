package com.ruvego.project.client;

import java.util.Date;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.gwt.user.datepicker.client.DateBox;
import com.ruvego.project.shared.CreateItineraryPacket;
import com.sun.java.swing.plaf.windows.resources.windows;

public class CreateItinerary {
	private static CreateItinerary page;

	private static String ITINERARY_NAME = "";
	private static String NUM_DAYS = "";
	private static Date START_DATE;
	private static boolean FROM_BOX_PAGE = false;

	private static PopupPanel popUpPanel;
	private static VerticalPanel createItineraryPanel;

	static private CreateItineraryPacket createItineraryPacket;

	static private AsyncCallback<Boolean> callbackCreateItinerary;

	static private Label lblError;
	
	private static TextBox txtBoxName;
	private static ListBox listNumDays;
	private static DateBox dateBoxStartDate;
	
	private static Label lblStartDate;
	private static VerticalPanel vNumDaysPanel;
	
	private static ItineraryPage itineraryPage;

	public static CreateItinerary getPage() {
		if (page == null) {
			page = new CreateItinerary();
		}
		return page;
	}

	private CreateItinerary() {
		createItineraryPanel = new VerticalPanel();
		popUpPanel = new PopupPanel(true, true);

		popUpPanel.setAnimationEnabled(true);
		popUpPanel.setGlassEnabled(true);
		popUpPanel.setGlassStyleName("popUpPanel");
		popUpPanel.add(createItineraryPanel);
		RootPanel.get().add(popUpPanel);
		createItineraryPanel.setSize("300px", "10px");
		createItineraryPanel.setStyleName("itineraryBG");
		createItineraryPanel.setSpacing(15);

		VerticalPanel vNamePanel = new VerticalPanel();
		Label lblName = new Label("Itinerary Name");
		vNamePanel.add(lblName);
		lblName.setStyleName("silverContributeText");

		txtBoxName = new TextBox();
		txtBoxName.setSize("190px", "18px");
		vNamePanel.add(txtBoxName);
		createItineraryPanel.add(vNamePanel);


		vNumDaysPanel = new VerticalPanel();
		Label lblNumDays = new Label("No. of days");
		vNumDaysPanel.add(lblNumDays);
		lblNumDays.setStyleName("silverContributeText");

		listNumDays = new ListBox(false);
		listNumDays.addItem("Choose");
		for (int i = 1; i < 10; i++) {
			listNumDays.addItem(i + "");	
		}
		vNumDaysPanel.add(listNumDays);
		createItineraryPanel.add(vNumDaysPanel);

		VerticalPanel vStartDatePanel = new VerticalPanel();
		lblStartDate = new Label("Start Date");
		vStartDatePanel.add(lblStartDate);
		lblStartDate.setStyleName("silverContributeText");

		dateBoxStartDate = new DateBox();
		dateBoxStartDate.setFormat(new DateBox.DefaultFormat(DateTimeFormat.getFormat("MM/dd/yyyy")));

		vStartDatePanel.add(dateBoxStartDate);
		createItineraryPanel.add(vStartDatePanel);

		HorizontalPanel savePanel = new HorizontalPanel();
		Image imgSave = new Image("Images/saveicon.png");
		imgSave.setStyleName("imgLogo");
		savePanel.add(imgSave);

		lblError = new Label("");
		savePanel.add(lblError);
		lblError.setStyleName("itineraryLoginError");

		createItineraryPanel.add(savePanel);

		callbackCreateItinerary = new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				System.err.println("Error");
			}

			@Override
			public void onSuccess(Boolean result) {
				if (result == false) {
					infoCreateItinerary("Itinerary name already exists. Use a different name");
					return;
				}
				
				Ruvego.setItineraryText(ITINERARY_NAME);
				Ruvego.itineraryNamePanelAlignments();
				
				History.newItem("itineraryView=?" + ITINERARY_NAME);
				createItinerary();
				System.out.println("Client: Successfullly created itinerary");

			}
		};


		imgSave.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (LoginModule.isUserAuthenticated() == false) {
					infoCreateItinerary("Login Required");
					LoginModule.showLogin();
				} else {					
					if (txtBoxName.getText().equalsIgnoreCase("")) {
						infoCreateItinerary("Itinerary name cannot left blank");
						return;
					}

					if (vNumDaysPanel.isVisible() && listNumDays.getSelectedIndex() == 0) {
							infoCreateItinerary("Choose the number of days");
							return;
					}

					createItineraryPacket = new CreateItineraryPacket(txtBoxName.getText(), NUM_DAYS, 
							dateBoxStartDate.getTextBox().getText(), LoginModule.getUsername());  

					ITINERARY_NAME = txtBoxName.getText();
					START_DATE = dateBoxStartDate.getValue(); 
							//dateBoxStartDate.getTextBox().getValue();
					
					Ruvego.getResultsWriteService().writeCreateItinerary(createItineraryPacket, callbackCreateItinerary);

				}

			}
		});

		popUpPanel.addCloseHandler(new CloseHandler<PopupPanel>() {

			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				clearContent();
			}
		});
		
		listNumDays.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				NUM_DAYS = listNumDays.getItemText(listNumDays.getSelectedIndex());
			}
		});
	}

	protected void createItinerary() {
		itineraryPage = ItineraryPage.getPage();
		itineraryPage.fetchResults(ITINERARY_NAME);
		
		ItineraryPage.panelResizeAlignments();
		Ruvego.panelAlignments();
	}

	public void infoCreateItinerary(String text) {
		lblError.setText(text);
	}

	public void panelsView() {
		popUpPanel.setVisible(true);
		popUpPanel.show();
		popUpPanel.center();
	}
	
	public void panelsMultiDayView() {
		Ruvego.setMapsPosition(DayActivityPlan.BOX_PANEL_WIDTH, RuvegoBoxPage.BOTTOM_BLANK_HEIGHT);
		panelsView();
		vNumDaysPanel.setVisible(true);
		lblStartDate.setText("Start Date");
		FROM_BOX_PAGE = false;
	}
	
	public void panelsOneDayView() {
		panelsView();
		vNumDaysPanel.setVisible(false);
		lblStartDate.setText("Date");
		NUM_DAYS = "1";
		FROM_BOX_PAGE = true;
	}

	public void clearContent() {
		popUpPanel.setVisible(false);
		txtBoxName.setText("");
		listNumDays.setSelectedIndex(0);
		dateBoxStartDate.setValue(null);
		infoCreateItinerary("");
	}
}
