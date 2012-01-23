package com.ruvego.project.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ResultsActivityMenu {
	static private ResultsActivityMenu page;

	static private Timer timer;

	static private VerticalPanel menuPanel;

	static private Image imgMenu;

	public static ResultsActivityMenu getPage() {
		if (page == null) {
			page = new ResultsActivityMenu();
		}
		return page;
	}

	private ResultsActivityMenu() {
		menuPanel = new VerticalPanel();
		menuPanel.setWidth("150px");
		menuPanel.setSpacing(4);

		Label lblAddToBox = new Label("Add to Box");
		lblAddToBox.setStyleName("menuItemText");
		lblAddToBox.setWidth("100%");

		timer = new Timer() {
			public void run() {
				Ruvego.boxErrorClear();
			}
		};

		lblAddToBox.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Ruvego.insertItem(ResultsDataGridView.htmlName.getText() + "<;>" + ResultsDataGridView.htmlAddress.getText() + "<;>" + ResultsDataGridView.rating);
				menuHide();

				// Execute the timer to expire 2 seconds in the future
				timer.schedule(3000);
			}
		});

		Label lblPlanEvent = new Label("Plan an Event");
		lblPlanEvent.setStyleName("menuItemText");
		lblPlanEvent.setWidth("100%");
		
		imgMenu = new Image("Images/menuicon.png");
		imgMenu.setStyleName("menuNormal");
		imgMenu.setPixelSize(35, 35);
		ResultsDataGridView.resultsBriefPanel.add(imgMenu, 12, ResultsDataGridView.resultsBriefPanel.getOffsetHeight() - 
				Ruvego.getFooterHeight() - imgMenu.getOffsetHeight());

		menuPanel.add(lblAddToBox);
		menuPanel.add(lblPlanEvent);
		menuPanel.setStyleName("menuPanel");
		ResultsDataGridView.resultsBriefPanel.add(menuPanel, 12, ResultsDataGridView.resultsBriefPanel.getOffsetHeight() - 
				imgMenu.getAbsoluteTop() - 100);
		menuHide();


		imgMenu.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (menuPanel.isVisible() == false) {
					menuShow();
				} else {
					menuHide();
				}
			}
		});

		panelResizeAlignments();

	}

	public void panelResizeAlignments() {
		ResultsDataGridView.resultsBriefPanel.setWidgetPosition(imgMenu, 20, ResultsDataGridView.resultsBriefPanel.getOffsetHeight() - 
				imgMenu.getOffsetHeight() - 10);
	}

	protected void menuShow() {
		menuPanel.setVisible(true);	
		imgMenu.setStyleName("menuNormal:hover");
		ResultsDataGridView.resultsBriefPanel.setWidgetPosition(menuPanel, 20, imgMenu.getAbsoluteTop() - 
				ResultsDataGridView.resultsBriefPanel.getAbsoluteTop() - 
				menuPanel.getOffsetHeight());
	}


	protected static void menuHide() {
		imgMenu.setStyleName("menuNormal");
		menuPanel.setVisible(false);	
	}




}
