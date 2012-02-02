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

	static private ChangeHandler listBoxDayChangeHdlr;

	static private ListBox listBoxDayList;

	static private HorizontalPanel addToItineraryPanel;

	static private Label lblAddToIti;
	
	static private ItineraryPage itineraryPage;

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
				menuHide();
				String preparedText = prepareEntryForInsert(ResultsDataGridView.htmlName.getText(), ResultsDataGridView.htmlAddress.getText());
				Ruvego.insertItem(preparedText);
			}
		});

		addToItineraryPanel = new HorizontalPanel();

		lblAddToIti = new Label();
		lblAddToIti.setStyleName("menuItemTextNormal");
		setupAddToItineraryText(Ruvego.lblItineraryNameText.getText());

		listBoxDayList = new ListBox();

		addToItineraryPanel.add(lblAddToIti);
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


		listBoxDayChangeHdlr = new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				menuHide();
				ItineraryPage.itineraryPlan[0].addEntry(ResultsDataGridView.htmlName.getText(), ResultsDataGridView.htmlAddress.getText());
				//TODO write to backend and also put it in the list
			}
		};

		listBoxDayList.addChangeHandler(listBoxDayChangeHdlr);

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

	protected String prepareEntryForInsert(String name, String address) {
		return (name + "<;>" + address);
	}

	private void setupAddToItineraryText(String text) {
		lblAddToIti.setText("Add to " + text + " : ");
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
		listBoxDayList.setSelectedIndex(0);
	}


	protected static void menuHide() {
		activityMenuPopUpPanel.setVisible(false);
		btnMenu.setStyleName("activityBtnMore");	
	}

	protected void onItineraryActive() {
		setupAddToItineraryText(Ruvego.lblItineraryNameText.getText());
		listBoxDayList.clear();
		listBoxDayList.addItem("Choose");
		for (int i = 1; i <= Ruvego.itineraryState.getNumDays(); i++) {
			listBoxDayList.addItem("Day " + i);
		}
		menuPanel.insert(addToItineraryPanel, 0);
	}

	protected void onItineraryInactive() {
		listBoxDayList.addItem("Choose");
		for (int i = 1; i <= Ruvego.itineraryState.getNumDays(); i++) {
			listBoxDayList.addItem("Day " + i);
		}
		menuPanel.remove(addToItineraryPanel);
	}


}
