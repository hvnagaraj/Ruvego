package com.ruvego.project.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.maps.client.geocode.DirectionsPanel;
import com.google.gwt.maps.client.geocode.LatLngCallback;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.LatLngBounds;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;

public class ItineraryCommon {
	private static ItineraryCommon page;
	
	/* Static variables */
	private static PopupPanel confirmPanel; 

	protected static DirectionsPanel directionsPanel = null;
	
	protected static AbsolutePanel routeBriefPanel;
	
	private static HTML lblTotalDistance;
	
	private static HTML lblTotalDuration;
	
	protected static String SRC_ADDRESS = "";
	
	protected static String DST_ADDRESS = "";
	
	protected static LatLng srcPoint;
	protected static LatLng dstPoint;

	protected static LatLngCallback mapsCallback;
	
	protected static LatLngBounds bounds = LatLngBounds.newInstance();

	
	public static ItineraryCommon getPage() {
		if (page == null) {
			page = new ItineraryCommon();
		}
		return page;
	}
	
	private ItineraryCommon() {
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
				ItineraryState.getEntry().deleteEntry();
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


		setupRouteBriefPanel();

	}
	
	protected static void setTotalDistDuration(String dist, String duration) {
		lblTotalDistance.setHTML("Total Distance : " + dist);
		lblTotalDuration.setHTML("Total Duration : " + duration);
		routeBriefPanel.setVisible(true);
		routeBriefPanel.setWidth((lblTotalDuration.getOffsetWidth() + 10) + "px");
	}

	private static void setupRouteBriefPanel() {
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

	public static void showConfirmPanel() {
		confirmPanel.setVisible(true);
		confirmPanel.show();
		confirmPanel.center();
	}

}
