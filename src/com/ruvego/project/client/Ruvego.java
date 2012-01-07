package com.ruvego.project.client;


import java.util.ArrayList;
import java.util.LinkedList;

import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.Maps;
import com.google.gwt.maps.client.control.LargeMapControl3D;
import com.google.gwt.maps.client.geocode.Geocoder;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
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

	/* Constants */
	final private static int FOOTER_FIXED_HEIGHT = 22;
	private static final int HEADER_PANEL_HEIGHT = 65;
	private static final int SECOND_HEADER_PANEL_HEIGHT = 30;

	/* Variables */
	static private int OTHER_WIDGET_TOP = HEADER_PANEL_HEIGHT + SECOND_HEADER_PANEL_HEIGHT;
	static private String place;
	static private String withinRange;
	static private int timeOfTheDay;
	
	static private int indent = 15;

	protected static RootPanel rootPanel = RootPanel.get();
	static private AbsolutePanel headerPanel = new AbsolutePanel();
	static protected AbsolutePanel secondHeaderPanel = new AbsolutePanel();
	protected static AbsolutePanel mapsPanel = new AbsolutePanel();
	protected static AbsolutePanel footerEncapPanel = new AbsolutePanel();
	static private HorizontalPanel timeOfTheactivityResultsPanel = new HorizontalPanel();

	static Image imgLogo;
	static private ListBox listBoxWithin;
	static private SuggestBox suggestBox;

	static private MapWidget map;
	static protected Geocoder geocoder;
	static private MultiWordSuggestOracle oracle;
	
	/* Pages objects */
	protected static RuvegoHomePage homePage = null;
	static private RuvegoContribute contributePage = null;

	static private ResultsFetchAsync resultsFetchService;

	/* Time of the Day Panel */
	static private CheckBox chkBoxDaytime, chkBoxNightlife;
	
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
		
		resultsFetchService = GWT.create(ResultsFetch.class);
		
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
	}

	private void userAuthenticated() {
		/* Setup all the basic Panels */
		setPlaces();
		setHeaderPanel();
		setSecondHeaderPanel();
		setFooterPanel();
		setMapsPanel();

		/* Google Maps 
		 * Should be initialized prio to using geocoding 
		 */
		Maps.loadMapsApi("ABQIAAAAAGd1OaKc5-LiujdmEUkjYhQlFGC39_UJYbUJQ_qzppf1NqVVqBTObk-i2YL_AX9I0dCr1pCekuVcpQ", "2", false, new Runnable() {
			public void run() {
				LatLng city = LatLng.newInstance(37.68889, -100.478611);
				map = new MapWidget(city, 4);
				map.zoomIn();
				//map.setInfoWindowEnabled(true);
				//map.addControl(new OverviewMapControl());
				//mapsPanel.add(map);
				map.addControl(new LargeMapControl3D());
				//map.addOverlay(new Marker(city));
				rootPanel.add(mapsPanel, indent, OTHER_WIDGET_TOP);
				map.setSize("100%", "100%");

				geocoder = new Geocoder();
				mapsPanel.add(map);
				/*
				DirectionsPanel directionsPanel = null;
				DirectionQueryOptions opts = new DirectionQueryOptions(map, 
						directionsPanel); 
						    String query = "from: 500 Memorial Dr, Cambridge, MA to: 4 Yawkey Way, Boston, MA"; 
						    Directions.load(query, null, new DirectionsCallback() { 
						      public void onFailure(int statusCode) { 
						        Window.alert("Failed to load directions: Status " 
						            + StatusCodes.getName(statusCode) + " " + statusCode); 
						      } 
						      public void onSuccess(DirectionResults result) { 
						    	  System.out.println("Distance : " + (String)result.);
						        GWT.log("Successfully loaded directions.", null); 
						      } 
						    }); 
				 */
			}
		});

		if (History.getToken().equalsIgnoreCase("contributePage")) {
			formContributePage();
			rootPanel.setStyleName("pageBackground");
		} else if (History.getToken().equalsIgnoreCase("homePage")) {
			rootPanel.setStyleName("pageBackground");
			formHomePage();
			History.newItem("homePage");
		} else {
			System.err.println("Error: Invalid token in the URL. Going to default page");
			rootPanel.setStyleName("pageBackground");
			formHomePage();
			History.newItem("homePage");
		}


		History.addValueChangeHandler(new ValueChangeHandler<String>() {
			public void onValueChange(ValueChangeEvent<String> event) {
				String historyToken = event.getValue();
				// Parse the history token
				try {
					if (historyToken.contains("contributePage")) {
						formContributePage();
						rootPanel.setStyleName("pageBackground");
					} else if (historyToken.contains("homePage")) {
						formHomePage();
						rootPanel.setStyleName("pageBackground");
					}

				} catch (IndexOutOfBoundsException e) {
					System.err.println("History token parse error");
				}
			}
		});

		Window.addResizeHandler(new ResizeHandler() {

			public void onResize(ResizeEvent event) {
				if (History.getToken().equalsIgnoreCase("contributePage")) {
					RuvegoContribute.ruvegoContributeAlignments();
				} else if (History.getToken().equalsIgnoreCase("homePage")) {
					ruvegoPanelAlignments();
				}
			}
		});
	}

	private void setPlaces() {
		final AsyncCallback<String[]> callbackPlaceList = new AsyncCallback<String[]>() {

			@Override
			public void onFailure(Throwable caught) {
				System.err.println("Error");
			}

			@Override
			public void onSuccess(String[] result) {
				LinkedList<String> suggestions = new LinkedList<String>();
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

	static protected void ruvegoPanelAlignments() {
		int width;

		width = getClientWidth();

		ruvegoPanelResize(width);

		rootPanel.setWidgetPosition(headerPanel, 0, 0);
		rootPanel.setWidgetPosition(secondHeaderPanel, 0, headerPanel.getOffsetHeight());
		secondHeaderPanel.setWidgetPosition(timeOfTheactivityResultsPanel, width - timeOfTheactivityResultsPanel.getOffsetWidth()/*290*/, 0);
		rootPanel.setWidgetPosition(mapsPanel, indent, OTHER_WIDGET_TOP);
		
		int mapsHeight = getClientHeight() - Ruvego.getOtherWidgetTop() - Ruvego.getFooterHeight();
		
		mapsPanel.setPixelSize(Ruvego.getClientWidth() - Ruvego.getIndent(), mapsHeight);
		rootPanel.setWidgetPosition(Ruvego.footerEncapPanel, 0, (getClientHeight() - Ruvego.getFooterHeight()));
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

	private void setSecondHeaderPanel() {
		rootPanel.add(secondHeaderPanel);
		secondHeaderPanel.setSize(Window.getClientWidth() + "px", SECOND_HEADER_PANEL_HEIGHT + "px");
		secondHeaderPanel.setStyleName("secondHeaderPanel");

		secondHeaderPanel.add(timeOfTheactivityResultsPanel, Window.getClientWidth() - 290, 0);		
		setTimeofthedayPanel();
	}


	private void setTimeofthedayPanel() {
		timeOfTheactivityResultsPanel.setSpacing(7);

		Label lblTimeOfTheDay = new Label("Plan your activity for : ");
		chkBoxDaytime = new CheckBox("Daytime");
		chkBoxDaytime.setValue(true);
		chkBoxNightlife = new CheckBox("Nightlife");

		lblTimeOfTheDay.setStyleName("chkBox");
		chkBoxDaytime.setStyleName("chkBox");
		chkBoxNightlife.setStyleName("chkBox");

		timeOfTheactivityResultsPanel.add(lblTimeOfTheDay);
		timeOfTheactivityResultsPanel.add(chkBoxDaytime);
		timeOfTheactivityResultsPanel.add(chkBoxNightlife);
		timeOfTheactivityResultsPanel.setHeight(SECOND_HEADER_PANEL_HEIGHT + "px");

	}

	protected void formContributePage() {
		if (contributePage == null) {
			contributePage = RuvegoContribute.getPage();
		} else {
			contributePage.clearContent();
			contributePage.panelsView();
		}
		if (homePage != null) {
			homePage.clearContent();	
		}
	}

	/** No Panels are made visible in this. This is used to just create the Home Page and align and not to show results */
	protected void formHomePage() {
		if (homePage == null) {
			homePage = RuvegoHomePage.getPage();
		} else {
			homePage.clearContent();
			homePage.ruvegoHomePagePanelAlignments();
		}
		
		if (contributePage != null) {
			contributePage.clearContent();
		}
	}

	private void setFooterPanel() {
		int footerContentSpacing = 18;
		footerEncapPanel.setStyleName("footerPanel");

		footerEncapPanel.setSize(Window.getClientWidth() + "px", FOOTER_FIXED_HEIGHT + "px");
		rootPanel.add(footerEncapPanel);//, 0, (Window.getClientHeight() - footerEncapPanel.getOffsetHeight() - FOOTER_FIXED_HEIGHT));

		Hyperlink contributePage = new Hyperlink("Contribute", "contributePage");
		Hyperlink about = new Hyperlink("About", "aboutPage");

		footerEncapPanel.add(contributePage, 15, 3);
		footerEncapPanel.add(about, contributePage.getAbsoluteLeft() + contributePage.getOffsetWidth() + footerContentSpacing, 3);

		contributePage.setStyleName("footerContent");
		about.setStyleName("footerContent");
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
		suggestBox.setSize("220px", "28px");

		Image searchBtn = new Image("Images/search.png");
		headerPanel.add(searchBtn, 570, 10);
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
		headerPanel.add(listBoxWithin, 370, 43);
		listBoxWithin.setHeight("18px");
		listBoxWithin.setVisibleItemCount(1);

		Label lblWithin = new Label("within ");
		lblWithin.setStyleName("withinLabel");
		headerPanel.add(lblWithin, 330, 45);

		final ArrayList<String> suggestions = new ArrayList<String>(); 

		suggestions.add("Sunnyvale, California"); 
		suggestions.add("Palo Alto, California");
		suggestions.add("Bay Area, California");
		suggestions.add("Cupertino, California");
		suggestions.add("San Jose, California");
		suggestions.add("San Francisco, California");

		oracle.addAll(suggestions);

		searchBtn.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				placeChoose();
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
				if (History.getToken().equalsIgnoreCase("contributePage")) {
					System.out.println("Set home page");
					RuvegoContribute.contributePanel.setVisible(false);
					History.newItem("homePage");
				} else {
					formHomePage();
				}
			}
		});
		
	}

	protected void placeChoose() {
		if (suggestBox.getText().equalsIgnoreCase("")) {
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
		
		System.out.println("Search request processing start");

		if (History.getToken().equalsIgnoreCase("contributePage")) {
			History.newItem("homePage");
			homePage.showResults();
		} else {
			homePage.showResults();
		}
	}

	public static void setMinimumPageHeight(int height) {
		MIN_PAGE_HEIGHT = height;
	}
}