package com.ruvego.project.client;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.SuggestOracle.Callback;
import com.google.gwt.user.client.ui.SuggestOracle.Request;
import com.google.gwt.user.client.ui.SuggestOracle.Response;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.Widget;



public class DayActivityPlan {
	private static DayActivityPlan page;

	/* Constants */
	final static int JUST_SRC = 1;
	final static int JUST_DST = 2;
	final static int BOTH_SRC_DST = 3;

	int SRC_ADDRESS_PRESENT = 0;
	int DST_ADDRESS_PRESENT = 0;
	
	int DAY_INFO_PRESENT = 0;

	int SRC_PANEL = 0;
	int DST_PANEL = 0;

	final static int BOX_RESULTS_INDENT = 5;
	final static int BOX_RESULTS_SPACING = 5;
	final static int BOX_PANEL_WIDTH = 310;

	int TOTAL_COUNT_IN_PLAN = 0;

	private static Waypoint waypointSrc;
	private static Waypoint waypointDst;

	private LatLngCallback mapsAddSrcCallback;
	private LatLngCallback mapsAddDstCallback;

	private static Overlay srcMarker;
	private static Overlay dstMarker;

	private class BoxResultSrcDst {
		private AbsolutePanel boxResultPanel = new AbsolutePanel();
		private Label lblPosition = new Label();
		private HTML name = new HTML();
		private HTML routeInfo = new HTML();
		private GoogleMapsSuggestBox suggestBoxAddress = new GoogleMapsSuggestBox();
		private Image img = new Image("Images/boxPosition.png");;
		private Button btnAdd = new Button("Add");
		private Button btnClear = new Button("Clear");

		public void reSize() {
			boxResultPanel.setHeight((routeInfo.getAbsoluteTop() - boxResultPanel.getAbsoluteTop()
					+ routeInfo.getOffsetHeight() + 5) + "px");
		}

		public void setPosition() {
			if (!ItineraryCommon.DST_ADDRESS.equalsIgnoreCase("")) {
				lblPosition.setText(String.valueOf((char)(TOTAL_COUNT_IN_PLAN + SRC_ADDRESS_PRESENT + 65)));
			}
		}
	}

	private static BoxResultSrcDst srcBox = null;
	private static BoxResultSrcDst dstBox = null;

	private static CheckBox srcDstSame;


	/* Non-Static variables */
	protected Grid grid;

	private Waypoint[] waypointWithout;
	private Waypoint[] waypoint;

	protected BoxResult[] boxResult;

	private DirectionsCallback directionsCallback;

	private DirectionQueryOptions opts;

	/* Used while displaying errors belonging to a particular day in the itinerary */
	protected String dayName;
	
	private static ItineraryCommon itineraryCommon;


	private class BoxResult {
		private AbsolutePanel boxResultPanel = new AbsolutePanel();
		private Label lblPosition;
		private HTML name;
		private Label address;
		private HTML routeInfo;
		private Image img;
		private Image btnDel;
		private ItineraryEntryMenu itineraryEntryMenu;

		public void reSize() {
			boxResultPanel.setHeight((routeInfo.getAbsoluteTop() - boxResultPanel.getAbsoluteTop()
					+ routeInfo.getOffsetHeight() + 5) + "px");
		}

		public void setPosition(int i, int start) {
			lblPosition.setText(String.valueOf((char)(i + 1 + start)));
		}
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

	public static DayActivityPlan getPage() {
		if (page == null) {
			page = new DayActivityPlan();
		}
		return page;
	}

	public DayActivityPlan() {
		
		itineraryCommon = ItineraryCommon.getPage();
	}


	public DayActivityPlan(VerticalPanel panel) {
		grid = new Grid(0, 1);
		panel.add(grid);
		grid.setWidth("100%");

		opts = new DirectionQueryOptions(Ruvego.getMapWidget(), 
				ItineraryCommon.directionsPanel); 

		if (directionsCallback == null) {
			directionsCallback = new DirectionsCallback() { 
				public void onFailure(int statusCode) { 
					Ruvego.errorDisplay("Google Maps: Route not found for 1 or more addresses in your list"); 
					ItineraryCommon.routeBriefPanel.setVisible(false);
				}

				public void onSuccess(DirectionResults result) {
					int startIndex, count;
					Iterator<Route> itr = result.getRoutes().iterator();
					Route route;

					if (!ItineraryCommon.SRC_ADDRESS.equalsIgnoreCase("")) {
						startIndex = 0;
					} else {
						startIndex = 1;
					}

					count = startIndex;
					while (itr.hasNext()) {
						route = itr.next();
						if (count == TOTAL_COUNT_IN_PLAN) {
							dstBox.routeInfo.setHTML((char)(count - startIndex + 65) + " to " + (char)(count - startIndex + 65 + 1) + " : " + 
									route.getDistance().inLocalizedUnits() + " (" +
									route.getDuration().inLocalizedUnits() + ")");

							dstBox.reSize();
							break;
						}

						setRouteInfo(route.getDistance().inLocalizedUnits(), route.getDuration().inLocalizedUnits(), count, startIndex);

						count++;
					}

					ItineraryCommon.setTotalDistDuration(result.getDistance().inLocalizedUnits(), result.getDuration().inLocalizedUnits());
				} 
			};
		}





	}

	protected void setRouteInfo(String dist, String duration, int count, int startIndex) {
		if (startIndex == 0) {
			boxResult[0].routeInfo.setVisible(true);
		} else {
			boxResult[0].routeInfo.setVisible(false);
		}

		boxResult[count].routeInfo.setHTML((char)(count - startIndex + 65) + " to " + (char)(count - startIndex + 65 + 1) + " : " + 
				dist + " (" + duration + ")");

		boxResult[count].reSize();
	}

	public void addResults(String[] entry, int count) {
		TOTAL_COUNT_IN_PLAN = count;
		/* Clear the grid before forming the list */

		waypointWithout = new Waypoint[count];
		boxResult = new BoxResult[count];


		for (int i = 0; i < count; i++) {
			String fieldsDelims = "<;>";
			String[] fields;

			System.out.println("In loop of : " + i + " Total count : " + count);
			fields = entry[i].split(fieldsDelims);

			boxResult[i] = new BoxResult();
			boxResult[i].boxResultPanel.setSize("100%", 100 + "px");

			/* add i+1 th element */
			addToGrid(boxResult[i].boxResultPanel, i + SRC_PANEL + DAY_INFO_PRESENT);

			if (i % 2 == 0) {
				boxResult[i].boxResultPanel.setStyleName("boxResultPanelEven");
			}

			boxResult[i].img = new Image("Images/boxPosition.png");
			boxResult[i].img.setPixelSize(35, 25);
			boxResult[i].boxResultPanel.add(boxResult[i].img, BOX_RESULTS_INDENT, 5);

			boxResult[i].lblPosition = new Label();
			boxResult[i].lblPosition.setText(String.valueOf((char)(i + 1 + 64)));
			boxResult[i].lblPosition.setStyleName("whiteText");
			boxResult[i].lblPosition.setWidth("34px");
			boxResult[i].boxResultPanel.add(boxResult[i].lblPosition, BOX_RESULTS_INDENT, 8);

			boxResult[i].name = new HTML(fields[0]);
			boxResult[i].name.setStyleName("greyText");
			boxResult[i].boxResultPanel.add(boxResult[i].name, BOX_RESULTS_INDENT + boxResult[i].img.getOffsetWidth() + 5, 8);

			boxResult[i].address = new Label(fields[1], true);
			waypointWithout[i] = new Waypoint(boxResult[i].address.getText());
			Ruvego.getGeocode().getLatLng(boxResult[i].address.getText(), ItineraryCommon.mapsCallback);

			boxResult[i].address.setStyleName("boxValueAddressText");
			boxResult[i].address.setWidth((BOX_PANEL_WIDTH - BOX_RESULTS_INDENT * 2) + "px");
			boxResult[i].boxResultPanel.add(boxResult[i].address, BOX_RESULTS_INDENT, BOX_RESULTS_SPACING + boxResult[i].img.getOffsetHeight());

			boxResult[i].itineraryEntryMenu = new ItineraryEntryMenu(boxResult[i].boxResultPanel, 
					boxResult[i].address.getAbsoluteTop() - boxResult[i].boxResultPanel.getAbsoluteTop() + boxResult[i].address.getOffsetHeight() + 2,
					i, this);

			boxResult[i].routeInfo = new HTML("");
			boxResult[i].routeInfo.setStyleName("routeInfoText");
			boxResult[i].boxResultPanel.add(boxResult[i].routeInfo, 20, boxResult[i].itineraryEntryMenu.btnMore.getAbsoluteTop() - 
					boxResult[i].boxResultPanel.getAbsoluteTop() + boxResult[i].itineraryEntryMenu.btnMore.getOffsetHeight() + 8);
			boxResult[i].boxResultPanel.setHeight((boxResult[i].routeInfo.getAbsoluteTop() - boxResult[i].boxResultPanel.getAbsoluteTop()
					+ boxResult[i].routeInfo.getOffsetHeight() + 2) + "px");

			boxResult[i].btnDel = new Image("Images/closebutton.png");
			boxResult[i].boxResultPanel.add(boxResult[i].btnDel, boxResult[i].boxResultPanel.getOffsetWidth() - 27, 5);
			boxResult[i].btnDel.setPixelSize(18, 18);
			boxResult[i].btnDel.setStyleName("imgLogo");
			
			boxResult[i].btnDel.setLayoutData(i);
			boxResult[i].btnDel.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					Image img = (Image) event.getSource();
					ItineraryState.setEntry(DayActivityPlan.this, (Integer)img.getLayoutData());
			//		BoxResult.this.address.setText("fdsad");
					Ruvego.getMapWidget().clearOverlays();
					ItineraryCommon.showConfirmPanel();
				}
			});
		}
	}

	private void moveEntriesUp(int start, int end) {
		for (int i = start; i < end; i++) {
			boxResult[i].name.setText(boxResult[i + 1].name.getText());
			boxResult[i].address.setText(boxResult[i + 1].address.getText());
			System.out.println("Moving from  " + (i + 1) + " to " + i);
			waypointWithout[i] = waypointWithout[i + 1];

			boxResult[i].routeInfo.setText("");

			boxResult[i].reSize();
		}
	}


	public void deleteEntry() {	
		System.out.println("Deleting " + ItineraryState.getEntry().dayName + " Entry " + ItineraryState.getEntryNum());
		moveEntriesUp(ItineraryState.getEntryNum(), TOTAL_COUNT_IN_PLAN - 1);
		boxResult[--TOTAL_COUNT_IN_PLAN].boxResultPanel.clear();
		boxResult[TOTAL_COUNT_IN_PLAN].boxResultPanel.removeFromParent();
		waypointWithout = new Waypoint[TOTAL_COUNT_IN_PLAN];
		for (int i = 0; i < TOTAL_COUNT_IN_PLAN; i++) {
			waypointWithout[i] = new Waypoint(boxResult[i].address.getText());
		}
		boxResult[TOTAL_COUNT_IN_PLAN] = null;
	}

	protected void reorganizePositions(int currentPos, int newPos) {
		System.out.println("Current Position : " + currentPos + " New Position : " + newPos);
		System.out.println("Total entries : " + TOTAL_COUNT_IN_PLAN);

		if (newPos < 0) {
			Ruvego.errorDisplay("New position cannot be -ve");
			return;
		}

		if (newPos >= TOTAL_COUNT_IN_PLAN) {
			Ruvego.errorDisplay("There are " + TOTAL_COUNT_IN_PLAN + " entries in " + dayName);
			return;
		}

		if (currentPos == newPos) {
			Ruvego.errorDisplay("New position same as previous position");
			return;			
		}

		String tempName = boxResult[currentPos].name.getText();
		String tempAddress = boxResult[currentPos].address.getText();
		Waypoint tempWaypoint = waypointWithout[currentPos];

		if (currentPos < newPos) {
			moveEntriesUp(currentPos, newPos);
		} else {
			for (int i = currentPos; i > newPos; i--) {
				boxResult[i].name.setText(boxResult[i - 1].name.getText());
				boxResult[i].address.setText(boxResult[i - 1].address.getText());
				waypointWithout[i] = waypointWithout[i - 1];
				System.out.println("Moving from  " + (i - 1) + " to " + i);
			}
		}

		boxResult[newPos].name.setText(tempName);
		boxResult[newPos].address.setText(tempAddress);
		waypointWithout[newPos] = tempWaypoint;
	}

	public void addToGrid(Widget boxResultPanel, int position) {
		System.out.println("Added position : " + position + " Row count : " + grid.getRowCount());
		grid.resizeRows(grid.getRowCount() + 1);
		grid.setWidget(position, 0, boxResultPanel);
	}

	protected void mapRoute() {
		ItineraryCommon.routeBriefPanel.setVisible(false);
		Ruvego.getMapWidget().clearOverlays();

		if (TOTAL_COUNT_IN_PLAN == 0) {
			Ruvego.errorDisplay("Google Maps: No activities in the Box");
			return;
		}

		if (TOTAL_COUNT_IN_PLAN == 1 && ItineraryCommon.SRC_ADDRESS.equalsIgnoreCase("")) {
			Ruvego.errorDisplay("Google Maps: Routing requires atleast 1 activity and a source address");
			return;
		}

		if (this.SRC_ADDRESS_PRESENT == 1) {
			if (this.DST_ADDRESS_PRESENT == 1) {
				Ruvego.errorDisplayClear();
				prepareWaypoints(BOTH_SRC_DST);
				Directions.loadFromWaypoints(waypoint, opts, directionsCallback);
			} else {
				Ruvego.errorDisplayClear();
				prepareWaypoints(JUST_SRC);
				Directions.loadFromWaypoints(waypoint, opts, directionsCallback);
			}
			return;
		} else if (!ItineraryCommon.DST_ADDRESS.equalsIgnoreCase("")) {
			Ruvego.errorDisplayClear();
			prepareWaypoints(JUST_DST);
			Directions.loadFromWaypoints(waypoint, opts, directionsCallback);
			return;
		} 

		Ruvego.errorDisplayClear();
		Directions.loadFromWaypoints(waypointWithout, opts, directionsCallback);
	}


	private void prepareWaypoints(int type) {
		switch (type) {
		case JUST_SRC:
			waypoint = new Waypoint[TOTAL_COUNT_IN_PLAN + 1];
			waypoint[0] = waypointSrc;
			for (int i = 0; i < TOTAL_COUNT_IN_PLAN; i++) {
				waypoint[i + 1] = waypointWithout[i];
			}
			break;

		case JUST_DST:
			waypoint = new Waypoint[TOTAL_COUNT_IN_PLAN + 1];
			for (int i = 0; i < TOTAL_COUNT_IN_PLAN; i++) {
				waypoint[i] = waypointWithout[i];
			}
			waypoint[TOTAL_COUNT_IN_PLAN] = waypointDst;
			break;

		case BOTH_SRC_DST:
			waypoint = new Waypoint[TOTAL_COUNT_IN_PLAN + 2];
			waypoint[0] = waypointSrc;
			for (int i = 0; i < TOTAL_COUNT_IN_PLAN; i++) {
				waypoint[i + 1] = waypointWithout[i];
			}
			waypoint[TOTAL_COUNT_IN_PLAN + 1] = waypointDst;
			break;

		default:
			break;
		}
	}

	public void addWaypoint(boolean isSrc) {
		if (isSrc == true) {
			waypointSrc = new Waypoint(ItineraryCommon.srcPoint);
		} else {
			waypointDst = new Waypoint(ItineraryCommon.dstPoint);
		}
	}


	protected void setupDstBoxPanel() {
		System.out.println("Total count : " + TOTAL_COUNT_IN_PLAN);
		if (dstBox == null) {
			dstBox = new BoxResultSrcDst();

			addToGrid(dstBox.boxResultPanel, TOTAL_COUNT_IN_PLAN + SRC_PANEL + DAY_INFO_PRESENT);

			dstBox.name.setHTML("End Point");

			dstBox.img.setPixelSize(35, 25);
			dstBox.boxResultPanel.add(dstBox.img, DayActivityPlan.BOX_RESULTS_INDENT, 5);

			dstBox.lblPosition.setText("");
			dstBox.lblPosition.setStyleName("whiteText");
			dstBox.lblPosition.setWidth("34px");
			dstBox.boxResultPanel.add(dstBox.lblPosition, DayActivityPlan.BOX_RESULTS_INDENT, 8);


			dstBox.name.setStyleName("greyText");
			dstBox.boxResultPanel.add(dstBox.name, DayActivityPlan.BOX_RESULTS_INDENT + dstBox.img.getOffsetWidth() + 5, 8);

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

					if (!ItineraryCommon.DST_ADDRESS.equalsIgnoreCase(dstBox.suggestBoxAddress.getText())) {
						if (dstMarker != null) {
							Ruvego.getMapWidget().removeOverlay(dstMarker);
						}
						ItineraryCommon.DST_ADDRESS = dstBox.suggestBoxAddress.getText();
						Ruvego.getGeocode().getLatLng(ItineraryCommon.DST_ADDRESS, mapsAddDstCallback);
					}
				}
			});

			dstBox.btnClear.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					if (!ItineraryCommon.DST_ADDRESS.equalsIgnoreCase("")) {
						if (dstMarker != null) {
							Ruvego.getMapWidget().removeOverlay(dstMarker);
						}	
					}

					dstBox.suggestBoxAddress.setText("");
					ItineraryCommon.DST_ADDRESS = "";
					Ruvego.errorDisplayClear();
					dstBox.lblPosition.setText("");
					dstBox.routeInfo.setText("");
					dstBox.reSize();
					DST_ADDRESS_PRESENT = 0;
				}
			});

			if (TOTAL_COUNT_IN_PLAN % 2 == 1) {
				dstBox.boxResultPanel.setStyleName("boxResultPanelEven");
			}

			srcDstSame = new CheckBox("same as start point");
			srcDstSame.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

				@Override
				public void onValueChange(ValueChangeEvent<Boolean> event) {
					if (srcDstSame.getValue() == true) {
						dstBox.suggestBoxAddress.setText(ItineraryCommon.SRC_ADDRESS);
					} else {
						dstBox.suggestBoxAddress.setText("");
					}
				}

			});

			dstBox.boxResultPanel.add(srcDstSame, dstBox.name.getAbsoluteLeft() - dstBox.boxResultPanel.getAbsoluteLeft() + dstBox.name.getOffsetWidth() + 5, 
					dstBox.name.getAbsoluteTop() - dstBox.boxResultPanel.getAbsoluteTop());
			srcDstSame.setStyleName("srcDstInfoText");

			if (TOTAL_COUNT_IN_PLAN % 2 == 0) {
				dstBox.boxResultPanel.setStyleName("boxResultPanelEven");
			}


			mapsAddDstCallback = new LatLngCallback() {

				@Override
				public void onFailure() {
					Ruvego.errorDisplay("Google Maps: Unable to find destination address");
				}

				@Override
				public void onSuccess(LatLng point) {
					ItineraryCommon.dstPoint = LatLng.newInstance(point.getLatitude(), point.getLongitude());
					Ruvego.errorDisplayClear();
					dstMarker = new Marker(point);
					Ruvego.getMapWidget().addOverlay(dstMarker);
					ItineraryCommon.bounds.extend(point);
					Ruvego.getMapWidget().setCenter(ItineraryCommon.bounds.getCenter(), Ruvego.getMapWidget().getBoundsZoomLevel(ItineraryCommon.bounds));


					dstBox.setPosition();
					DST_ADDRESS_PRESENT = 1;

					addWaypoint(false);
				}
			};


		} else {
			addToGrid(dstBox.boxResultPanel, TOTAL_COUNT_IN_PLAN + SRC_PANEL + DAY_INFO_PRESENT);
		}

		DST_PANEL = 1;
		dstBox.boxResultPanel.setSize("100%", (dstBox.routeInfo.getAbsoluteTop() - dstBox.boxResultPanel.getAbsoluteTop() + 
				dstBox.routeInfo.getOffsetHeight() + 5) + "px");


	}

	public void setupSrcBoxPanel() {
		if (srcBox == null) {
			srcBox = new BoxResultSrcDst();

			addToGrid(srcBox.boxResultPanel, DAY_INFO_PRESENT);

			srcBox.name.setHTML("Start Point");

			srcBox.img.setPixelSize(35, 25);
			srcBox.boxResultPanel.add(srcBox.img, DayActivityPlan.BOX_RESULTS_INDENT, 5);

			srcBox.lblPosition.setText("");
			srcBox.lblPosition.setStyleName("whiteText");
			srcBox.lblPosition.setWidth("34px");
			srcBox.boxResultPanel.add(srcBox.lblPosition, DayActivityPlan.BOX_RESULTS_INDENT, 8);


			srcBox.name.setStyleName("greyText");
			srcBox.boxResultPanel.add(srcBox.name, DayActivityPlan.BOX_RESULTS_INDENT + srcBox.img.getOffsetWidth() + 5, 8);

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

					if (!ItineraryCommon.SRC_ADDRESS.equalsIgnoreCase(srcBox.suggestBoxAddress.getText())) {
						if (srcMarker != null) {
							Ruvego.getMapWidget().removeOverlay(srcMarker);
						}
						ItineraryCommon.SRC_ADDRESS = srcBox.suggestBoxAddress.getText();
						Ruvego.getGeocode().getLatLng(ItineraryCommon.SRC_ADDRESS, mapsAddSrcCallback);
					}
				}
			});

			srcBox.btnClear.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					if (!ItineraryCommon.SRC_ADDRESS.equalsIgnoreCase("")) {
						if (srcMarker != null) {
							Ruvego.getMapWidget().removeOverlay(srcMarker);
						}	
					}

					srcBox.suggestBoxAddress.setText("");
					ItineraryCommon.SRC_ADDRESS = "";
					Ruvego.errorDisplayClear();
					srcBox.lblPosition.setText("");

					SRC_ADDRESS_PRESENT = 0;

					reAssignLabels(64);
				}
			});

			mapsAddSrcCallback = new LatLngCallback() {

				@Override
				public void onFailure() {
					Ruvego.errorDisplay("Google Maps: Unable to find source address");
				}

				@Override
				public void onSuccess(LatLng point) {
					ItineraryCommon.srcPoint = LatLng.newInstance(point.getLatitude(), point.getLongitude());
					Ruvego.errorDisplayClear();
					srcMarker = new Marker(point);
					Ruvego.getMapWidget().addOverlay(srcMarker);
					ItineraryCommon.bounds.extend(point);
					Ruvego.getMapWidget().setCenter(ItineraryCommon.bounds.getCenter(), Ruvego.getMapWidget().getBoundsZoomLevel(ItineraryCommon.bounds));

					addWaypoint(true);

					srcBox.lblPosition.setText("A");
					SRC_ADDRESS_PRESENT = 1;
					reAssignLabels(65);
				}

			};

		} else {
			addToGrid(srcBox.boxResultPanel, DAY_INFO_PRESENT);
		}

		SRC_PANEL = 1;
		srcBox.boxResultPanel.setSize("100%", (srcBox.routeInfo.getAbsoluteTop() - srcBox.boxResultPanel.getAbsoluteTop() + 
				srcBox.routeInfo.getOffsetHeight() + 5) + "px");
	}

	private void reAssignLabels(int start) {
		for (int i = 0; i < TOTAL_COUNT_IN_PLAN; i++) {
			boxResult[i].setPosition(i, start);
		}

		if (DST_ADDRESS_PRESENT == 1) {
			dstBox.setPosition();
		}
	}
	
	public void addDayDatePanel(int day, String date) {
		Label lblDayOneInfo = new Label("Day " + day + "  [ " + date + " ]");
		lblDayOneInfo.setStyleName("lblItineraryDayInfo");
		lblDayOneInfo.setWidth("100%");
		addToGrid(lblDayOneInfo, 0);
		DAY_INFO_PRESENT = 1;
	}

}
