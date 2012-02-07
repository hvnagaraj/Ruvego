package com.ruvego.project.client;


import java.util.ArrayList;
import com.ruvego.project.client.json.ExceptionJso;
import com.ruvego.project.client.json.UserJso;
import com.ruvego.project.client.json.UserRefJso;

import java.util.Date;
import java.util.LinkedList;

import javax.naming.directory.SearchControls;

import com.google.gwt.maps.client.GoogleBarOptions;
import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.MapOptions;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.Maps;
import com.google.gwt.maps.client.control.Control;
import com.google.gwt.maps.client.control.ControlAnchor;
import com.google.gwt.maps.client.control.ControlPosition;
import com.google.gwt.maps.client.control.LargeMapControl3D;
import com.google.gwt.maps.client.geocode.Geocoder;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.impl.ControlPositionImpl;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Ruvego implements EntryPoint {

	static private String PLACE = "Wrong";
	static private int WITHIN_MILES_INDEX = 5;
	static private int MIN_PAGE_HEIGHT = 0;
	static private int MAPS_POSITION_LEFT = 0;
	static private int MAPS_POSITION_BOTTOM = 0;
	static private final int BOX_INFO_WIDTH = 100;

	static private int boxCount = 0;

	/* Constants */
	final private static int FOOTER_FIXED_HEIGHT = 22;
	private static final int HEADER_PANEL_HEIGHT = 70;
	private static final int SECOND_HEADER_PANEL_HEIGHT = 30;

	/* Variables */
	static private int OTHER_WIDGET_TOP = HEADER_PANEL_HEIGHT + SECOND_HEADER_PANEL_HEIGHT;
	static private String place;
	static private String withinRange;
	static private int timeOfTheDay;

	static private int indent = 15;
	final private static long DURATION = 1000 * 60 * 60 * 24 * 14; //duration remembering login. 2 weeks in this example.
	final private static Date expires = new Date(System.currentTimeMillis() + DURATION);

	protected static RootPanel rootPanel = RootPanel.get();
	static protected AbsolutePanel headerPanel = new AbsolutePanel();
	static protected AbsolutePanel secondHeaderPanel = new AbsolutePanel();
	protected static AbsolutePanel mapsPanel = new AbsolutePanel();
	protected static AbsolutePanel footerEncapPanel = new AbsolutePanel();
	static private HorizontalPanel timeOfTheactivityResultsPanel = new HorizontalPanel();

	static private Image imgLogo;
	static protected Image imgBox;
	static private ListBox listBoxWithin;
	static private SuggestBox suggestBox;

	static private Label lblBoxCount;
	static private Label lblBoxText;
	static private Label lblBoxInfo;

	static private MapWidget map;
	static protected Geocoder geocoder;
	static private MultiWordSuggestOracle oracle;

	/* Pages objects */
	protected static RuvegoHomePage homePage = null;
	static private RuvegoContribute contributePage = null;
	static private RuvegoAboutPage aboutPage = null;
	static protected RuvegoBoxPage boxView = null;
	static protected ItineraryState itineraryState = null;
	static private ItineraryPage itineraryPage = null;

	static private ResultsFetchAsync resultsFetchService;
	static protected ResultsWriteAsync resultsWriteService;

	/* Time of the Day Panel */
	static private CheckBox chkBoxDaytime, chkBoxNightlife;

	/* Google Maps */
	private static ControlPosition zoomPosRight;
	private static ControlPosition zoomPosLeft;
	private static LargeMapControl3D zoomControls;

	/* Used to display errors at the center of the map */
	static protected HTML noContent = new HTML();

	static private LinkedList<String> suggestions;

	static private LoginModule loginModule;

	static private Application fbApp;

	static private Label lblWithin;

	/* Menu */
	private static BoxMenu moreMenu;

	static private HorizontalPanel itineraryNamePanel;

	static protected Label lblItineraryNameText;

	static private Timer timerBoxInfo;

	static private Timer timerErrorDisplay;

	private static DayActivityPlan dayActivityPlan;

	protected static InfoWindowContent infoWindow;

	protected static Label lblInfoWindow;

	public static void setItineraryText(String text) {
		lblItineraryNameText.setText(text);
		itineraryNamePanel.setVisible(true);
		itineraryNamePanelAlignments();
	}
	
	public static void clearItineraryText() {
		itineraryNamePanel.setVisible(false);
	}

	public static ResultsWriteAsync getResultsWriteService() {
		return Ruvego.resultsWriteService;
	}

	public static Label getBoxInfo() {
		return lblBoxInfo;
	}

	public static void errorDisplay(String msg) {
		noContent.setHTML(msg);
		noContent.setVisible(true);
		errorDisplayAlignments();
	}

	public static void errorDisplayWithTimer(String msg) {
		noContent.setHTML(msg);
		noContent.setVisible(true);
		errorDisplayAlignments();
		timerErrorDisplay.schedule(2000);
	}

	public static void errorDisplayClear() {
		noContent.setVisible(false);
	}

	public static void errorDisplayAlignments() {
		if (noContent.isAttached() == true) {
			mapsPanel.setWidgetPosition(noContent, (mapsPanel.getOffsetWidth() - noContent.getOffsetWidth())/2, 
					(mapsPanel.getOffsetHeight() - noContent.getOffsetHeight())/2);
		}
	}

	public static void mapControlsSetRight() {
		map.removeControl(zoomControls);
		map.addControl(zoomControls, zoomPosRight);		
	}

	public static void mapControlsSetLeft() {
		map.removeControl(zoomControls);
		map.addControl(zoomControls, zoomPosLeft);		
	}

	public static void insertItem(String data) {
		/* Only 25 entries supported */
		if (boxCount == 25) {
			boxInfo("Only 25 entries can be in the Box");
			return;
		}

		String cookieValue = Cookies.getCookie("itemsdata");
		if (cookieValue == null) {
			Cookies.setCookie("itemsdata", data, expires, null, "/", false);

			boxCount++;
			lblBoxCount.setText(String.valueOf(boxCount));
			Cookies.setCookie("itemcount", lblBoxCount.getText(), expires, null, "/", false);
			boxInfo("Entry added to Box");
			System.out.println("First Cookie entry added");
			return;
		} 

		boolean entryPresent = (cookieValue.toLowerCase().indexOf(data.toLowerCase()) >= 0);
		if (entryPresent == true) {
			boxInfo("Entry already in the box");
			System.out.println("Entry already present in the cookie");
			return;
		}

		String cookieCount = Cookies.getCookie("itemcount");

		boxCount = Integer.parseInt(cookieCount);
		boxCount++;

		lblBoxCount.setText(String.valueOf(boxCount));
		Cookies.setCookie("itemcount", lblBoxCount.getText(), expires, null, "/", false);

		/* <;;> between entries and <;> between fields of an entry */
		Cookies.setCookie("itemsdata", cookieValue + "<;;>" + data, expires, null, "/", false);
		boxInfo("Entry added to Box");
		System.out.println("Cookie entry added");
	}

	private static void boxInfo(String string) {		
		lblBoxInfo.setText(string);
		lblBoxInfo.setVisible(true);
		timerBoxInfo.schedule(2500);
	}

	public static void boxInfoClear() {
		lblBoxInfo.setVisible(false);
	}

	public static void deleteItem() {
		boxCount--;
		lblBoxCount.setText(String.valueOf(boxCount));
	}

	public static void setMapsPosition(int left, int bottom) {
		MAPS_POSITION_LEFT = left;
		MAPS_POSITION_BOTTOM = bottom;
	}

	public static int getIndent() {
		return indent;
	}

	public static AbsolutePanel getMapsPanel() {
		return mapsPanel;
	}

	public static MapWidget getMapWidget() {
		return map;
	}

	public static AbsolutePanel getFooterEncapPanel() {
		return footerEncapPanel;
	}

	public static AbsolutePanel getSecondHeaderPanel() {
		return secondHeaderPanel;
	}
	public static ResultsFetchAsync getResultsFetchAsync() {
		return resultsFetchService;
	}

	public static Geocoder getGeocode() {
		return Ruvego.geocoder;
	}

	public static String getPlace() {
		return PLACE;
	}

	public static String getWithinRange() {
		return Ruvego.withinRange;
	}

	public static int getOtherWidgetTop() {
		return Ruvego.OTHER_WIDGET_TOP;
	}

	public static int getTimeoftheday() {
		return Ruvego.timeOfTheDay;
	}

	public static int getFooterHeight() {
		return Ruvego.FOOTER_FIXED_HEIGHT;
	}

	public static RootPanel getRootPanel() {
		return Ruvego.rootPanel;
	}


	public void onModuleLoad() {
		rootPanel.setSize("100%", "100%");

		/* Initialize the result read service */
		resultsFetchService = GWT.create(ResultsFetch.class);

		/* Initialize the result write service */
		resultsWriteService = GWT.create(ResultsWrite.class);

		//TODO change before commit. Must be authenticateUser()
		//userAuthenticated();
		authenticateUser();
	}

	private void authenticateUser() {
		rootPanel.setStyleName("userAuthenticateBG");
		final DialogBox dialogbox = new DialogBox(false);
		dialogbox.setStyleName("userAuthenticationDialogBox");
		VerticalPanel dialogBoxContents = new VerticalPanel();

		Image img = new Image("Images/ruvegosmall.png");
		img.setSize("140px", "35px");
		img.setStyleName("imgLogo");
		dialogBoxContents.add(img);

		dialogbox.setModal(true);

		dialogbox.setText("Pre-Beta Testing. User Authentication Required");
		HTML message = new HTML("Enter Username");
		message.setStyleName("demo-DialogBox-message");
		message.setStyleName("userAuthenticationDialogBoxHTML");

		dialogBoxContents.add(message);

		final TextBox textBoxUsername = new TextBox();
		textBoxUsername.setWidth("200px");
		dialogBoxContents.add(textBoxUsername);

		HTML password = new HTML("Enter Password");
		dialogBoxContents.add(password);
		password.setStyleName("userAuthenticationDialogBoxHTML");

		final PasswordTextBox textBoxPassword = new PasswordTextBox();
		textBoxPassword.setWidth("200px");
		dialogBoxContents.add(textBoxPassword);

		final AsyncCallback<Boolean> callbackAuthenticate = new AsyncCallback<Boolean>() {

			@Override
			public void onSuccess(Boolean result) {
				if (result == true) {
					dialogbox.hide();
					userAuthenticated();
				} else {
					Window.alert("Invalid Username or Password. Access Denied");
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Server is busy. Please try again after some time");
			}
		};

		ClickHandler clickHandler = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				resultsFetchService.authenticateUser(textBoxUsername.getText(), textBoxPassword.getText(), callbackAuthenticate);
			}
		};

		Button button = new Button("Login", clickHandler);
		button.setStyleName("button");
		button.setPixelSize(70, 30);
		dialogBoxContents.add(button);
		dialogbox.setWidget(dialogBoxContents);
		dialogbox.center();
		dialogBoxContents.setSpacing(4);
		
		setMinimumPageHeight(650);
	}

	private void userAuthenticated() {
		rootPanel.setStyleName("pageBackground");

		/* Setup all the basic Panels */
		setPlaces();
		setHeaderPanel();
		setSecondHeaderPanel();
		setFooterPanel();
		setMapsPanel();
		setupItineraryState();


		/* Google Maps 
		 * Should be initialized prio to using geocoding 
		 */
		Maps.loadMapsApi("ABQIAAAAAGd1OaKc5-LiujdmEUkjYhQlFGC39_UJYbUJQ_qzppf1NqVVqBTObk-i2YL_AX9I0dCr1pCekuVcpQ", "2", false, new Runnable() {
			public void run() {
				LatLng city = LatLng.newInstance(37.68889, -100.478611);
				map = new MapWidget(city, 4);
				map.zoomIn();

				setupInfoWindow();
				zoomPosRight = new ControlPosition(ControlAnchor.TOP_RIGHT, 10, 10);
				zoomPosLeft = new ControlPosition(ControlAnchor.TOP_LEFT, 10, 10);
				zoomControls = new LargeMapControl3D();

				if (History.getToken().equalsIgnoreCase("boxView")) {
					mapControlsSetRight();
				} else {
					mapControlsSetLeft();					
				}

				rootPanel.add(mapsPanel, indent, OTHER_WIDGET_TOP);
				map.setSize("100%", "100%");

				geocoder = new Geocoder();
				mapsPanel.add(map);
				setupNoContent();
				setupFacebookModule();
				setupDayActvityPlan();

				/* This should be the last func to be called */
				managePageHistory();

			}
		});

		panelAlignments();

		Window.addResizeHandler(new ResizeHandler() {
			public void onResize(ResizeEvent event) {
				panelAlignments();
			}
		});

		timerBoxInfo = new Timer() {
			public void run() {
				boxInfoClear();
			}
		};

		timerErrorDisplay = new Timer() {
			public void run() {
				errorDisplayClear();
				History.newItem("homePage");
			}
		};



	}

	protected void setupInfoWindow() {
		lblInfoWindow = new Label();
		lblInfoWindow.setStyleName("greyText");
		lblInfoWindow.setWidth("100%");
		infoWindow = new InfoWindowContent(lblInfoWindow);
	}

	private void setupDayActvityPlan() {
		dayActivityPlan = DayActivityPlan.getPage();
	}

	private void setupItineraryState() {
		itineraryState = ItineraryState.getPage();
	}

	private void setupFacebookModule() {
		fbApp = new Application();
		fbApp.onModuleLoad();
	}

	private void setupLoginModule() {
		loginModule = LoginModule.getModule();
	}

	private void managePageHistory() {
		if (History.getToken().equalsIgnoreCase("contributePage")) {
			formContributePage();
		} else if (History.getToken().equalsIgnoreCase("homePage")) {
			formHomePage();
			History.newItem("homePage");
		} else if (History.getToken().equalsIgnoreCase("aboutPage")) {
			formAboutPage();
			History.newItem("aboutPage");
		} else if (History.getToken().equalsIgnoreCase("boxView")) {
			formBoxView();
			History.newItem("boxView");
		} else if (History.getToken().contains("itineraryPage")) {
			formItineraryPage();
			History.newItem(History.getToken());
		} else {
			System.err.println("Error: Invalid token in the URL. Going to default page");
			formHomePage();
			History.newItem("homePage");
		}

		History.addValueChangeHandler(new ValueChangeHandler<String>() {
			public void onValueChange(ValueChangeEvent<String> event) {
				String historyToken = event.getValue();
				try {
					if (historyToken.contains("contributePage")) {
						formContributePage();
					} else if (historyToken.contains("homePage")) {
						formHomePage();
					} else if (historyToken.contains("aboutPage")) {
						formAboutPage();
					} else if (historyToken.contains("boxView")) {
						formBoxView();
					} else if (historyToken.contains("itineraryPage")) {
						formItineraryPage();
					} else {
						clearOtherPages("homePage");
					}

				} catch (IndexOutOfBoundsException e) {
					System.err.println("History token parse error");
				}
			}
		});

	}

	protected void formItineraryPage() {
		clearOtherPages("itineraryPage");
		mapControlsSetRight();
		if (itineraryPage == null) {
			itineraryPage = ItineraryPage.getPage();
		} else {
			itineraryPage.panelsView();
		}
		itineraryPage.fetchResults();
	}

	private void setPlaces() {
		suggestions = new LinkedList<String>();

		final AsyncCallback<String[]> callbackPlaceList = new AsyncCallback<String[]>() {

			@Override
			public void onFailure(Throwable caught) {
				System.err.println("Error");
			}

			@Override
			public void onSuccess(String[] result) {
				suggestions.clear();
				oracle.clear();
				for(int i = 0; i < result.length; i++) {
					suggestions.add(result[i]);
				}
				oracle.addAll(suggestions);
			}

		};

		resultsFetchService.fetchPlaceList(callbackPlaceList);
	}

	private void setMapsPanel() {
		rootPanel.add(mapsPanel);	
	}

	protected void pageAlignments() {
	} 

	static protected void panelAlignments() {
		int width;

		width = getClientWidth();

		ruvegoPanelResize(width);

		rootPanel.setWidgetPosition(headerPanel, 0, 0);
		rootPanel.setWidgetPosition(secondHeaderPanel, 0, headerPanel.getOffsetHeight());

		mapsPanelAlignments();

		rootPanel.setWidgetPosition(footerEncapPanel, 0, (getClientHeight() - Ruvego.getFooterHeight()));
		headerPanel.setWidgetPosition(lblBoxCount, width - 57, headerPanel.getOffsetHeight() - 55);
		headerPanel.setWidgetPosition(imgBox, width - 68, headerPanel.getOffsetHeight() - 55);
		headerPanel.setWidgetPosition(lblBoxInfo, width - 185, 15);
		headerPanel.setWidgetPosition(lblBoxText, width - 57, headerPanel.getOffsetHeight() - 20);
		errorDisplayAlignments();
		LoginModule.panelALignments();
		itineraryNamePanelAlignments();
	}

	private static void mapsPanelAlignments() {
		int mapsHeight = getClientHeight() - Ruvego.getOtherWidgetTop() - Ruvego.getFooterHeight() - MAPS_POSITION_BOTTOM;

		rootPanel.setWidgetPosition(mapsPanel, indent + MAPS_POSITION_LEFT, OTHER_WIDGET_TOP);
		mapsPanel.setPixelSize(Ruvego.getClientWidth() - Ruvego.getIndent() - MAPS_POSITION_LEFT, mapsHeight);
	}

	private static void ruvegoPanelResize(int width) {
		headerPanel.setWidth(width + "px");
		footerEncapPanel.setPixelSize(width, FOOTER_FIXED_HEIGHT);
		secondHeaderPanel.setWidth(width + "px");
	}

	public static int getClientHeight() {
		if (Window.getClientHeight() > MIN_PAGE_HEIGHT) {
			return(Window.getClientHeight());
		} else {
			return(MIN_PAGE_HEIGHT);
		}
	}

	public static int getClientWidth() {
		if (Window.getClientWidth() > 1000) {
			return(Window.getClientWidth());
		} else {
			return(1000);
		}
	}

	private void setupNoContent() {
		/* If no content for a given place, given within Miles Range and given Time of the Day then display this message */
		mapsPanel.add(noContent, (mapsPanel.getOffsetWidth() - 300)/2, mapsPanel.getOffsetHeight()/2);
		noContent.setStyleName("noContent");
		noContent.setWidth("250px");
		noContent.setVisible(false);
	}

	private void setSecondHeaderPanel() {
		rootPanel.add(secondHeaderPanel);
		rootPanel.setWidgetPosition(secondHeaderPanel, 0, HEADER_PANEL_HEIGHT);
		secondHeaderPanel.setSize(Window.getClientWidth() + "px", SECOND_HEADER_PANEL_HEIGHT + "px");
		secondHeaderPanel.setStyleName("secondHeaderPanel");

		moreMenu = BoxMenu.getPage();

		itineraryNamePanel = new HorizontalPanel();
		Label lblItineraryName = new Label("Planning Itinerary : ");
		lblItineraryName.setStyleName("itineraryNormal");

		lblItineraryNameText = new Label("Los Angeles Trip");
		lblItineraryNameText.setStyleName("itineraryActive");

		lblItineraryNameText.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				History.newItem("itineraryPage/" + Ruvego.lblItineraryNameText.getText());
			}
		});

		itineraryNamePanel.add(lblItineraryName);
		itineraryNamePanel.add(lblItineraryNameText);

		secondHeaderPanel.add(itineraryNamePanel);

		itineraryNamePanelAlignments();
		
		itineraryNamePanel.setVisible(false);
	}


	public static void itineraryNamePanelAlignments() {
		secondHeaderPanel.setWidgetPosition(itineraryNamePanel, Ruvego.getClientWidth() - itineraryNamePanel.getOffsetWidth() - 5, 6);
	}

	private void setTimeofthedayPanel() {
		headerPanel.add(timeOfTheactivityResultsPanel, listBoxWithin.getAbsoluteLeft() + listBoxWithin.getOffsetWidth(), 
				suggestBox.getAbsoluteTop() + suggestBox.getOffsetHeight() - 8);		
		timeOfTheactivityResultsPanel.setSpacing(5);

		chkBoxDaytime = new CheckBox("Daytime");
		chkBoxDaytime.setValue(true);
		chkBoxNightlife = new CheckBox("Nightlife");

		chkBoxDaytime.setStyleName("chkBox");
		chkBoxNightlife.setStyleName("chkBox");

		timeOfTheactivityResultsPanel.add(chkBoxDaytime);
		timeOfTheactivityResultsPanel.add(chkBoxNightlife);
		//timeOfTheactivityResultsPanel.setHeight(SECOND_HEADER_PANEL_HEIGHT + "px");

	}

	protected void formAboutPage() {
		clearOtherPages("aboutPage");
		if (aboutPage == null) {
			aboutPage = RuvegoAboutPage.getPage();
		} else {
			aboutPage.panelsView();
		}
	}

	private void clearOtherPages(String currentPage) {
		errorDisplayClear();
		if (!currentPage.equalsIgnoreCase("homePage") && homePage != null) {
			homePage.clearContent();	
		}

		if (!currentPage.equalsIgnoreCase("contributePage") && contributePage != null) {
			contributePage.clearContent();
		}

		if (!currentPage.equalsIgnoreCase("aboutPage") && aboutPage != null) {
			aboutPage.clearContent();
		}

		if (!currentPage.equalsIgnoreCase("boxView") && boxView != null) {
			boxView.clearContent();
		}

		if (!currentPage.equalsIgnoreCase("itineraryPage") && itineraryPage != null) {
			itineraryPage.clearContent();
		}
	}


	protected void formContributePage() {
		clearOtherPages("contributePage");
		if (contributePage == null) {
			contributePage = RuvegoContribute.getPage();
		} else {
			contributePage.panelsView();
		}
	}

	/** No Panels are made visible in this. This is used to just create the Home Page and align and not to show results */
	protected void formHomePage() {
		clearOtherPages("homePage");
		mapControlsSetLeft();
		if (homePage == null) {
			homePage = RuvegoHomePage.getPage();
		} else {
			homePage.clearContent();
			homePage.panelAlignments();
		}
	}

	private void setFooterPanel() {
		int footerContentSpacing = 18;
		footerEncapPanel.setStyleName("footerPanel");

		footerEncapPanel.setSize(Window.getClientWidth() + "px", FOOTER_FIXED_HEIGHT + "px");
		rootPanel.add(footerEncapPanel);

		Hyperlink contributePage = new Hyperlink("Contribute", "contributePage");
		Hyperlink aboutPage = new Hyperlink("About", "aboutPage");

		footerEncapPanel.add(aboutPage, 15, 3);
		footerEncapPanel.add(contributePage, aboutPage.getAbsoluteLeft() + aboutPage.getOffsetWidth() + footerContentSpacing, 3);

		contributePage.setStyleName("footerContent");
		aboutPage.setStyleName("footerContent");
	}


	private void setHeaderPanel() {
		oracle = new MultiWordSuggestOracle();

		headerPanel.setStyleName("headerPanel");
		rootPanel.add(headerPanel);//, 0, 0);
		headerPanel.setSize("100%", HEADER_PANEL_HEIGHT + "px");

		imgLogo = new Image("Images/ruvegosmall.png");
		imgLogo.setSize("140px", "35px");
		imgLogo.setStyleName("imgLogo");
		headerPanel.add(imgLogo, 5, (headerPanel.getOffsetHeight() - 35)/2);

		suggestBox = new SuggestBox(oracle);
		suggestBox.setText(place);
		suggestBox.setStyleName("suggestBox");
		suggestBox.setFocus(true);
		headerPanel.add(suggestBox, 327, 10);
		suggestBox.setSize("250px", "28px");

		Image searchBtn = new Image("Images/search.png");
		headerPanel.add(searchBtn, suggestBox.getAbsoluteLeft() + suggestBox.getOffsetWidth() - 45, 10);
		searchBtn.setStyleName("searchBtn");

		listBoxWithin = new ListBox();
		listBoxWithin.setStyleName("listBoxWithin");
		listBoxWithin.addItem("150 miles");
		listBoxWithin.addItem("125 miles");
		listBoxWithin.addItem("100 miles");
		listBoxWithin.addItem("75 miles");
		listBoxWithin.addItem("50 miles");
		listBoxWithin.addItem("25 miles");
		listBoxWithin.addItem("10 miles");
		listBoxWithin.setSelectedIndex(WITHIN_MILES_INDEX);
		headerPanel.add(listBoxWithin, 372, 45);
		listBoxWithin.setHeight("30px");
		listBoxWithin.setVisibleItemCount(1);

		lblWithin = new Label("within ");
		lblWithin.setStyleName("withinLabel");
		headerPanel.add(lblWithin, 330, 47);

		searchBtn.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				placeChoose();
			}
		});

		imgBox = new Image("Images/shoppingcart.png");
		imgBox.setSize("45px", "35px");
		imgBox.setStyleName("imgLogo");
		headerPanel.add(imgBox, Window.getClientWidth() - 68, headerPanel.getOffsetHeight() - 55);

		imgBox.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				boxViewOnClick();
			}
		});

		lblBoxText = new Label("Box");
		lblBoxText.setStyleName("boxLogoText");
		lblBoxText.setWidth("30px");
		headerPanel.add(lblBoxText, Window.getClientWidth() - 57, headerPanel.getOffsetHeight() - 20);

		lblBoxText.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				boxViewOnClick();
			}
		});

		lblBoxCount = new Label("0");
		/* Cookie retrieval */
		String cookieValue = Cookies.getCookie("itemcount");
		if (cookieValue == null) {
			Cookies.setCookie("itemcount", lblBoxCount.getText(), expires, null, "/", false);
			lblBoxCount.setText("0");
			boxCount = 0;
		} else {
			boxCount = Integer.parseInt(Cookies.getCookie("itemcount"));
			lblBoxCount.setText(Cookies.getCookie("itemcount"));			
		}

		lblBoxCount.setStyleName("boxText");
		lblBoxCount.setWidth("30px");
		headerPanel.add(lblBoxCount, Window.getClientWidth() - 57, headerPanel.getOffsetHeight() - 55);
		lblBoxCount.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				boxViewOnClick();
			}
		});

		suggestBox.addKeyDownHandler(new KeyDownHandler() {
			private int count = 0;
			@Override
			public void onKeyDown(KeyDownEvent event) {
				count++;
				if (event.getNativeKeyCode() == 13 && count % 2 == 0) {
					placeChoose();
				}
			}
		});

		imgLogo.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (History.getToken().equalsIgnoreCase("homePage")) {
					formHomePage();
				} else {
					clearOtherPages(History.getToken());
					History.newItem("homePage");
				}
			}
		});

		setupLoginModule();

		lblBoxInfo = new Label("Error");
		lblBoxInfo.setWidth(BOX_INFO_WIDTH + "px");
		lblBoxInfo.setStyleName("boxInfo");
		lblBoxInfo.setVisible(false);
		headerPanel.add(lblBoxInfo, Window.getClientWidth() - 185, 15);

		setTimeofthedayPanel();
	}

	protected void boxViewOnClick() {
		if (boxCount == 0) {
			boxInfo("Box is Empty");
			return;
		}

		if (History.getToken().equalsIgnoreCase("boxView")) {
			formBoxView();
		} else {
			clearOtherPages(History.getToken());
			History.newItem("boxView");
		}
	}

	protected void formBoxView() {
		clearOtherPages("boxView");
		mapControlsSetRight();
		if (boxView == null) {
			boxView = RuvegoBoxPage.getPage();
		} else {
			boxView.panelsView();
		}
		boxView.fetchBoxResults();
	}

	protected void placeChoose() {
		if (suggestBox.getText().equalsIgnoreCase("")) {
			homePage.clearContent();
			errorDisplay("Enter a place name to search");
			return;
		}

		if (!suggestions.contains(suggestBox.getText())) {
			homePage.clearContent();
			errorDisplay("Either the Place is wrong or Ruvego currently doesn't have any activities for this place");
			return;
		}

		WITHIN_MILES_INDEX = listBoxWithin.getSelectedIndex();
		PLACE = suggestBox.getText();

		if (chkBoxDaytime.getValue() == true) {
			timeOfTheDay = timeOfTheDay | 1;
		} else {
			timeOfTheDay = timeOfTheDay & ~1;
		}

		if (chkBoxNightlife.getValue() == true) {
			timeOfTheDay = timeOfTheDay | 2;
		} else {
			timeOfTheDay = timeOfTheDay & ~2;
		}

		withinRange = Ruvego.listBoxWithin.getItemText(Ruvego.listBoxWithin.getSelectedIndex());

		if (History.getToken().equalsIgnoreCase("homePage")) {
			homePage.showResults();
		} else {
			History.newItem("homePage");
			homePage.showResults();
		}
	}

	public static void setMinimumPageHeight(int height) {
		MIN_PAGE_HEIGHT = height;
	}

	public static String[] parseString(String input, String delims) {
		return(input.split(delims));
	}

	public static void userLoggedOut() {
		clearItineraryText();
		
		Cookies.removeCookie("itinerary", "/");
	}



}