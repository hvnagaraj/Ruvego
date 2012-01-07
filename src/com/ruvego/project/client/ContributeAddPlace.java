package com.ruvego.project.client;


import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.maps.client.geocode.LatLngCallback;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;

public class ContributeAddPlace {

	static private ContributeAddPlace page;

	static private AbsolutePanel placePanel;
	
	static private AsyncCallback<Boolean> callbackWritePlace;

	static private Button btnSubmit;
	
	static private TextBox textBox;
	static private ListBox listBox;


	public static ContributeAddPlace getPage() {
		if (page == null) {
			page = new ContributeAddPlace();
		}
		return page;
	}

	private ContributeAddPlace() {
		placePanel = new AbsolutePanel();

		/*
		RootPanel rootPanel = RootPanel.get();
		rootPanel.add(placePanel, 10, 10);
		*/
		
		RuvegoContribute.getContributePanel().add(placePanel, 0, 50);
		placePanel.setSize("1000px", "252px");

		AbsolutePanel backPanel = new AbsolutePanel();
		backPanel.setStyleName("backPanel");
		placePanel.add(backPanel, 280, 10);
		backPanel.setSize("440px", "207px");

		listBox = new ListBox();
		backPanel.add(listBox, 152, 45);
		listBox.setWidth("200px");
		listBox.addItem("Choose");
		listBox.addItem("California");
		listBox.addItem("Alabama");
		listBox.setVisibleItemCount(1);

		textBox = new TextBox();
		backPanel.add(textBox, 153, 90);
		textBox.setWidth("200px");
		textBox.setFocus(true);

		btnSubmit = new Button("New button");
		backPanel.add(btnSubmit, 191, 143);
		btnSubmit.setText("Submit");
		btnSubmit.setStyleName("button");
		btnSubmit.setPixelSize(70, 30);

		Label lblState = new Label("Choose State : ");
		backPanel.add(lblState, 56, 49);
		lblState.setSize("88px", "18px");
		lblState.setStyleName("contributeText");

		Label lblColumn = new Label("Place Name : ");
		backPanel.add(lblColumn, 65, 98);
		lblColumn.setStyleName("contributeText");

		btnSubmit.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Ruvego.geocoder.getLatLng(textBox.getText(), new LatLngCallback() {

					@Override
					public void onFailure() {
						Window.alert("Invalid City");
						System.out.println("Geocoding error 2");
					}	

					@Override
					public void onSuccess(LatLng point) {
						WritePlacePacket writeData = new WritePlacePacket(textBox.getText(), listBox.getItemText(listBox.getSelectedIndex()), 
								point.getLatitudeRadians(), point.getLongitudeRadians());
						
						RuvegoContribute.resultsWriteService.writeResults(writeData, callbackWritePlace);

					}

				});	
			}
		});
		
		/* Fetch Category Header which stores info on the columns*/
		callbackWritePlace = new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				System.err.println("Async: Add Place error");
				
			}

			@Override
			public void onSuccess(Boolean result) {
				if (result == false) {
					Window.alert("Unable to add the Place");
				}
			}
			
		};
	}

	public static void clearContent() {
		if (placePanel != null) {
			placePanel.setVisible(false);
		}
	}

	public void panelsView() {
		clearColumnData();
		placePanel.setVisible(true);
	}
	
	protected void clearColumnData() {
		textBox.setText("");
		listBox.setSelectedIndex(0);
	}
}

