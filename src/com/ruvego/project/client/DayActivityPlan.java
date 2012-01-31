package com.ruvego.project.client;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

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

	private LinkedList<Waypoint> waypointLL;

	protected BoxResult boxResult;

	protected LinkedList<BoxResult> boxResultLL;

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

		/* Linked List initializations */
		boxResultLL = new LinkedList<DayActivityPlan.BoxResult>();
		waypointLL = new LinkedList<Waypoint>();

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

					count = 0;
					while (itr.hasNext()) {
						route = itr.next();
						if (count == TOTAL_COUNT_IN_PLAN) {
							dstBox.routeInfo.setHTML((char)(count - startIndex + 65) + " to " + (char)(count - startIndex + 65 + 1) + " : " + 
									route.getDistance().inLocalizedUnits() + " (" +
									route.getDuration().inLocalizedUnits() + ")");

							dstBox.reSize();
							break;
						}

						setRouteInfo(route.getDistance().inLocalizedUnits(), route.getDuration().inLocalizedUnits(), count);

						count++;
					}

					ItineraryCommon.setTotalDistDuration(result.getDistance().inLocalizedUnits(), result.getDuration().inLocalizedUnits());
				} 
			};
		}

	}

	protected void setRouteInfo(String dist, String duration, int count) {
		System.out.println("Setting route info for : " + count);
		if (!ItineraryCommon.SRC_ADDRESS.equalsIgnoreCase("")) {
			boxResultLL.get(count).routeInfo.setVisible(true);
			boxResultLL.get(count).routeInfo.setHTML((char)(count + 'A') + " to " + (char)(count + 'A' + 1) + " : " + 
					dist + " (" + duration + ")");
			boxResultLL.get(count).reSize();
		} else {
			boxResultLL.get(count + 1).routeInfo.setVisible(true);
			boxResultLL.get(count + 1).routeInfo.setHTML((char)(count + 'A') + " to " + (char)(count +'A' + 1) + " : " + 
					dist + " (" + duration + ")");
			boxResultLL.get(count + 1).reSize();
			boxResultLL.get(0).routeInfo.setVisible(false);
		}
	}

	public void addResults(String[] entry, int count) {
		TOTAL_COUNT_IN_PLAN = count;
		/* Clear the grid before forming the list */

		waypointWithout = new Waypoint[count];
		//boxResult = new BoxResult[count];


		for (int i = 0; i < count; i++) {
			String fieldsDelims = "<;>";
			String[] fields;

			System.out.println("In loop of : " + i + " Total count : " + count);
			fields = entry[i].split(fieldsDelims);

			boxResult = new BoxResult();

			boxResult.boxResultPanel.setSize("100%", 100 + "px");

			/* add i+1 th element */
			addToGrid(boxResult.boxResultPanel, i + SRC_PANEL + DAY_INFO_PRESENT);

			if (i % 2 == 0) {
				boxResult.boxResultPanel.setStyleName("boxResultPanelEven");
			}

			boxResult.img = new Image("Images/boxPosition.png");
			boxResult.img.setPixelSize(35, 25);
			boxResult.boxResultPanel.add(boxResult.img, BOX_RESULTS_INDENT, 5);

			boxResult.lblPosition = new Label();
			boxResult.lblPosition.setText(String.valueOf((char)(i + 1 + 64)));
			boxResult.lblPosition.setStyleName("whiteText");
			boxResult.lblPosition.setWidth("34px");
			boxResult.boxResultPanel.add(boxResult.lblPosition, BOX_RESULTS_INDENT, 8);

			boxResult.name = new HTML(fields[0]);
			boxResult.name.setStyleName("greyText");
			boxResult.boxResultPanel.add(boxResult.name, BOX_RESULTS_INDENT + boxResult.img.getOffsetWidth() + 5, 8);

			boxResult.address = new Label(fields[1], true);
			waypointWithout[i] = new Waypoint(boxResult.address.getText());
			Ruvego.getGeocode().getLatLng(boxResult.address.getText(), ItineraryCommon.mapsCallback);

			boxResult.address.setStyleName("boxValueAddressText");
			boxResult.address.setWidth((BOX_PANEL_WIDTH - BOX_RESULTS_INDENT * 2) + "px");
			boxResult.boxResultPanel.add(boxResult.address, BOX_RESULTS_INDENT, BOX_RESULTS_SPACING + boxResult.img.getOffsetHeight());

			boxResult.itineraryEntryMenu = new ItineraryEntryMenu(boxResult.boxResultPanel, 
					boxResult.address.getAbsoluteTop() - boxResult.boxResultPanel.getAbsoluteTop() + boxResult.address.getOffsetHeight() + 2,
					i, this);

			boxResult.routeInfo = new HTML("");
			boxResult.routeInfo.setStyleName("routeInfoText");
			boxResult.boxResultPanel.add(boxResult.routeInfo, 20, boxResult.itineraryEntryMenu.btnMore.getAbsoluteTop() - 
					boxResult.boxResultPanel.getAbsoluteTop() + boxResult.itineraryEntryMenu.btnMore.getOffsetHeight() + 8);
			boxResult.boxResultPanel.setHeight((boxResult.routeInfo.getAbsoluteTop() - boxResult.boxResultPanel.getAbsoluteTop()
					+ boxResult.routeInfo.getOffsetHeight() + 2) + "px");

			boxResult.btnDel = new Image("Images/closebutton.png");
			boxResult.boxResultPanel.add(boxResult.btnDel, boxResult.boxResultPanel.getOffsetWidth() - 27, 5);
			boxResult.btnDel.setPixelSize(18, 18);
			boxResult.btnDel.setStyleName("imgLogo");

			boxResult.btnDel.setLayoutData(i);
			boxResult.btnDel.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					Image img = (Image) event.getSource();
					ItineraryState.setEntry(DayActivityPlan.this, (Integer)img.getLayoutData());
					//		BoxResult.this.address.setText("fdsad");
					System.out.println("Clicked delete for : " + (Integer)img.getLayoutData());
					Ruvego.getMapWidget().clearOverlays();
					ItineraryCommon.showConfirmPanel();
				}
			});

			/* Add everything to Linked List */
			boxResultLL.add(boxResult);
			waypointLL.add(waypointWithout[i]);
		}
	}

	public void deleteEntry() {
		BoxResult freeBoxResult;
		System.out.println("Deleting " + ItineraryState.getEntry().dayName + " Entry " + ItineraryState.getEntryNum());

		freeBoxResult = boxResultLL.remove(ItineraryState.getEntryNum());
		grid.removeRow(ItineraryState.getEntryNum() + SRC_PANEL);
		waypointLL.remove(ItineraryState.getEntryNum());
		TOTAL_COUNT_IN_PLAN--;

		freeBoxResult.boxResultPanel.removeFromParent();
		freeBoxResult = null;

		for (int i = ItineraryState.getEntryNum(); i < TOTAL_COUNT_IN_PLAN; i++) {
			boxResultLL.get(i).itineraryEntryMenu.updateEntryPosition(i + SRC_PANEL);
			boxResultLL.get(i).btnDel.setLayoutData(i);
		}
		reAssignLabels();
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



		BoxResult curBoxResult = boxResultLL.remove(currentPos);
		boxResultLL.add(newPos, curBoxResult);
		Waypoint curWaypoint = waypointLL.remove(currentPos);
		waypointLL.add(newPos, curWaypoint);


		grid.removeRow(currentPos + SRC_PANEL);
		grid.insertRow(newPos + SRC_PANEL);
		grid.setWidget(newPos + SRC_PANEL, 0, curBoxResult.boxResultPanel);

		if (currentPos > newPos) {
			int temp = currentPos;
			currentPos = newPos;
			newPos = temp;
		}

		for (int i = currentPos; i <= newPos; i++) {
			boxResultLL.get(i).itineraryEntryMenu.updateEntryPosition(i + SRC_PANEL);
			boxResultLL.get(i).btnDel.setLayoutData(i);
		}
		reAssignLabels();
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

		Ruvego.errorDisplayClear();
		Directions.loadFromWaypoints(waypointLL.toArray(new Waypoint[waypointLL.size()]), opts, directionsCallback);
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
					waypointLL.removeLast();
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
					waypointDst = new Waypoint(point);
					waypointLL.addLast(waypointDst);

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
					waypointLL.removeFirst();

					reAssignLabels();
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
					waypointSrc = new Waypoint(point);
					waypointLL.addFirst(waypointSrc);
					
					reAssignLabels();
				}

			};

		} else {
			addToGrid(srcBox.boxResultPanel, DAY_INFO_PRESENT);
		}

		SRC_PANEL = 1;
		srcBox.boxResultPanel.setSize("100%", (srcBox.routeInfo.getAbsoluteTop() - srcBox.boxResultPanel.getAbsoluteTop() + 
				srcBox.routeInfo.getOffsetHeight() + 5) + "px");
	}

	private void reAssignLabels() {
		Iterator<BoxResult> itr = boxResultLL.iterator();
		
		int count = 0;
		if (!ItineraryCommon.SRC_ADDRESS.equalsIgnoreCase("")) {
			count = 'B';
		} else {
			count = 'A';
		}
		
		while (itr.hasNext()) {
			itr.next().lblPosition.setText(String.valueOf((char)(count)));
			count++;
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