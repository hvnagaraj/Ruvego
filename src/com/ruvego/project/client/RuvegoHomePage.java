package com.ruvego.project.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.maps.client.geocode.LatLngCallback;
import com.google.gwt.maps.client.geocode.LocationCallback;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

public class RuvegoHomePage {
	
	static private RuvegoHomePage page;
	
	static final private int HOMEPAGE_MIN_HEIGHT = 650;

	/* Numeric constants */
	int MAPS_MIN_HEIGHT = 275;

	static private HorizontalPanel lblHierPanel = new HorizontalPanel();
	static private Label lblHier1 = new Label();

	public static HorizontalPanel getLblHierPanel() {
		return lblHierPanel;
	}

	public static Label getLblHier1() {
		return lblHier1;
	}

	static ResultsCategoryView categoryResults = null;

	static private LatLngCallback placeLatLngCallback;
	
	public static RuvegoHomePage getPage() {
		if (page == null) {
			page = new RuvegoHomePage();
		}
		return page;
	}


	private RuvegoHomePage() {
		System.out.println("Creating an object of type RuvegoHomePage");

		/* Hier Labels */
		lblHier1.setText(Ruvego.getPlace());
		lblHierPanel.add(lblHier1);
		lblHier1.setStyleName("lblHierNormal");
		lblHierPanel.setVisible(true);
		lblHier1.setVisible(false);
		lblHierPanel.setSpacing(7);

		Ruvego.getSecondHeaderPanel().add(lblHierPanel, 0, 0);

		/* All the Event Handlers */
		/* Resize window handler for making the site compatible for different resolutions utilizing the maximum possible resources */
		Window.addResizeHandler(new ResizeHandler() {

			public void onResize(ResizeEvent event) {
				ruvegoHomePagePanelAlignments();
			}
		});


		lblHier1.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (categoryResults != null) {

					lblHier1.setStyleName("lblHierNormal");
					categoryResults.getLblHier().setStyleName("lblHierNormal");
					categoryResults.getLblHier().setVisible(false);
					categoryResults.getLblHierDiv().setVisible(false);
					lblHier1.setStyleName("lblHierNormal");
					categoryResults.panelsView();
				}
			}
		});
		
		/** Used to display the appropriate map when the place is changed */
		placeLatLngCallback = new LatLngCallback() {
			
			@Override
			public void onSuccess(LatLng point) {
				Ruvego.getMapWidget().clearOverlays();
				Ruvego.getMapWidget().setCenter(point, 13);
			}
			
			@Override
			public void onFailure() {
//				Window.alert("Server is busy. Please try again after sometime");
			}
		}; 
		
		/* All the alignments of the main page */
		ruvegoHomePagePanelAlignments();
	}

	public void showResults() {		
		Ruvego.getGeocode().getLatLng(Ruvego.getPlace(), placeLatLngCallback);

		categoryResults = ResultsCategoryView.getPage();
		categoryResults.fetchCategoryResults();
	}

	public void ruvegoHomePagePanelAlignments() {
		/** Setting minimum page height for the HomePage. Appropriate pages must set this value */ 
		Ruvego.setMinimumPageHeight(HOMEPAGE_MIN_HEIGHT);
		Ruvego.secondHeaderPanel.setWidgetPosition(lblHierPanel, 0, 0);
		Ruvego.ruvegoPanelAlignments();
	}

	
	public void clearContent() {
		lblHier1.setVisible(false);
		if (categoryResults != null) {
			categoryResults.clearContent();
		}
	}

	public int getClientHeight() {
		if (Window.getClientHeight() > HOMEPAGE_MIN_HEIGHT) {
			return(Window.getClientHeight());
		} else {
			return(HOMEPAGE_MIN_HEIGHT);
		}
	}
}
