package com.ruvego.project.client;


import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.maps.client.geocode.Geocoder;
import com.google.gwt.maps.client.geocode.LatLngCallback;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;

public class ContributeAddActivity {

	private static ContributeAddActivity page;
	
	/* Constants */
	public static final int BIN_DAYTIME = 1;
	public static final int BIN_NIGHTLIFE = 2;
	public static final int LBL_FIXED_DELTA = 15;
	public static int PANEL_TOP = 0;

	static private AbsolutePanel activityPanel;

	static private AsyncCallback<String[]> callbackCategoryColumns;

	static private CheckBox chkBoxDaytime, chkBoxNightlife;
	
	static private TextArea textAreaBrief;
	static private TextBox textBoxWebsite;
	
	static private AsyncCallback<Boolean> callbackActivitySubmit;

	public static AbsolutePanel getPanel() {
		return activityPanel;
	}

	private static final String UPLOAD_ACTION_URL = GWT.getModuleBaseURL() + "upload";

	private static Label lblError;

	private static WriteActivityPacket writeData;

	private static Label lblName;
	private static Label lblRating;
	private static Label lblColumn1;
	private static Label lblColumn2;
	private static Label lblColumn3;

	private static TextBox textBoxName;
	private static TextBox textBoxRating;
	private static TextBox textBox1;
	private static TextBox textBox2;
	private static TextBox textBox3;
	private static TextBox textBoxContact;

	private static Button btnSubmit, btnClear;
	private static Label lblAddress;

	private static ListBox listTypeOptions = new ListBox();
	private static Label lblSubCategoryList;
	private static ListBox listBoxSubCategory;
	private static FormPanel formPanel;
	private static Label lblEntryfee;
	private static TextBox textBoxEntryFee;
	private static Label lblToll;
	private static TextBox textBoxToll;
	private static Label lblParking;
	private static TextArea textAreaParking;
	private static TextArea textAreaAddress;
	private static ListBox listBoxCategory;
	private static ListBox listTimings;
	private static ListBox listFromTime;
	private static ListBox listToTime;

	private static double latN, latS, latE, latW;
	private static double lonN, lonS, lonE, lonW;

	private static double deg;
	private static double bearing;
	private static double earthRadius = 3959; //In Miles
	private static double distance = 150; //In Miles

	private static String filename;
	private static String timingsText = "";
	private static int checkBoxValue = 0;
	private static Label lblDay;
	private static Label lblImgUpload;
	private static FileUpload fileUpload;
	private static String formattedContact;
	
	private static AsyncCallback<String[]> callbackCategoryList;


	private ContributeAddActivity() {
		activityPanel = new AbsolutePanel();

		AbsolutePanel backPanel1 = new AbsolutePanel();
		AbsolutePanel backPanel2 = new AbsolutePanel();
		AbsolutePanel backPanel3 = new AbsolutePanel();

		//TODO To be changed. RootPanel must not be accessed directly
		/*
		RootPanel rootPanel = RootPanel.get();
		rootPanel.add(activityPanel, 10, 10);
		*/
		
		RuvegoContribute.getContributePanel().add(activityPanel, 0, 50);

		activityPanel.setSize("100%", "596px");

		activityPanel.add(backPanel1, 50, 40);
		backPanel1.setSize("265px", "485px");
		backPanel1.setStyleName("backPanel");
		
		PANEL_TOP = backPanel1.getAbsoluteTop();

		Label lblCategoryList = new Label("Category");
		backPanel1.add(lblCategoryList, 27, 22);
		lblCategoryList.setStyleName("contributeText");

		lblSubCategoryList = new Label("Sub Category");
		backPanel1.add(lblSubCategoryList, 27, 66);
		lblSubCategoryList.setStyleName("contributeText");

		listBoxSubCategory = new ListBox(false);
		backPanel1.add(listBoxSubCategory, 27, lblSubCategoryList.getAbsoluteTop() - PANEL_TOP + LBL_FIXED_DELTA);
		listBoxSubCategory.setVisibleItemCount(1);
		listBoxSubCategory.setWidth("200px");

		lblName = new Label("Name");
		backPanel1.add(lblName, 27, 114);
		lblName.setStyleName("contributeText");
		
		textBoxName = new TextBox();
		backPanel1.add(textBoxName, 27, lblName.getAbsoluteTop() - PANEL_TOP + LBL_FIXED_DELTA);
		textBoxName.setSize("190px", "18px");
		textBoxName.setFocus(true);

		lblEntryfee = new Label("Entry Fee");
		backPanel1.add(lblEntryfee, 27, 410);
		lblEntryfee.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		lblEntryfee.setSize("75px", "18px");
		lblEntryfee.setStyleName("contributeText");

		lblColumn3 = new Label("Column3");
		backPanel1.add(lblColumn3, 27, 351);
		lblColumn3.setStyleName("contributeText");

		lblColumn2 = new Label("Column2");
		lblColumn2.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		backPanel1.add(lblColumn2, 27, 291);
		lblColumn2.setStyleName("contributeText");

		lblColumn1 = new Label("Column1");
		backPanel1.add(lblColumn1, 27, 232);
		lblColumn1.setStyleName("contributeText");

		lblRating = new Label("Rating");
		backPanel1.add(lblRating, 27, 173);
		lblRating.setStyleName("contributeText");

		listBoxCategory = new ListBox(false);
		backPanel1.add(listBoxCategory, 27, lblCategoryList.getAbsoluteTop() - PANEL_TOP + LBL_FIXED_DELTA);
		listBoxCategory.setWidth("200px");
		listBoxCategory.addItem("Choose");

		textBoxRating = new TextBox();
		backPanel1.add(textBoxRating, 27, lblRating.getAbsoluteTop() - PANEL_TOP + LBL_FIXED_DELTA);
		textBoxRating.setSize("190px", "18px");

		textBox1 = new TextBox();
		backPanel1.add(textBox1, 27, lblColumn1.getAbsoluteTop() - PANEL_TOP + LBL_FIXED_DELTA);
		backPanel1.add(listTypeOptions, 27, lblColumn1.getAbsoluteTop() - PANEL_TOP + LBL_FIXED_DELTA);
		textBox1.setSize("190px", "18px");

		textBox2 = new TextBox();
		backPanel1.add(textBox2, 27, lblColumn2.getAbsoluteTop() - PANEL_TOP + LBL_FIXED_DELTA);
		textBox2.setSize("190px", "18px");

		textBox3 = new TextBox();
		backPanel1.add(textBox3, 27, lblColumn3.getAbsoluteTop() - PANEL_TOP + LBL_FIXED_DELTA);
		textBox3.setSize("190px", "18px");

		textBoxEntryFee = new TextBox();
		backPanel1.add(textBoxEntryFee, 27, lblEntryfee.getAbsoluteTop() - PANEL_TOP + LBL_FIXED_DELTA);
		textBoxEntryFee.setSize("190px", "18px");

		listBoxCategory.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				clearColumnData();
				clearSubCategoryList();

				if (listBoxCategory.getSelectedIndex() != 0) {
					Ruvego.getResultsFetchAsync().fetchCategoryColumns(listBoxCategory.getItemText(listBoxCategory.getSelectedIndex()), "Category", 
							callbackCategoryColumns);
				}
			}
		});
		
		lblSubCategoryList.setVisible(false);
		listBoxSubCategory.setVisible(false);
		listBoxSubCategory.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				clearColumnData();

				if (listBoxSubCategory.getSelectedIndex() != 0) {
					Ruvego.getResultsFetchAsync().fetchCategoryColumns(listBoxSubCategory.getItemText(listBoxSubCategory.getSelectedIndex()), 
							"SubCategory", callbackCategoryColumns);
				}
			}
		});

		activityPanel.add(backPanel2, 50 + 45 + backPanel1.getOffsetWidth(), 40);
		backPanel2.setSize("265px", "485px");
		backPanel2.setStyleName("backPanel");

		lblImgUpload = new Label("Image Upload");
		lblImgUpload.setStyleName("contributeText");
		lblImgUpload.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		backPanel2.add(lblImgUpload, 10, 412);
		lblImgUpload.setSize("94px", "15px");

		formPanel = new FormPanel();
		backPanel2.add(formPanel, 10, lblImgUpload.getAbsoluteTop() - PANEL_TOP + LBL_FIXED_DELTA);
		formPanel.setMethod(FormPanel.METHOD_POST);
		formPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
		formPanel.setAction(UPLOAD_ACTION_URL);

		fileUpload = new FileUpload();
		formPanel.setWidget(fileUpload);
		fileUpload.setName("uploadFormElement");
		fileUpload.setSize("100%", "100%");

		Label lblFrom = new Label("From");
		backPanel2.add(lblFrom, 59, 64);
		lblFrom.setStyleName("contributeText");

		lblDay = new Label("Day");
		lblDay.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		backPanel2.add(lblDay, 17, 80);
		lblDay.setSize("37px", "18px");
		lblDay.setStyleName("contributeText");


		Label lblToTime = new Label("Time");
		lblToTime.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		backPanel2.add(lblToTime, 23, 118);
		lblToTime.setStyleName("contributeText");

		final ListBox listFrom = new ListBox(false);
		backPanel2.add(listFrom, 59, 79);
		listFrom.setSize("84px", "22px");
		listFrom.addItem("Mon");
		listFrom.addItem("Tue");
		listFrom.addItem("Wed");
		listFrom.addItem("Thu");
		listFrom.addItem("Fri");
		listFrom.addItem("Sat");
		listFrom.addItem("Sun");

		final ListBox listTo = new ListBox(false);
		backPanel2.add(listTo, 160, 79);
		listTo.setSize("84px", "22px");
		listTo.addItem("Choose");
		listTo.addItem("Mon");
		listTo.addItem("Tue");
		listTo.addItem("Wed");
		listTo.addItem("Thu");
		listTo.addItem("Fri");
		listTo.addItem("Sat");
		listTo.addItem("Sun");

		listFromTime = new ListBox();
		backPanel2.add(listFromTime, 59, 116);
		listFromTime.setSize("84px", "22px");
		listFromTime.addItem("Closed");
		listFromTime.addItem("24 hours");
		listFromTime.addItem("8:00am");
		listFromTime.addItem("8:30am");
		listFromTime.addItem("9:00am");
		listFromTime.addItem("9:30am");
		listFromTime.addItem("10:00am");
		listFromTime.addItem("10:30am");
		listFromTime.addItem("11:00am");
		listFromTime.addItem("11:30am");
		listFromTime.addItem("12:00am");
		listFromTime.addItem("12:30am");
		listFromTime.addItem("1:00pm");
		listFromTime.addItem("1:30pm");
		listFromTime.addItem("2:00pm");
		listFromTime.addItem("2:30pm");
		listFromTime.addItem("3:00pm");
		listFromTime.addItem("3:30pm");
		listFromTime.addItem("4:00pm");
		listFromTime.addItem("4:30pm");
		listFromTime.addItem("5:00pm");
		listFromTime.addItem("5:30pm");
		listFromTime.addItem("6:00pm");
		listFromTime.addItem("6:30pm");
		listFromTime.addItem("7:00pm");
		listFromTime.addItem("7:30pm");
		listFromTime.addItem("8:00pm");
		listFromTime.addItem("8:30pm");
		listFromTime.addItem("9:00pm");
		listFromTime.addItem("9:30pm");
		listFromTime.addItem("10:00pm");
		listFromTime.addItem("10:30pm");
		listFromTime.addItem("11:00pm");
		listFromTime.addItem("11:30pm");
		listFromTime.addItem("12:00pm");
		listFromTime.addItem("12:30pm");
		listFromTime.addItem("1:00am");
		listFromTime.addItem("1:30am");
		listFromTime.addItem("2:00am");
		listFromTime.addItem("2:30am");
		listFromTime.addItem("3:00am");
		listFromTime.addItem("3:30am");
		listFromTime.addItem("4:00am");
		listFromTime.addItem("4:30am");
		listFromTime.addItem("5:00am");
		listFromTime.addItem("5:30am");
		listFromTime.addItem("6:00am");
		listFromTime.addItem("6:30am");
		listFromTime.addItem("7:00am");
		listFromTime.addItem("7:30am");

		listToTime = new ListBox();
		backPanel2.add(listToTime, 160, 116);
		listToTime.setSize("84px", "22px");
		listToTime.addItem("Closed");
		listToTime.addItem("24 hours");
		listToTime.addItem("8:00am");
		listToTime.addItem("8:30am");
		listToTime.addItem("9:00am");
		listToTime.addItem("9:30am");
		listToTime.addItem("10:00am");
		listToTime.addItem("10:30am");
		listToTime.addItem("11:00am");
		listToTime.addItem("11:30am");
		listToTime.addItem("12:00am");
		listToTime.addItem("12:30am");
		listToTime.addItem("1:00pm");
		listToTime.addItem("1:30pm");
		listToTime.addItem("2:00pm");
		listToTime.addItem("2:30pm");
		listToTime.addItem("3:00pm");
		listToTime.addItem("3:30pm");
		listToTime.addItem("4:00pm");
		listToTime.addItem("4:30pm");
		listToTime.addItem("5:00pm");
		listToTime.addItem("5:30pm");
		listToTime.addItem("6:00pm");
		listToTime.addItem("6:30pm");
		listToTime.addItem("7:00pm");
		listToTime.addItem("7:30pm");
		listToTime.addItem("8:00pm");
		listToTime.addItem("8:30pm");
		listToTime.addItem("9:00pm");
		listToTime.addItem("9:30pm");
		listToTime.addItem("10:00pm");
		listToTime.addItem("10:30pm");
		listToTime.addItem("11:00pm");
		listToTime.addItem("11:30pm");
		listToTime.addItem("12:00pm");
		listToTime.addItem("12:30pm");
		listToTime.addItem("1:00am");
		listToTime.addItem("1:30am");
		listToTime.addItem("2:00am");
		listToTime.addItem("2:30am");
		listToTime.addItem("3:00am");
		listToTime.addItem("3:30am");
		listToTime.addItem("4:00am");
		listToTime.addItem("4:30am");
		listToTime.addItem("5:00am");
		listToTime.addItem("5:30am");
		listToTime.addItem("6:00am");
		listToTime.addItem("6:30am");
		listToTime.addItem("7:00am");
		listToTime.addItem("7:30am");
		Label lblTo = new Label("To");
		backPanel2.add(lblTo, 161, 64);
		lblTo.setStyleName("contributeText");

		Button btnTimingsAdd = new Button("Add");
		backPanel2.add(btnTimingsAdd, 55, 155);
		btnTimingsAdd.setStyleName("button");
		btnTimingsAdd.setPixelSize(70, 30);

		Button btnTimingsClear = new Button("Clear");
		backPanel2.add(btnTimingsClear, 140, 155);
		btnTimingsClear.setStyleName("button");
		btnTimingsClear.setPixelSize(70, 30);

		listTimings = new ListBox(true);
		listTimings.setSize("221px", "114px");
		backPanel2.add(listTimings, 22, 200);
		
		Label lblContact = new Label("Contact  (XXX-XXX-XXXX)");
		lblContact.setStyleName("contributeText");
		backPanel2.add(lblContact, 27, 350);
		
		textBoxContact = new TextBox();
		backPanel2.add(textBoxContact, 27, lblContact.getAbsoluteTop() - PANEL_TOP + LBL_FIXED_DELTA);
		textBoxContact.setSize("190px", "18px");
		
		btnTimingsClear.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				listTimings.clear();
			}
		});

		btnTimingsAdd.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				String from = listFrom.getItemText(listFrom.getSelectedIndex());
				String to = listTo.getItemText(listTo.getSelectedIndex());

				if (listTo.getSelectedIndex() != 0) {
					if (listFromTime.getSelectedIndex() == 0) {
						listTimings.addItem(from + " - " + to + " : " + "Closed");
					} else if (listFromTime.getSelectedIndex() == 1) {
						listTimings.addItem(from + " - " + to + " : " + "24 hours");
					} else {
						listTimings.addItem(from + " - " + to + " : " + listFromTime.getItemText(listFromTime.getSelectedIndex()) + " - " + 
								listToTime.getItemText(listToTime.getSelectedIndex()));	
					}
				} else {
					if (listFromTime.getSelectedIndex() == 0) {
						listTimings.addItem(from + " : " + "Closed");
					} else if (listFromTime.getSelectedIndex() == 1) {
						listTimings.addItem(from + " : " + "24 hours");
					} else {
						listTimings.addItem(from + " : " + listFromTime.getItemText(listFromTime.getSelectedIndex()) + " - " + 
								listToTime.getItemText(listToTime.getSelectedIndex()));
					}
				}
			}
		});

		// Add an event handler to the form.
		formPanel.addSubmitHandler(new FormPanel.SubmitHandler() {
			public void onSubmit(SubmitEvent event) {
				// This event is fired just before the form is submitted. We can
				// take this opportunity to perform validation.
				if (fileUpload.getFilename().isEmpty() == true) {
					Window.alert("Choose a file to upload");
					event.cancel();
				}
			}
		});

		formPanel.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
			public void onSubmitComplete(SubmitCompleteEvent event) {
				// When the form submission is successfully completed, this
				// event is fired. Assuming the service returned a response of type
				// text/html, we can get the result text here (see the FormPanel
				// documentation for further explanation).
				//Window.alert(event.getResults());

				System.out.println("Server: Image upload successful");

				/* Type, Place, Name, Brief, Address, Website, Rating, TextBox1, TextBox2, TextBox3, Upload, Entry Fee, 
				 * Toll, Free Parking, Paid Parking, Additional Info 
				 */ 
				String delims;
				String[] tokens = null;
				if (event.getResults().contains("Created filename :")) {
					delims = "Created filename : ";
					tokens = event.getResults().split(delims);

					delims = "</pre>";
					tokens = tokens[1].split(delims);

					filename = tokens[0];
					System.out.println("File: " + tokens[0]);
				}


				if (chkBoxDaytime.getValue() == true) {
					checkBoxValue = checkBoxValue | BIN_DAYTIME;

				} 
				if (chkBoxNightlife.getValue() == true) {
					checkBoxValue = checkBoxValue | BIN_NIGHTLIFE;
				}

				final String col1Data;
				if (listTypeOptions.isVisible() == true) {
					col1Data = listTypeOptions.getItemText(listTypeOptions.getSelectedIndex());
				} else {
					col1Data = RuvegoContribute.toCamelCase(textBox1.getText());
				}

				final String subCategoryData;
				if (listBoxSubCategory.isVisible() == true) {
					subCategoryData = listBoxSubCategory.getItemText(listBoxSubCategory.getSelectedIndex());
				} else {
					subCategoryData = null;
				}

				timingsText = "";
				for (int i = 0; i < listTimings.getItemCount(); i++) {
					timingsText = timingsText + listTimings.getItemText(i) + "<BR>"; 
				}
				
				Geocoder geocoder = Ruvego.getGeocode();
				geocoder.getLatLng(textAreaAddress.getText(), new LatLngCallback() {

					@Override
					public void onFailure() {
						throwError("Invalid Street Address. Please check on google maps for the correct address");
					}

					@Override
					public void onSuccess(LatLng point) {
						/* For North co-ordinates */
						deg = 0;
						bearing = Math.toRadians(deg);

						latN = Math.asin(Math.sin(point.getLatitudeRadians()) * Math.cos(distance/earthRadius) + 
								Math.cos(point.getLatitudeRadians()) * Math.sin(distance/earthRadius) * Math.cos(bearing));
						lonN = point.getLongitudeRadians() + Math.atan2(Math.sin(bearing) * Math.sin(distance/earthRadius) * Math.cos(point.getLatitudeRadians()), 
								Math.cos(distance/earthRadius) - Math.sin(point.getLatitudeRadians()) * Math.sin(latN));

						/* For East co-ordinates */
						deg = 90;
						bearing = Math.toRadians(deg);

						latE = Math.asin(Math.sin(point.getLatitudeRadians()) * Math.cos(distance/earthRadius) + 
								Math.cos(point.getLatitudeRadians()) * Math.sin(distance/earthRadius) * Math.cos(bearing));
						lonE = point.getLongitudeRadians() + Math.atan2(Math.sin(bearing) * Math.sin(distance/earthRadius) * Math.cos(point.getLatitudeRadians()), 
								Math.cos(distance/earthRadius) - Math.sin(point.getLatitudeRadians()) * Math.sin(latE));

						/* For South co-ordinates */
						deg = 180;
						bearing = Math.toRadians(deg);

						latS = Math.asin(Math.sin(point.getLatitudeRadians()) * Math.cos(distance/earthRadius) + 
								Math.cos(point.getLatitudeRadians()) * Math.sin(distance/earthRadius) * Math.cos(bearing));
						lonS = point.getLongitudeRadians() + Math.atan2(Math.sin(bearing) * Math.sin(distance/earthRadius) * Math.cos(point.getLatitudeRadians()), 
								Math.cos(distance/earthRadius) - Math.sin(point.getLatitudeRadians()) * Math.sin(latS));

						/* For West co-ordinates */
						deg = 270;
						bearing = Math.toRadians(deg);

						latW = Math.asin(Math.sin(point.getLatitudeRadians()) * Math.cos(distance/earthRadius) + 
								Math.cos(point.getLatitudeRadians()) * Math.sin(distance/earthRadius) * Math.cos(bearing));
						lonW = point.getLongitudeRadians() + Math.atan2(Math.sin(bearing) * Math.sin(distance/earthRadius) * Math.cos(point.getLatitudeRadians()), 
								Math.cos(distance/earthRadius) - Math.sin(point.getLatitudeRadians()) * Math.sin(latW));

						writeData = new WriteActivityPacket(RuvegoContribute.getTypeListbox().getItemText(RuvegoContribute.getTypeListbox().getSelectedIndex()), 
								"", RuvegoContribute.toCamelCase(textBoxName.getText()), textAreaBrief.getText(), textAreaAddress.getText(), 
								textBoxWebsite.getText(), textBoxRating.getText(), col1Data, RuvegoContribute.toCamelCase(textBox2.getText()),
								RuvegoContribute.toCamelCase(textBox3.getText()), filename, textBoxEntryFee.getText(), textBoxToll.getText(),
								textAreaParking.getText(), timingsText, 
								listBoxCategory.getItemText(listBoxCategory.getSelectedIndex()), subCategoryData, checkBoxValue,
								latN, latS, latE, latW, lonN, lonS, lonE, lonW, formattedContact);

						RuvegoContribute.getResultsWriteService().writeResults(writeData, callbackActivitySubmit);
					}
				});
			}
		});

		activityPanel.add(backPanel3, 50 + 45 + 45 + backPanel1.getOffsetWidth() * 2, 40);
		backPanel3.setSize("265px", "485px");
		backPanel3.setStyleName("backPanel");

		lblAddress = new Label("Address");
		lblAddress.setWordWrap(false);
		backPanel3.add(lblAddress, 35, 22);
		lblAddress.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		lblAddress.setSize("60px", "18px");
		lblAddress.setStyleName("contributeText");

		textAreaAddress = new TextArea();
		backPanel3.add(textAreaAddress, 35, lblAddress.getAbsoluteTop() - PANEL_TOP + LBL_FIXED_DELTA);
		textAreaAddress.setText("");
		textAreaAddress.setSize("188px", "57px");

		Label lblBrief = new Label("Brief Description");
		backPanel3.add(lblBrief, 35, 125);
		lblBrief.setSize("108px", "18px");
		lblBrief.setStyleName("contributeText");

		textAreaBrief = new TextArea();
		backPanel3.add(textAreaBrief, 35, lblBrief.getAbsoluteTop() - PANEL_TOP + LBL_FIXED_DELTA);
		textAreaBrief.setSize("189px", "57px");

		Label lblWebsite = new Label("Website");
		lblWebsite.setWordWrap(false);
		backPanel3.add(lblWebsite, 35, 219);
		lblWebsite.setSize("52px", "18px");
		lblWebsite.setStyleName("contributeText");

		textBoxWebsite = new TextBox();
		backPanel3.add(textBoxWebsite, 35, lblWebsite.getAbsoluteTop() - PANEL_TOP + LBL_FIXED_DELTA);
		textBoxWebsite.setSize("190px", "18px");

		lblToll = new Label("Toll");
		backPanel3.add(lblToll, 35, 280);
		lblToll.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		lblToll.setSize("39px", "12px");
		lblToll.setStyleName("contributeText");

		textBoxToll = new TextBox();
		backPanel3.add(textBoxToll, 35, lblToll.getAbsoluteTop() - PANEL_TOP + LBL_FIXED_DELTA);
		textBoxToll.setSize("190px", "18px");

		lblParking = new Label("Parking");
		backPanel3.add(lblParking, 35, 335);
		lblParking.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		lblParking.setSize("60px", "18px");
		lblParking.setStyleName("contributeText");

		textAreaParking = new TextArea();
		backPanel3.add(textAreaParking, 35, lblParking.getAbsoluteTop() - PANEL_TOP + LBL_FIXED_DELTA);
		textAreaParking.setText("");
		textAreaParking.setSize("189px", "57px");

		Label lblActivityType = new Label("Activity Type");
		backPanel3.add(lblActivityType, 35, 430);
		lblActivityType.setStyleName("contributeText");

		chkBoxDaytime = new CheckBox("Daytime");
		chkBoxDaytime.setStyleName("chkBoxBlack");
		backPanel3.add(chkBoxDaytime, 35, lblActivityType.getAbsoluteTop() - PANEL_TOP + LBL_FIXED_DELTA);

		chkBoxNightlife = new CheckBox("Nightlife");
		chkBoxNightlife.setStyleName("chkBoxBlack");
		backPanel3.add(chkBoxNightlife, 130, lblActivityType.getAbsoluteTop() - PANEL_TOP + LBL_FIXED_DELTA);



		RuvegoContribute.getContributePanel().add(activityPanel, 0, 50);

		lblError = new Label("Place name cannot be left blank");
		activityPanel.add(lblError, 50, 10);
		lblError.setSize("645px", "25px");
		lblError.setStyleName("orangeText");

		btnSubmit = new Button("New button");
		btnSubmit.setText("Submit");
		btnSubmit.setStyleName("button");
		btnSubmit.setPixelSize(70, 30);


		btnClear = new Button("New button");
		btnClear.setText("Clear");
		btnClear.setStyleName("button");
		btnClear.setPixelSize(70, 30);

		HorizontalPanel btnPanel = new HorizontalPanel();
		btnPanel.add(btnSubmit);
		btnPanel.add(btnClear);
		btnPanel.setSpacing(10);
		System.out.println(btnPanel.getOffsetWidth());
		activityPanel.add(btnPanel);
		activityPanel.setWidgetPosition(btnPanel, (985 - btnPanel.getOffsetWidth())/2, 544);

		btnSubmit.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (listBoxCategory.getSelectedIndex() == 0) {
					throwError("Please choose a Category");
					return;
				}
				
		        if (textBoxName.getText().equalsIgnoreCase("")) {
					throwError("Enter the name of the activity to be added");
					return;
				}

				if (listTimings.getItemCount() == 0) {
					throwError("Hours of Operation must be entered");
					return;
				}
				
				try {
				    double x = Double.parseDouble(textBoxRating.getText());
				    if (x <= 0 || x > 5) {
				    	throwError("Rating must be in the range 0.1 - 5.0");
				    	return;
				    }
				} catch(NumberFormatException nFE) {
					throwError("Rating must be in the format XX.X, where X is an Integer");
					return;
				}
				
				String contact = textBoxContact.getText().replaceAll("[^\\d]", "");
				if (contact.equalsIgnoreCase("")) {
					throwError("Enter a phone number in the format : XXX-XXX-XXXX");
					return;
				}
				
				int counter = 0;
		        
		        for (int i = 0; i < contact.length(); i++) {
		            if (Character.isDigit(contact.charAt(i))) {
		                counter++;
		            }
		        }

		        if (counter < 10 || counter > 10) {
					throwError("Phone number must be a 10 digit number. Enter the number in the format : XXX-XXX-XXXX");
					return;
		        }
				
		        try {
		        	long number = Long.parseLong(contact);
					if (number == 0) {
						throwError("0 is an invalid Phone number. Enter the number in the format : XXX-XXX-XXXX");
						return;
					}
		        } catch(NumberFormatException nFE) {
		        	throwError("Phone number must be a 10 digit number. Enter the number in the format : XXX-XXX-XXXX");
					return;
				}
		        
		        formattedContact = "+1 (" + contact.substring(0, 3) + ") " + 
		        		contact.substring(3, 6) + "-" + contact.substring(6, contact.length());
		        
		        if (fileUpload.getFilename().equalsIgnoreCase("")) {
		        	throwError("Choose a file to upload");
					return;
		        }
		        
		        if (textAreaAddress.getText().equalsIgnoreCase("")) {
					throwError("Enter the address");
					return;
				}
		        
				if (chkBoxDaytime.getValue() == false && chkBoxNightlife.getValue() == false) {
					throwError("Choose Daytime or Nightlife or both");
					return;
				}

				lblError.setVisible(false);
				formPanel.submit();
			}


		});

		btnClear.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				resetTableData();
			}

		});
		lblError.setVisible(false);


		/* Fetch the Type Options */
		final AsyncCallback<String[]> callbackTypeOptions = new AsyncCallback<String[]>() {

			@Override
			public void onFailure(Throwable caught) {
				System.err.println("Error");
			}

			public void onSuccess(String[] result) {
				listTypeOptions.clear();
				for (int i = 0; i < result.length; i++) {
					listTypeOptions.addItem(result[i]);
				}		
			}

		};


		/* Fetch the Category List */
		callbackCategoryList = new AsyncCallback<String[]>() {

			@Override
			public void onFailure(Throwable caught) {
				System.err.println("Error");
			}

			@Override
			public void onSuccess(String[] result) {
				listBoxCategory.clear();
				listBoxCategory.addItem("Choose");
				for (int i = 0; i < result.length; i++) {
					listBoxCategory.addItem(result[i]);
				}

			}

		};
		
		/* Fetch the Sub Category List */
		final AsyncCallback<String[]> callbackSubCategoryList = new AsyncCallback<String[]>() {

			@Override
			public void onFailure(Throwable caught) {
				System.err.println("Sub Category List addition error");
			}

			@Override
			public void onSuccess(String[] result) {
				listBoxSubCategory.clear();
				listBoxSubCategory.addItem("Choose");
				lblSubCategoryList.setVisible(true);
				listBoxSubCategory.setVisible(true);

				for (int i = 0; i < result.length; i++) {
					listBoxSubCategory.addItem(result[i]);
				}

			}

		};


		/* Fetch Category Header which stores info on the columns*/
		callbackCategoryColumns = new AsyncCallback<String[]>() {

			@Override
			public void onFailure(Throwable caught) {
				System.err.println("Error");
			}

			@Override
			public void onSuccess(String[] result) {
				clearColumnData();
				if (result[0] == null) {
					//listBoxSubCategory.setVisible(false);
					//lblSubCategoryList.setVisible(false);
					return;
				} else if (result[0].equalsIgnoreCase("subcategory")) {
					Ruvego.getResultsFetchAsync().fetchSubCategoryList(listBoxCategory.getItemText(listBoxCategory.getSelectedIndex()), callbackSubCategoryList);
					System.out.println("Client: Sub Category List added");
					return;
				} else {
					if (result[0].equalsIgnoreCase("type")) {
						listTypeOptions.setVisible(true);
						if (lblSubCategoryList.isVisible() == true) {
							Ruvego.getResultsFetchAsync().fetchTypeOptions(listBoxSubCategory.getItemText(listBoxSubCategory.getSelectedIndex()), "SubCategory", 
									callbackTypeOptions);
						} else {
							Ruvego.getResultsFetchAsync().fetchTypeOptions(listBoxCategory.getItemText(listBoxCategory.getSelectedIndex()), "Category", 
									callbackTypeOptions);
						}
					} else {
						textBox1.setVisible(true);
					}
					lblColumn1.setText(result[0]);
					lblColumn1.setVisible(true);
				}

				if (result[1] == null) {
					return;
				} else {
					lblColumn2.setText(result[1] + "");
					lblColumn2.setVisible(true);
					textBox2.setVisible(true);
				}

				if (result[2] == null) {
					return;
				} else {
					lblColumn3.setText(result[2] + "");
					lblColumn3.setVisible(true);
					textBox3.setVisible(true);
				}

			}


		};

		/* Submit Activity into Database */
		callbackActivitySubmit = new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				System.err.println("Error");
			}

			@Override
			public void onSuccess(Boolean result) {
				if (result == false) {
					throwError("An entry already exists with this name!");
					return;
				}
				resetTableData();
				throwError("Successfullly added the activity");
				System.out.println("Client: Successfullly added the activity into DB");
			}
		};
	}

	protected void resetTableData() {
		textAreaBrief.setText("");
		textAreaAddress.setText("");
		textBoxWebsite.setText("");
		textAreaParking.setText("");
		textBox1.setText("");
		textBox2.setText("");
		textBox3.setText("");
		textBoxEntryFee.setText("");
		textBoxName.setText("");
		textBoxRating.setText("");
		textBoxToll.setText("");
		chkBoxDaytime.setValue(false);
		chkBoxNightlife.setValue(false);
		formPanel.reset();
		textBoxName.setFocus(true);
		listBoxCategory.setItemSelected(0, true);
		textBoxContact.setText("");
		textAreaAddress.setText("");
		textAreaBrief.setText("");
		textAreaParking.setText("");

		listTimings.clear();
		listFromTime.setItemSelected(0, true);
		listToTime.setItemSelected(0, true);
		lblError.setVisible(false);
		
		clearColumnData();
		clearSubCategoryList();
	}

	protected void throwError(String error) {
		lblError.setText(error);
		lblError.setVisible(true);
	}

	protected void clearColumnData() {
		lblColumn1.setVisible(false);
		textBox1.setVisible(false);
		lblColumn2.setVisible(false);
		textBox2.setVisible(false);
		lblColumn3.setVisible(false);
		textBox3.setVisible(false);
		listTypeOptions.setVisible(false);
		lblError.setVisible(false);
	}

	protected void clearSubCategoryList() {
		lblSubCategoryList.setVisible(false);
		listBoxSubCategory.setVisible(false);
	}

	public static ContributeAddActivity getPage() {
		if (page == null) {
			page = new ContributeAddActivity();
		}
		return page;
	}

	public static void clearContent() {
		if (activityPanel != null) {
			activityPanel.setVisible(false);
		}
	}

	public void panelsView() {
		activityPanel.setVisible(true);
		Ruvego.getResultsFetchAsync().fetchCategoryList(callbackCategoryList);
		resetTableData();
	}

}
