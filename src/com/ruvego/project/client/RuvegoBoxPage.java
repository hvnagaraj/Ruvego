package com.ruvego.project.client;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.maps.client.geocode.DirectionQueryOptions;
import com.google.gwt.maps.client.geocode.DirectionResults;
import com.google.gwt.maps.client.geocode.Directions;
import com.google.gwt.maps.client.geocode.DirectionsCallback;
import com.google.gwt.maps.client.geocode.DirectionsPanel;
import com.google.gwt.maps.client.geocode.Geocoder;
import com.google.gwt.maps.client.geocode.LatLngCallback;
import com.google.gwt.maps.client.geocode.LocationCallback;
import com.google.gwt.maps.client.geocode.Placemark;
import com.google.gwt.maps.client.geocode.Route;
import com.google.gwt.maps.client.geocode.Waypoint;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.LatLngBounds;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.maps.client.overlay.Overlay;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.VerticalPanel;


public class RuvegoBoxPage {
	static private RuvegoBoxPage page;

	final static int SRC_DST_PANEL_HEIGHT = 30;
	final static int BOX_RESULTS_INDENT = 5;
	final static int BOX_RESULTS_SPACING = 5;
	final static int BOX_PANEL_WIDTH = 310;

	private static String SRC_ADDRESS = "";
	private static String DST_ADDRESS = "";
	
	private static String CLOSE_INFO = "";

	protected static ScrollPanel scrollPanel;

	protected static Grid grid;

	private static LatLngCallback mapsCallback;
	private static LatLngCallback mapsAddSrcCallback;
	private static LatLngCallback mapsAddDstCallback;

	private static int boxValueCount;

	private static Geocoder geocoder;

	private static DirectionsPanel directionsPanel = null;

	private static DirectionQueryOptions opts;

	private static DirectionsCallback directionsCallback;

	private static CheckBox srcDstSame;

	private static Waypoint[] waypoint;
	private static Waypoint[] waypointWithSrc;
	private static Waypoint[] waypointWithBoth;

	private static AbsolutePanel routeBriefPanel;

	private static CreateItinerary createItinerary;

	private class BoxResult {
		private AbsolutePanel boxResultPanel = new AbsolutePanel();
		private Label lblPosition = new Label();
		private HTML name;
		private Label address;
		private HTML routeInfo;
		private Image img;
		private Image imgClose;
		private ItineraryEntryMenu itineraryEntryMenu;
	}

	static protected BoxResult[] boxResult;

	private static HTML lblTotalDistance;
	private static HTML lblTotalDuration;

	private static BoxResultSrcDst srcBox;
	private static BoxResultSrcDst dstBox;

	private static Label btnRoute;

	private static HTML htmlBoxItineraryInfo1;
	private static HTML htmlBoxItineraryInfo2;
	private static HTML htmlBoxClickHere;

	private static LatLngBounds bounds = LatLngBounds.newInstance();
	
	private static PopupPanel confirmPanel; 

	private void setTotalDistDuration(String dist, String duration) {
		lblTotalDistance.setHTML("Total Distance : " + dist);
		lblTotalDuration.setHTML("Total Duration : " + duration);
		routeBriefPanel.setVisible(true);
		routeBriefPanel.setWidth((lblTotalDuration.getOffsetWidth() + 10) + "px");
	}

	public class GoogleMapsSuggestBox extends SuggestBox {
		public GoogleMapsSuggestBox() {
			super(new AddressOracle());
		}
	}

	class AddressOracle extends SuggestOracle {

		// this instance is needed, to call the getLocations-Service
		private final Geocoder geocoder;


		public AddressOracle() {
			geocoder = Ruvego.getGeocode();
		}

		@Override
		public void requestSuggestions(final Request request,
				final Callback callback) {
			// this is the string, the user has typed so far
			String addressQuery = request.getQuery();
			// look up for suggestions, only if at least 2 letters have been typed
			if (addressQuery.length() > 3) {    
				geocoder.getLocations(addressQuery, new LocationCallback() {

					@Override
					public void onFailure(int statusCode) {
						// do nothing
					}

					@Override
					public void onSuccess(JsArray<Placemark> places) {
						// create an oracle response from the places, found by the
						// getLocations-Service
						Collection<Suggestion> result = new LinkedList<Suggestion>();
						for (int i = 0; i < places.length(); i++) {
							String address = places.get(i).getAddress();
							AddressSuggestion newSuggestion = new AddressSuggestion(
									address);
							result.add((Suggestion) newSuggestion);
						}
						Response response = new Response(result);
						callback.onSuggestionsReady(request, response);
					}

				});

			} else {
				//	        	
				//	            Response response = new Response(
				//	                    Collections.<Suggestion> emptyList());
				//	          
				//	            callback.onSuggestionsReady(request, response);
			}

		}
	}

	class AddressSuggestion implements SuggestOracle.Suggestion, Serializable {

		private static final long serialVersionUID = 1L;

		String address;

		public AddressSuggestion(String address) {
			this.address = address;
		}

		@Override
		public String getDisplayString() {
			return this.address;
		}

		@Override
		public String getReplacementString() {
			return this.address;
		}
	}

	private class BoxResultSrcDst {
		private AbsolutePanel boxResultPanel = new AbsolutePanel();
		private Label lblPosition = new Label();
		private HTML name = new HTML();
		private HTML routeInfo = new HTML();
		private GoogleMapsSuggestBox suggestBoxAddress = new GoogleMapsSuggestBox();
		private Image img = new Image("Images/boxPosition.png");;
		private Button btnAdd = new Button("Add");
		private Button btnClear = new Button("Clear");
	}

	private static Overlay srcMarker;
	private static Overlay dstMarker;

	private static LatLng srcPoint;
	private static LatLng dstPoint;

	private RuvegoBoxPage() {
		System.out.println("Creating an object of type RuvegoBoxPage");
		scrollPanel = new ScrollPanel();

		RootPanel.get().add(scrollPanel, Ruvego.getIndent(), Ruvego.getOtherWidgetTop());
		scrollPanel.setStyleName("boxBG");
		scrollPanel.setPixelSize(BOX_PANEL_WIDTH, Window.getClientHeight() - Ruvego.getOtherWidgetTop() - Ruvego.getFooterHeight() - SRC_DST_PANEL_HEIGHT - 3);

		grid = new Grid(1, 1);
		scrollPanel.add(grid);
		grid.setWidth("100%");


		btnRoute = new Label("Route");
		btnRoute.setStyleName("btnRoute");
		Ruvego.getMapsPanel().add(btnRoute, Ruvego.getMapsPanel().getOffsetWidth() - 90, 12);

		btnRoute.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				mapRoute();
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

		mapsAddSrcCallback = new LatLngCallback() {

			@Override
			public void onFailure() {
				Ruvego.errorDisplay("Google Maps: Unable to find source address");
			}

			@Override
			public void onSuccess(LatLng point) {
				srcPoint = LatLng.newInstance(point.getLatitude(), point.getLongitude());
				Ruvego.errorDisplayClear();
				srcMarker = new Marker(point);
				Ruvego.getMapWidget().addOverlay(srcMarker);
				bounds.extend(point);
				Ruvego.getMapWidget().setCenter(bounds.getCenter(), Ruvego.getMapWidget().getBoundsZoomLevel(bounds));

				srcBox.lblPosition.setText("A");
				changePositions(65);
			}
		};

		mapsAddDstCallback = new LatLngCallback() {

			@Override
			public void onFailure() {
				Ruvego.errorDisplay("Google Maps: Unable to find destination address");
			}

			@Override
			public void onSuccess(LatLng point) {
				dstPoint = LatLng.newInstance(point.getLatitude(), point.getLongitude());
				Ruvego.errorDisplayClear();
				dstMarker = new Marker(point);
				Ruvego.getMapWidget().addOverlay(dstMarker);
				bounds.extend(point);
				Ruvego.getMapWidget().setCenter(bounds.getCenter(), Ruvego.getMapWidget().getBoundsZoomLevel(bounds));

				dstBox.lblPosition.setText(String.valueOf((char)(boxValueCount + 1 + 65)));
			}
		};



		mapsCallback = new LatLngCallback() {

			@Override
			public void onFailure() {
				Window.alert("cannot geocode");
			}

			@Override
			public void onSuccess(LatLng point) {
				Marker marker = new Marker(point);
				Ruvego.getMapWidget().addOverlay(marker);
				bounds.extend(point);
				Ruvego.getMapWidget().setCenter(bounds.getCenter(), Ruvego.getMapWidget().getBoundsZoomLevel(bounds));
			}
		};


		geocoder = Ruvego.getGeocode();
		opts = new DirectionQueryOptions(Ruvego.getMapWidget(), 
				directionsPanel); 

		directionsCallback = new DirectionsCallback() { 
			public void onFailure(int statusCode) { 
				Ruvego.errorDisplay("Google Maps: Route not found for 1 or more addresses in your list"); 
				routeBriefPanel.setVisible(false);
			} 

			public void onSuccess(DirectionResults result) {
				int startIndex, count;
				Iterator<Route> itr = result.getRoutes().iterator();
				Route route;

				if (!SRC_ADDRESS.equalsIgnoreCase("")) {
					startIndex = 0;
				} else {
					startIndex = 1;
				}

				count = startIndex;
				while (itr.hasNext()) {
					route = itr.next();
					if (count == boxValueCount) {

						break;
					}

					if (startIndex == 0) {
						boxResult[count].routeInfo.setHTML((char)(count + 65) + " to " + (char)(count + 65 + 1) + " : " + 
								route.getDistance().inLocalizedUnits() + " (" +
								route.getDuration().inLocalizedUnits() + ")");
					} else {
						boxResult[count].routeInfo.setHTML((char)(count + 64) + " to " + (char)(count + 64 + 1) + " : " + 
								route.getDistance().inLocalizedUnits() + " (" +
								route.getDuration().inLocalizedUnits() + ")");
					}
					boxResult[count].boxResultPanel.setHeight((boxResult[count].routeInfo.getAbsoluteTop() - boxResult[count].boxResultPanel.getAbsoluteTop()
							+ boxResult[count].routeInfo.getOffsetHeight() + 5) + "px");
					count++;
				}
				setTotalDistDuration(result.getDistance().inLocalizedUnits(), result.getDuration().inLocalizedUnits());
			} 
		};

		Window.addResizeHandler(new ResizeHandler() {

			@Override
			public void onResize(ResizeEvent event) {
				if (History.getToken().equalsIgnoreCase("boxView")) {
					panelResizeAlignments();
				}
			}
		});

		confirmPanel = new PopupPanel(true, true);
		confirmPanel.setWidth("150px");
		confirmPanel.setStyleName("confirmPopUpPanel");
		confirmPanel.setGlassEnabled(true);
		confirmPanel.setGlassStyleName("popUpPanel");

		
		FlexTable confirmContentPanel = new FlexTable();
		confirmContentPanel.setPixelSize(150, 70);
		
		Label lblConfirmText = new Label("Are you sure?");
		lblConfirmText.setStyleName("greyText");
		confirmContentPanel.setWidget(0, 0, lblConfirmText);
		
		Label btnYes = new Label("Yes");
		btnYes.setStyleName("btnConfirm");
		
		btnYes.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				moveEntriesUp(Integer.parseInt(CLOSE_INFO), boxValueCount - 1);
				boxResult[--boxValueCount].boxResultPanel.clear();
				boxResult[boxValueCount].boxResultPanel.removeFromParent();
				waypoint = new Waypoint[boxValueCount];
				for (int i = 0; i < boxValueCount; i++) {
					waypoint[i] = new Waypoint(boxResult[i].address.getText());
				}
				boxResult[boxValueCount] = null;

				confirmPanel.setVisible(false);
			}
		});
		
		Label btnNo = new Label("No");
		btnNo.setStyleName("btnConfirm");
		
		btnNo.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				confirmPanel.setVisible(false);
			}
		});
		
		confirmContentPanel.setWidget(1, 0, btnYes);
		confirmContentPanel.getFlexCellFormatter().setAlignment(1, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
		confirmContentPanel.getFlexCellFormatter().setAlignment(1, 1, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
		confirmContentPanel.setWidget(1, 1, btnNo);
		
		confirmContentPanel.getFlexCellFormatter().setColSpan(0, 0, 2);
		confirmPanel.add(confirmContentPanel);
		
		RootPanel.get().add(confirmPanel);
		confirmPanel.center();
		confirmPanel.setVisible(false);

		setupRouteBriefPanel();

	}


	private void setupRouteBriefPanel() {
		routeBriefPanel = new AbsolutePanel();
		routeBriefPanel.setStyleName("routeBriefPanel");
		routeBriefPanel.setPixelSize(150, 50);
		Ruvego.getMapsPanel().add(routeBriefPanel, 5, 5);

		lblTotalDistance = new HTML("Total Distance : ", false);
		lblTotalDistance.setStyleName("routeBriefText");
		routeBriefPanel.add(lblTotalDistance, 5, 5);

		lblTotalDuration = new HTML("Total Duration : ", false);
		lblTotalDuration.setStyleName("routeBriefText");
		routeBriefPanel.add(lblTotalDuration, 5, 25);

		routeBriefPanel.setHeight((lblTotalDuration.getAbsoluteTop() - routeBriefPanel.getAbsoluteTop() + lblTotalDuration.getOffsetHeight() + 5) + "px");

		routeBriefPanel.setVisible(false);
	}

	protected void mapRoute() {
		routeBriefPanel.setVisible(false);
		Ruvego.getMapWidget().clearOverlays();

		if (boxValueCount == 0) {
			Ruvego.errorDisplay("Google Maps: No activities in the Box");
			return;
		}

		if (boxValueCount == 1 && SRC_ADDRESS.equalsIgnoreCase("")) {
			Ruvego.errorDisplay("Google Maps: Routing requires atleast 1 activity and a source address");
			return;
		}

		if (!SRC_ADDRESS.equalsIgnoreCase("")) {
			waypointWithSrc = new Waypoint[boxValueCount + 1];
			for (int i = 0; i < boxValueCount; i++) {
				waypointWithSrc[i + 1] = waypoint[i];
			}

			waypointWithSrc[0] = new Waypoint(srcPoint);

			if (!DST_ADDRESS.equalsIgnoreCase("")) {
				waypointWithBoth = new Waypoint[boxValueCount + 2];
				waypointWithBoth[boxValueCount + 1] = new Waypoint(dstPoint);
				for (int i = 0; i < boxValueCount + 1; i++) {
					waypointWithBoth[i] = waypointWithSrc[i];
				}
				Ruvego.errorDisplayClear();
				Directions.loadFromWaypoints(waypointWithBoth, opts, directionsCallback);
			} else {
				Ruvego.errorDisplayClear();
				Directions.loadFromWaypoints(waypointWithSrc, opts, directionsCallback);
			}
			return;
		} else if (!DST_ADDRESS.equalsIgnoreCase("")) {
			Ruvego.errorDisplay("Route planning with just Destination address is not currently supported");
			routeBriefPanel.setVisible(false);
			return;
		} 

		Ruvego.errorDisplayClear();
		Directions.loadFromWaypoints(waypoint, opts, directionsCallback);

	}

	private void changePositions(int start) {
		for (int i = 0; i < boxValueCount; i++) {
			boxResult[i].lblPosition.setText(String.valueOf((char)(i + 1 + start)));
		}
	}

	public void fetchBoxResults() {
		Ruvego.setMapsPosition(BOX_PANEL_WIDTH, SRC_DST_PANEL_HEIGHT);
		Ruvego.getMapWidget().clearOverlays();
		String cookieItemCount = Cookies.getCookie("itemcount");

		/* Clear the grid before forming the list */
		grid.clear();

		if (cookieItemCount != null && Integer.parseInt(cookieItemCount) != 0) {
			boxValueCount = Integer.parseInt(cookieItemCount);

			System.out.println("No of items in the cookie : " + boxValueCount);
			if (boxValueCount > 25) {
				System.out.println("Box has more than 25 items");
				return;
			}

			grid.resizeRows(boxValueCount + 2);


			String entryDelims = "<;;>";
			String[] entry;
			String cookieItems = Cookies.getCookie("itemsdata");

			entry = cookieItems.split(entryDelims);

			waypoint = new Waypoint[boxValueCount];

			boxResult = new BoxResult[boxValueCount];

			setupSrcBoxPanel();

			for (int i = 0; i < boxValueCount; i++) {
				String fieldsDelims = "<;>";
				String[] fields;

				fields = entry[i].split(fieldsDelims);

				boxResult[i] = new BoxResult();
				boxResult[i].boxResultPanel.setSize("100%", 100 + "px");
				grid.setWidget(i + 1, 0, boxResult[i].boxResultPanel);

				if (i % 2 == 0) {
					boxResult[i].boxResultPanel.setStyleName("boxResultPanelEven");
				}

				boxResult[i].img = new Image("Images/boxPosition.png");
				boxResult[i].img.setPixelSize(35, 25);
				boxResult[i].boxResultPanel.add(boxResult[i].img, BOX_RESULTS_INDENT, 5);

				boxResult[i].lblPosition.setText(String.valueOf((char)(i + 1 + 64)));
				boxResult[i].lblPosition.setStyleName("whiteText");
				boxResult[i].lblPosition.setWidth("34px");
				boxResult[i].boxResultPanel.add(boxResult[i].lblPosition, BOX_RESULTS_INDENT, 8);

				boxResult[i].name = new HTML(fields[0]);
				boxResult[i].name.setStyleName("greyText");
				boxResult[i].boxResultPanel.add(boxResult[i].name, BOX_RESULTS_INDENT + boxResult[i].img.getOffsetWidth() + 5, 8);

				boxResult[i].address = new Label(fields[1], true);
				waypoint[i] = new Waypoint(boxResult[i].address.getText());
				geocoder.getLatLng(boxResult[i].address.getText(), mapsCallback);

				boxResult[i].address.setStyleName("boxValueAddressText");
				boxResult[i].address.setWidth((boxResult[i].boxResultPanel.getOffsetWidth() - BOX_RESULTS_INDENT * 2) + "px");
				boxResult[i].boxResultPanel.add(boxResult[i].address, BOX_RESULTS_INDENT, BOX_RESULTS_SPACING + boxResult[i].img.getOffsetHeight());

				boxResult[i].itineraryEntryMenu = new ItineraryEntryMenu(boxResult[i].boxResultPanel, 
						boxResult[i].address.getAbsoluteTop() - boxResult[i].boxResultPanel.getAbsoluteTop() + boxResult[i].address.getOffsetHeight() + 2,
						i);

				boxResult[i].routeInfo = new HTML("");
				boxResult[i].routeInfo.setStyleName("routeInfoText");
				boxResult[i].boxResultPanel.add(boxResult[i].routeInfo, 20, boxResult[i].itineraryEntryMenu.btnMore.getAbsoluteTop() - 
						boxResult[i].boxResultPanel.getAbsoluteTop() + boxResult[i].itineraryEntryMenu.btnMore.getOffsetHeight() + 8);
				boxResult[i].boxResultPanel.setHeight((boxResult[i].routeInfo.getAbsoluteTop() - boxResult[i].boxResultPanel.getAbsoluteTop()
						+ boxResult[i].routeInfo.getOffsetHeight() + 2) + "px");

				boxResult[i].imgClose = new Image("Images/closebutton.png");
				boxResult[i].boxResultPanel.add(boxResult[i].imgClose, boxResult[i].boxResultPanel.getOffsetWidth() - 27, 5);
				boxResult[i].imgClose.setPixelSize(18, 18);
				boxResult[i].imgClose.setStyleName("imgLogo");
				boxResult[i].imgClose.setLayoutData(i + "<;;>" + boxResult[i].address.getText());
				boxResult[i].imgClose.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						confirmPanel.setVisible(true);
						confirmPanel.show();
						confirmPanel.center();
						
						Ruvego.getMapWidget().clearOverlays();
						Image img = (Image) event.getSource();
						String fieldsDelims = "<;;>";
						String[] fields;

						fields = ((String) img.getLayoutData()).split(fieldsDelims);
						CLOSE_INFO = fields[0];
					}
				});
			}

			setupDstBoxPanel();
		}

		panelAlignments();
	}

	private void setupDstBoxPanel() {
		if (dstBox == null) {
			dstBox = new BoxResultSrcDst();

			grid.setWidget(boxValueCount + 1, 0, dstBox.boxResultPanel);
			dstBox.name.setHTML("End Point");

			dstBox.img.setPixelSize(35, 25);
			dstBox.boxResultPanel.add(dstBox.img, BOX_RESULTS_INDENT, 5);

			//		dstBox.lblPosition.setText(String.valueOf((char)(boxValueCount + 1 + 65)));
			dstBox.lblPosition.setText("");
			dstBox.lblPosition.setStyleName("whiteText");
			dstBox.lblPosition.setWidth("34px");
			dstBox.boxResultPanel.add(dstBox.lblPosition, BOX_RESULTS_INDENT, 8);


			dstBox.name.setStyleName("greyText");
			dstBox.boxResultPanel.add(dstBox.name, BOX_RESULTS_INDENT + dstBox.img.getOffsetWidth() + 5, 8);

			dstBox.boxResultPanel.add(dstBox.suggestBoxAddress, 5, dstBox.name.getAbsoluteTop() - dstBox.boxResultPanel.getAbsoluteTop() 
					+ dstBox.name.getOffsetHeight() + 7);
			dstBox.suggestBoxAddress.setPixelSize(BOX_PANEL_WIDTH - 120, 12);


			dstBox.routeInfo.setStyleName("routeInfoText");
			dstBox.boxResultPanel.add(dstBox.routeInfo, 20, dstBox.suggestBoxAddress.getAbsoluteTop() - dstBox.boxResultPanel.getAbsoluteTop() 
					+ dstBox.suggestBoxAddress.getOffsetHeight() + 2);

			dstBox.btnAdd.setPixelSize(40, 24);
			dstBox.btnAdd.setStyleName("boxBtnAdd");
			dstBox.boxResultPanel.add(dstBox.btnAdd, dstBox.suggestBoxAddress.getAbsoluteLeft() - dstBox.boxResultPanel.getAbsoluteLeft() + 
					dstBox.suggestBoxAddress.getOffsetWidth(), 
					dstBox.suggestBoxAddress.getAbsoluteTop() - dstBox.boxResultPanel.getAbsoluteTop());

			dstBox.btnClear.setHeight("24px");
			dstBox.btnClear.setStyleName("boxBtnAdd");
			dstBox.boxResultPanel.add(dstBox.btnClear, dstBox.btnAdd.getAbsoluteLeft() - dstBox.boxResultPanel.getAbsoluteLeft() + 
					dstBox.btnAdd.getOffsetWidth() + 1, 
					dstBox.suggestBoxAddress.getAbsoluteTop() - dstBox.boxResultPanel.getAbsoluteTop());


			dstBox.routeInfo.setHTML("");


			dstBox.btnAdd.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					if (dstBox.suggestBoxAddress.getText().equalsIgnoreCase("")) {
						Ruvego.errorDisplay("Destination address is empty");	
					}

					if (!DST_ADDRESS.equalsIgnoreCase(dstBox.suggestBoxAddress.getText())) {
						if (dstMarker != null) {
							Ruvego.getMapWidget().removeOverlay(dstMarker);
						}
						DST_ADDRESS = dstBox.suggestBoxAddress.getText();
						geocoder.getLatLng(DST_ADDRESS, mapsAddDstCallback);
					}
				}
			});

			dstBox.btnClear.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					if (!DST_ADDRESS.equalsIgnoreCase("")) {
						if (dstMarker != null) {
							Ruvego.getMapWidget().removeOverlay(dstMarker);
						}	
					}

					dstBox.suggestBoxAddress.setText("");
					DST_ADDRESS = "";
					Ruvego.errorDisplayClear();
					dstBox.lblPosition.setText("");
				}
			});

			if (boxValueCount % 2 == 1) {
				dstBox.boxResultPanel.setStyleName("boxResultPanelEven");
			}

			srcDstSame = new CheckBox("same as start point");
			srcDstSame.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

				@Override
				public void onValueChange(ValueChangeEvent<Boolean> event) {
					if (srcDstSame.getValue() == true) {
						dstBox.suggestBoxAddress.setText(SRC_ADDRESS);
					} else {
						dstBox.suggestBoxAddress.setText("");
					}
				}

			});

			dstBox.boxResultPanel.add(srcDstSame, dstBox.name.getAbsoluteLeft() - dstBox.boxResultPanel.getAbsoluteLeft() + dstBox.name.getOffsetWidth() + 5, 
					dstBox.name.getAbsoluteTop() - dstBox.boxResultPanel.getAbsoluteTop());
			srcDstSame.setStyleName("srcDstInfoText");

			if (boxValueCount % 2 == 0) {
				dstBox.boxResultPanel.setStyleName("boxResultPanelEven");
			}
		} else {
			grid.setWidget(boxValueCount + 1, 0, dstBox.boxResultPanel);
		}

		dstBox.boxResultPanel.setSize("100%", (dstBox.routeInfo.getAbsoluteTop() - dstBox.boxResultPanel.getAbsoluteTop() + 
				dstBox.routeInfo.getOffsetHeight() + 5) + "px");


	}

	private void setupSrcBoxPanel() {
		if (srcBox == null) {
			srcBox = new BoxResultSrcDst();

			grid.setWidget(0, 0, srcBox.boxResultPanel);

			srcBox.name.setHTML("Start Point");

			srcBox.img.setPixelSize(35, 25);
			srcBox.boxResultPanel.add(srcBox.img, BOX_RESULTS_INDENT, 5);

			//		srcBox.lblPosition.setText(String.valueOf((char)(boxValueCount + 1 + 65)));
			srcBox.lblPosition.setText("");
			srcBox.lblPosition.setStyleName("whiteText");
			srcBox.lblPosition.setWidth("34px");
			srcBox.boxResultPanel.add(srcBox.lblPosition, BOX_RESULTS_INDENT, 8);


			srcBox.name.setStyleName("greyText");
			srcBox.boxResultPanel.add(srcBox.name, BOX_RESULTS_INDENT + srcBox.img.getOffsetWidth() + 5, 8);

			srcBox.boxResultPanel.add(srcBox.suggestBoxAddress, 5, srcBox.name.getAbsoluteTop() - srcBox.boxResultPanel.getAbsoluteTop() 
					+ srcBox.name.getOffsetHeight() + 7);
			srcBox.suggestBoxAddress.setPixelSize(BOX_PANEL_WIDTH - 120, 12);


			srcBox.routeInfo.setStyleName("routeInfoText");
			srcBox.boxResultPanel.add(srcBox.routeInfo, 20, srcBox.suggestBoxAddress.getAbsoluteTop() - srcBox.boxResultPanel.getAbsoluteTop() 
					+ srcBox.suggestBoxAddress.getOffsetHeight() + 2);

			srcBox.btnAdd.setPixelSize(40, 24);
			srcBox.btnAdd.setStyleName("boxBtnAdd");
			srcBox.boxResultPanel.add(srcBox.btnAdd, srcBox.suggestBoxAddress.getAbsoluteLeft() - srcBox.boxResultPanel.getAbsoluteLeft() + 
					srcBox.suggestBoxAddress.getOffsetWidth(), 
					srcBox.suggestBoxAddress.getAbsoluteTop() - srcBox.boxResultPanel.getAbsoluteTop());

			srcBox.btnClear.setHeight("24px");
			srcBox.btnClear.setStyleName("boxBtnAdd");
			srcBox.boxResultPanel.add(srcBox.btnClear, srcBox.btnAdd.getAbsoluteLeft() - srcBox.boxResultPanel.getAbsoluteLeft() + 
					srcBox.btnAdd.getOffsetWidth() + 1, 
					srcBox.suggestBoxAddress.getAbsoluteTop() - srcBox.boxResultPanel.getAbsoluteTop());


			srcBox.routeInfo.setHTML("");


			srcBox.btnAdd.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					if (srcBox.suggestBoxAddress.getText().equalsIgnoreCase("")) {
						Ruvego.errorDisplay("Source address is empty");	
					}

					if (!SRC_ADDRESS.equalsIgnoreCase(srcBox.suggestBoxAddress.getText())) {
						if (srcMarker != null) {
							Ruvego.getMapWidget().removeOverlay(srcMarker);
						}
						SRC_ADDRESS = srcBox.suggestBoxAddress.getText();
						geocoder.getLatLng(SRC_ADDRESS, mapsAddSrcCallback);
					}
				}
			});

			srcBox.btnClear.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					if (!SRC_ADDRESS.equalsIgnoreCase("")) {
						if (srcMarker != null) {
							Ruvego.getMapWidget().removeOverlay(srcMarker);
						}	
					}

					srcBox.suggestBoxAddress.setText("");
					SRC_ADDRESS = "";
					Ruvego.errorDisplayClear();
					srcBox.lblPosition.setText("");
					changePositions(64);
				}
			});


			/*
		srcBox.routeInfo.setHTML((char)(count + 65) + " to " + (char)(count + 65 + 1) + " : " + 
				route.getDistance().inLocalizedUnits() + " (" +
				route.getDuration().inLocalizedUnits() + ")");
			 */
		} else {
			grid.setWidget(0, 0, srcBox.boxResultPanel);
		}
		
		srcBox.boxResultPanel.setSize("100%", (srcBox.routeInfo.getAbsoluteTop() - srcBox.boxResultPanel.getAbsoluteTop() + 
				srcBox.routeInfo.getOffsetHeight() + 5) + "px");
	}

	private void panelAlignments() {
		Ruvego.setMinimumPageHeight(RuvegoHomePage.HOMEPAGE_PAGE_HEIGHT);
		Ruvego.panelAlignments();
		panelResizeAlignments();
	}

	public static void panelResizeAlignments() {
		scrollPanel.setHeight((Ruvego.getClientHeight() - Ruvego.getOtherWidgetTop() - Ruvego.getFooterHeight() - SRC_DST_PANEL_HEIGHT - 3) + "px");
		Ruvego.getMapsPanel().setWidgetPosition(btnRoute, Ruvego.getMapsPanel().getOffsetWidth() - 80, Ruvego.getMapsPanel().getOffsetHeight() - 60);
		Ruvego.setMapsPosition(BOX_PANEL_WIDTH, SRC_DST_PANEL_HEIGHT);
		scrollPanel.setPixelSize(BOX_PANEL_WIDTH, Window.getClientHeight() - Ruvego.getOtherWidgetTop() - Ruvego.getFooterHeight() - SRC_DST_PANEL_HEIGHT - 3);
	}

	protected static void reorganizePositions(int currentPos, int newPos) {
		System.out.println("Current Position : " + currentPos + " New Position : " + newPos);

		if (newPos < 0) {
			Ruvego.errorDisplay("New position cannot be -ve");
			return;
		}

		if (newPos >= boxValueCount) {
			Ruvego.errorDisplay("There are only " + boxValueCount + " entries in the Box");
			return;
		}

		if (currentPos == newPos) {
			Ruvego.errorDisplay("New position same as previous position");
			return;			
		}

		System.out.println("Current Position : " + currentPos + " New Position : " + newPos);

		String tempName = boxResult[currentPos].name.getText();
		String tempAddress = boxResult[currentPos].address.getText();
		Waypoint tempWaypoint = waypoint[currentPos];

		if (currentPos < newPos) {
			moveEntriesUp(currentPos, newPos);
		} else {
			for (int i = currentPos; i > newPos; i--) {
				boxResult[i].name.setText(boxResult[i - 1].name.getText());
				boxResult[i].address.setText(boxResult[i - 1].address.getText());
				waypoint[i] = waypoint[i - 1];
				System.out.println("Moving from  " + (i - 1) + " to " + i);
			}
		}

		boxResult[newPos].name.setText(tempName);
		boxResult[newPos].address.setText(tempAddress);
		waypoint[newPos] = tempWaypoint;
	}


	private static void moveEntriesUp(int start, int end) {
		String entryDelims = "<;;>";
		String[] entry;

		for (int i = start; i < end; i++) {
			boxResult[i].name.setText(boxResult[i + 1].name.getText());
			boxResult[i].address.setText(boxResult[i + 1].address.getText());
			System.out.println("Moving from  " + (i + 1) + " to " + i);
			waypoint[i] = waypoint[i + 1];

			entry = ((String) boxResult[i + 1].imgClose.getLayoutData()).split(entryDelims);
			System.out.println("old data : " + boxResult[i].imgClose.getLayoutData());

			boxResult[i].imgClose.setLayoutData(i + "<;;>" + entry[1]);

			System.out.println("new data : " + boxResult[i].imgClose.getLayoutData()); 
		}
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
		routeBriefPanel.setVisible(false);
		btnRoute.setVisible(false);
		htmlBoxClickHere.setVisible(false);
		htmlBoxItineraryInfo1.setVisible(false);
		htmlBoxItineraryInfo2.setVisible(false);
		Ruvego.errorDisplayClear();
		Ruvego.setMapsPosition(0, 0);
	}

}
