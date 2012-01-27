package com.ruvego.project.client;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ResultsActivityMenu {
	static private ResultsActivityMenu page;

	static private VerticalPanel menuPanel;

	static private Label btnMenu;
	
	static private PopupPanel activityMenuPopUpPanel;

	public static ResultsActivityMenu getPage() {
		if (page == null) {
			page = new ResultsActivityMenu();
		}
		return page;
	}

	private ResultsActivityMenu() {
		menuPanel = new VerticalPanel();
		activityMenuPopUpPanel = new PopupPanel(true, true);
		activityMenuPopUpPanel.setStyleName("popUpPanel");
		
		menuPanel.setWidth("250px");
		menuPanel.setSpacing(4);

		Label lblAddToBox = new Label("Add to Box");
		lblAddToBox.setStyleName("menuItemText");
		lblAddToBox.setWidth("100%");

		lblAddToBox.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Ruvego.insertItem(ResultsDataGridView.htmlName.getText() + "<;>" + ResultsDataGridView.htmlAddress.getText() + "<;>" + ResultsDataGridView.rating);
				menuHide();
			}
		});

		HorizontalPanel addToItineraryPanel = new HorizontalPanel();
		Label addToIti = new Label("Add to " + Ruvego.lblItineraryNameText.getText() + " : ");
		addToIti.setStyleName("menuItemTextNormal");
		
		final ListBox listBoxDayList = new ListBox();
		
		addToItineraryPanel.add(addToIti);
		addToItineraryPanel.add(listBoxDayList);
		addToItineraryPanel.setSpacing(5);
		
		
		Label lblPlanEvent = new Label("Plan an Event");
		lblPlanEvent.setStyleName("menuItemText");
		lblPlanEvent.setWidth("100%");
		
		btnMenu = new Label("More");
		btnMenu.setStyleName("activityBtnMore");
		ResultsDataGridView.resultsBriefPanel.add(btnMenu, 12, ResultsDataGridView.resultsBriefPanel.getOffsetHeight() - 
				Ruvego.getFooterHeight() - btnMenu.getOffsetHeight());

		menuPanel.add(lblAddToBox);
		menuPanel.add(lblPlanEvent);
		if (Ruvego.itineraryState.isItineraryActive()) {
			listBoxDayList.addItem("Choose");
			for (int i = 1; i <= Ruvego.itineraryState.getNumDays(); i++) {
				listBoxDayList.addItem("Day " + i);
			}
			menuPanel.insert(addToItineraryPanel, 0);
		}
		
		listBoxDayList.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				menuHide();
				//TODO add to appropriate place in the itinerary
			}
		});
		
		menuPanel.setStyleName("menuPanel");
		
		activityMenuPopUpPanel.add(menuPanel);
		RootPanel.get().add(activityMenuPopUpPanel);

		btnMenu.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (activityMenuPopUpPanel.isVisible() == false) {
					menuShow();
				} else {
					menuHide();
				}
			}
		});
		
		activityMenuPopUpPanel.addCloseHandler(new CloseHandler<PopupPanel>() {
			
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				menuHide();
			}
		});
		
		activityMenuPopUpPanel.addAutoHidePartner(btnMenu.getElement());


		menuHide();
	
	}

	public void panelResizeAlignments() {
		ResultsDataGridView.resultsBriefPanel.setWidgetPosition(btnMenu, 20, ResultsDataGridView.resultsBriefPanel.getOffsetHeight() - 
				btnMenu.getOffsetHeight() - 10);
	}

	protected void menuShow() {	
		activityMenuPopUpPanel.setVisible(true);
		activityMenuPopUpPanel.show();
		btnMenu.setStyleName("activityBtnMoreClick");
		RootPanel.get().setWidgetPosition(activityMenuPopUpPanel, btnMenu.getAbsoluteLeft(), 
				btnMenu.getAbsoluteTop() - activityMenuPopUpPanel.getOffsetHeight());
	}


	protected static void menuHide() {
		activityMenuPopUpPanel.setVisible(false);
		btnMenu.setStyleName("activityBtnMore");	
	}




}
