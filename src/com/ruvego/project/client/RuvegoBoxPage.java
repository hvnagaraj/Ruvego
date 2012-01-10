package com.ruvego.project.client;

import java.util.LinkedList;

import com.apple.dnssd.TXTRecord;
import com.google.gwt.core.client.GWT;
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
import com.google.gwt.maps.client.geocode.StatusCodes;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.LatLngBounds;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;

public class RuvegoBoxPage {
	static private RuvegoBoxPage page;

	final static int SRC_DST_PANEL_HEIGHT = 50;
	final static int BOX_RESULTS_INDENT = 5;
	final static int BOX_RESULTS_SPACING = 5;

	private static ScrollPanel scrollPanel;

	private static Grid grid;

	private static AbsolutePanel srcDstPanel;

	private static LatLngCallback mapsCallback;

	private static int boxValueCount;

	private static Geocoder geocoder;

	private static DirectionsPanel directionsPanel = null;

	private static DirectionQueryOptions opts;

	private static DirectionsCallback directionsCallback;

	private static TextBox txtBoxSource;
	private static TextBox txtBoxDest;

	private static LatLngCallback mapsCheckSrcAddress;
	private static LatLngCallback mapsCheckDstAddress;

	private static CheckBox srcDstSame;

	private static String query = "";
	private static String directionsToAddress = "";

	private class BoxResult {
		private AbsolutePanel boxResultPanel = new AbsolutePanel();
		private Image imgArrow = new Image();
		private Label lblPosition = new Label();
		private TextBox txtBoxPosition = new TextBox();
		private HTML name;
		private Label address;
		private Label rating;
	}

	private BoxResult[] boxResult;

	private RuvegoBoxPage() {
		System.out.println("Creating an object of type RuvegoBoxPage");
		scrollPanel = new ScrollPanel();
		srcDstPanel = new AbsolutePanel();

		RootPanel.get().add(scrollPanel, Ruvego.getIndent(), Ruvego.getOtherWidgetTop());
		scrollPanel.setStyleName("contributePanelBG");
		scrollPanel.setPixelSize(300, Window.getClientHeight() - Ruvego.getOtherWidgetTop() - Ruvego.getFooterHeight() - SRC_DST_PANEL_HEIGHT - 5);
		srcDstPanel = new AbsolutePanel();
		srcDstPanel.setPixelSize(500, 150);
		srcDstPanel.setStyleName("contributePanelBG");

		RootPanel.get().add(srcDstPanel, Ruvego.getIndent()/*scrollPanel.getAbsoluteLeft() + scrollPanel.getOffsetWidth() + 5*/, 
				Window.getClientHeight() - Ruvego.getFooterHeight() - SRC_DST_PANEL_HEIGHT);
		srcDstPanel.setStyleName("contributePanelBG");
		srcDstPanel.setPixelSize(1000 - srcDstPanel.getAbsoluteLeft(), SRC_DST_PANEL_HEIGHT);

		Label scrDstInfo = new Label("Enter the itinerary source and ending points");
		srcDstPanel.add(scrDstInfo, 5, 2);
		scrDstInfo.setStyleName("srcDstInfoText");

		grid = new Grid(25, 1);
		scrollPanel.add(grid);
		grid.setWidth("100%");

		Label source = new Label("Source Address :");
		source.setStyleName("srcDstText");
		srcDstPanel.add(source, 5, 7 + scrDstInfo.getOffsetHeight());

		txtBoxSource = new TextBox();
		txtBoxSource.setPixelSize(175, 10);
		srcDstPanel.add(txtBoxSource, source.getAbsoluteLeft() - srcDstPanel.getAbsoluteLeft() + source.getOffsetWidth() + 5, 5 + scrDstInfo.getOffsetHeight());

		Label dest = new Label("Destination Address :");
		dest.setStyleName("srcDstText");
		srcDstPanel.add(dest, txtBoxSource.getAbsoluteLeft() - srcDstPanel.getAbsoluteLeft() + txtBoxSource.getOffsetWidth() + 10, 7 + scrDstInfo.getOffsetHeight());

		txtBoxDest = new TextBox();
		txtBoxDest.setPixelSize(175, 10);
		srcDstPanel.add(txtBoxDest, dest.getAbsoluteLeft() - srcDstPanel.getAbsoluteLeft() + dest.getOffsetWidth() + 5, 5 + scrDstInfo.getOffsetHeight());

		srcDstSame = new CheckBox("same as source");
		srcDstSame.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				if (srcDstSame.getValue() == true) {
					txtBoxDest.setText(txtBoxSource.getText());
				} else {
					txtBoxDest.setText("");
				}
			}
		});
		srcDstPanel.add(srcDstSame, txtBoxDest.getAbsoluteLeft() - srcDstPanel.getAbsoluteLeft() + txtBoxDest.getOffsetWidth() + 5, 
				7 + scrDstInfo.getOffsetHeight());
		srcDstSame.setStyleName("srcDstInfoText");

		Button btnRoute = new Button("Route");
		btnRoute.setStyleName("button");
		srcDstPanel.add(btnRoute, srcDstPanel.getOffsetWidth() - 90, 12);

		btnRoute.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				mapRoute();
			}
		});

		final LatLngBounds bounds = LatLngBounds.newInstance();

		mapsCallback = new LatLngCallback() {

			@Override
			public void onFailure() {
				Window.alert("cannot geocode");
			}

			@Override
			public void onSuccess(LatLng point) {
				Ruvego.getMapWidget().setCenter(point, 13);
				Marker marker = new Marker(point);
				Ruvego.getMapWidget().addOverlay(marker);
				bounds.extend(point);
				Ruvego.getMapWidget().setZoomLevel(Ruvego.getMapWidget().getBoundsZoomLevel(bounds));
			}
		};

		mapsCheckSrcAddress = new LatLngCallback() {

			@Override
			public void onFailure() {
				Window.alert("cannot geocode source address");
			}

			@Override
			public void onSuccess(LatLng point) {
				query = query + txtBoxSource.getText() + " ";
				System.out.println("Callback : " + query);

				query = query + directionsToAddress;

				if (!txtBoxDest.getText().equalsIgnoreCase("")) {
					query = query + "to: ";
					System.out.println("Dst Address : " + txtBoxDest.getText());
					geocoder.getLatLng(txtBoxDest.getText(), mapsCheckDstAddress);
				} else {
					System.out.println(query);
					Directions.load(query, opts, directionsCallback);
				}

			}
		};

		mapsCheckDstAddress = new LatLngCallback() {

			@Override
			public void onFailure() {
				Window.alert("cannot geocode dest address");
			}

			@Override
			public void onSuccess(LatLng point) {
				query = query + txtBoxDest.getText();
				System.out.println("Callback : " + query);
				Directions.load(query, opts, directionsCallback);
			}
		};



		geocoder = Ruvego.getGeocode();


		opts = new DirectionQueryOptions(Ruvego.getMapWidget(), 
				directionsPanel); 

		directionsCallback = new DirectionsCallback() { 
			public void onFailure(int statusCode) { 
				Window.alert("Failed to load directions: Status " 
						+ StatusCodes.getName(statusCode) + " " + statusCode); 
			} 
			public void onSuccess(DirectionResults result) { 
				System.out.println("Distance : " + result.getDistance());
				GWT.log("Successfully loaded directions.", null); 
			} 
		};


	}

	protected void mapRoute() {
		query = "";
		//directionsToAddress = "";
		Ruvego.getMapWidget().clearOverlays();

		if (!txtBoxSource.getText().equalsIgnoreCase("")) {
			query = query + "from: ";
			System.out.println("Src Address : " + txtBoxSource.getText());
			geocoder.getLatLng(txtBoxSource.getText(), mapsCheckSrcAddress);
		}

	}

	public void fetchBoxResults() {
		String cookieValue = Cookies.getCookie("sid");
		if (cookieValue != null) {
			boxValueCount = Integer.parseInt(cookieValue);

			System.out.println("No of items in the cookie : " + boxValueCount);
			if (boxValueCount > 25) {
				System.out.println("Box has more than 25 items");
				return;
			}

			boxResult = new BoxResult[boxValueCount];

			for (int i = 0; i < boxValueCount; i++) {
				boxResult[i] = new BoxResult();
				//boxResult[i].boxResultPanel = new AbsolutePanel();
				boxResult[i].boxResultPanel.setSize("100%", "100px");
				grid.setWidget(i, 0, boxResult[i].boxResultPanel);

				if (i % 2 == 1) {
					boxResult[i].boxResultPanel.setStyleName("boxResultPanelEven");
				}

				Image img = new Image("Images/boxPosition.png");
				img.setPixelSize(35, 25);
				boxResult[i].boxResultPanel.add(img, BOX_RESULTS_INDENT, 5);

				boxResult[i].lblPosition.setText(String.valueOf(i + 1));
				boxResult[i].lblPosition.setStyleName("whiteText");
				boxResult[i].lblPosition.setWidth("34px");
				boxResult[i].boxResultPanel.add(boxResult[i].lblPosition, BOX_RESULTS_INDENT, 8);

				boxResult[i].name = new HTML("Golden Gate Bridge " + (i + 1));
				boxResult[i].name.setStyleName("silverText");
				boxResult[i].boxResultPanel.add(boxResult[i].name, BOX_RESULTS_INDENT + img.getOffsetWidth() + 5, 8);

				boxResult[i].address = new Label("1063 Morse Avenue Apt 20-300 Sunnyvale CA USA", false);
				
				if (i == 0)
				boxResult[0].address = new Label("1063 Morse Avenue Apt 20-300 Sunnyvale CA USA", false);
				if (i == 1)
				boxResult[1].address = new Label("425 E Tasman Drive San Jose CA", false);
				if (i == 2)
				boxResult[2].address = new Label("San Francisco CA", false);

				//geocoder.getLatLng(boxResult[i].address.getText(), mapsCallback);
				directionsToAddress = directionsToAddress + "to: " + boxResult[i].address.getText() + " ";
				boxResult[i].address.setStyleName("boxValueAddressText");
				boxResult[i].address.setWidth((boxResult[i].boxResultPanel.getOffsetWidth() - BOX_RESULTS_INDENT * 2) + "px");
				boxResult[i].boxResultPanel.add(boxResult[i].address, BOX_RESULTS_INDENT, BOX_RESULTS_SPACING + img.getOffsetHeight());

				Image rectangle = new Image("Images/blackrectangle.png");
				rectangle.setPixelSize(80, 35);
				boxResult[i].boxResultPanel.add(rectangle, BOX_RESULTS_INDENT, 
						boxResult[i].address.getAbsoluteTop() - boxResult[i].boxResultPanel.getAbsoluteTop() + boxResult[i].address.getOffsetHeight() + 10);

				boxResult[i].rating = new Label("3.3/5.0", true);
				boxResult[i].rating.setStyleName("orangeBoldText");
				boxResult[i].rating.setWidth("125px");
				boxResult[i].boxResultPanel.add(boxResult[i].rating, BOX_RESULTS_INDENT + 12, 
						boxResult[i].address.getAbsoluteTop() - boxResult[i].boxResultPanel.getAbsoluteTop() + boxResult[i].address.getOffsetHeight() + 18);

				Label travelTime = new Label("I want to do this activity at position", true);
				travelTime.setStyleName("boxValuePositionText");
				travelTime.setWidth("125px");
				boxResult[i].boxResultPanel.add(travelTime, BOX_RESULTS_INDENT + rectangle.getOffsetWidth() + 20, 
						boxResult[i].address.getAbsoluteTop() - boxResult[i].boxResultPanel.getAbsoluteTop() + boxResult[i].address.getOffsetHeight() + 10);


				boxResult[i].txtBoxPosition.setText(String.valueOf(i + 1));
				boxResult[i].txtBoxPosition.setPixelSize(18, 15);
				boxResult[i].boxResultPanel.add(boxResult[i].txtBoxPosition, travelTime.getAbsoluteLeft() - boxResult[i].boxResultPanel.getAbsoluteLeft()
						+ travelTime.getOffsetWidth(), 
						boxResult[i].address.getAbsoluteTop() - boxResult[i].boxResultPanel.getAbsoluteTop() + boxResult[i].address.getOffsetHeight() + 12);

				boxResult[i].imgArrow = new Image("Images/arrow.png");
				boxResult[i].imgArrow.setPixelSize(24, 24);
				boxResult[i].imgArrow.setLayoutData(i);
				boxResult[i].boxResultPanel.add(boxResult[i].imgArrow, boxResult[i].txtBoxPosition.getAbsoluteLeft() - boxResult[i].boxResultPanel.getAbsoluteLeft()
						+ boxResult[i].txtBoxPosition.getOffsetWidth() + 2, 
						boxResult[i].address.getAbsoluteTop() - boxResult[i].boxResultPanel.getAbsoluteTop() + boxResult[i].address.getOffsetHeight() + 12);

				boxResult[i].imgArrow.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						Image tempImg = (Image) event.getSource();
						reorganizePositions((Integer)tempImg.getLayoutData());
					}
				});
			}
		}


		geocoder.getLatLng(boxResult[0].address.getText(), mapsCallback);
		geocoder.getLatLng(boxResult[1].address.getText(), mapsCallback);
		geocoder.getLatLng(boxResult[2].address.getText(), mapsCallback);

		Ruvego.ruvegoPanelAlignments();
		Ruvego.getMapsPanel().setWidth((Window.getClientWidth() - scrollPanel.getOffsetWidth() - Ruvego.getIndent()) + "px");
		RootPanel.get().setWidgetPosition(Ruvego.getMapsPanel(), Ruvego.getIndent() + scrollPanel.getOffsetWidth(), Ruvego.getOtherWidgetTop());
	}

	protected void reorganizePositions(int currentPos) {
		//int currentPos = Integer.parseInt(lblPosition[imgArrow].getText());
		int newPos = Integer.parseInt(boxResult[currentPos].txtBoxPosition.getText()) - 1;

		System.out.println("Current Position : " + currentPos + " New Position : " + newPos);

		if ((newPos < 0) || (newPos > boxValueCount) || (currentPos == newPos)) {
			//TODO Error message
			return;
		}

		//lblPosition[currentPos].setText("33"/*txtBoxPosition.getText()*/);

		if (currentPos > newPos) {
			int temp = currentPos;
			currentPos = newPos;
			newPos = temp;
		}

		System.out.println("Current Position : " + currentPos + " New Position : " + newPos);

		String tempName = boxResult[currentPos].name.getText();
		String tempAddress = boxResult[currentPos].address.getText();
		String tempRating = boxResult[currentPos].rating.getText();


		for (int i = currentPos; i < newPos; i++) {
			boxResult[i].name.setText(boxResult[i + 1].name.getText());
			boxResult[i].address.setText(boxResult[i + 1].address.getText());
			boxResult[i].rating.setText(boxResult[i + 1].rating.getText());
			System.out.println("Moving from  " + (i + 1) + " to " + i);
		}

		boxResult[newPos].name.setText(tempName);
		boxResult[newPos].address.setText(tempAddress);
		boxResult[newPos].rating.setText(tempRating);
		boxResult[currentPos].txtBoxPosition.setText((currentPos + 1) + "");
	}


	public static RuvegoBoxPage getPage() {
		if (page == null) {
			page = new RuvegoBoxPage();
		}
		return page;
	}

	public void panelsView() {
		// TODO Auto-generated method stub

	}

}
