package com.ruvego.project.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class BoxMenu {
	static private BoxMenu page;

	private static Image imgIcon;
	
	private static VerticalPanel menuInputPanel;
	
	private static PopupPanel popUpPanel;
	
	private static CreateItinerary createItinerary;

	public static BoxMenu getPage() {
		if (page == null) {
			page = new BoxMenu();
		}
		return page;
	}

	private BoxMenu() {
		popUpPanel = new PopupPanel(true, true);
		
		imgIcon = new Image("Images/menuwhite.png");
		imgIcon.setStyleName("imgLogo");
		
		Ruvego.getSecondHeaderPanel().add(imgIcon);
		Ruvego.getSecondHeaderPanel().setWidgetPosition(imgIcon, 5, 7);
		
		menuInputPanel = new VerticalPanel();
		menuInputPanel.setWidth("250px");
		menuInputPanel.setSpacing(4);

		Label lblCreateItinerary = new Label("Create a new Itinerary");
		lblCreateItinerary.setStyleName("menuItemText");
		lblCreateItinerary.setWidth("100%");
		
		popUpPanel.add(menuInputPanel);
		RootPanel.get().add(popUpPanel);
		RootPanel.get().setWidgetPosition(popUpPanel, 5, imgIcon.getAbsoluteTop() + imgIcon.getOffsetHeight() + 4);
		
		menuInputPanel.add(lblCreateItinerary);
		menuInputPanel.setStyleName("menuInputPanel");
		
		imgIcon.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if (menuInputPanel.isVisible() == false) {
					menuShow();
				} else {
					menuHide();
				}
			}
		});
		
		lblCreateItinerary.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				menuHide();
				
				createItinerary = CreateItinerary.getPage();
				createItinerary.panelsMultiDayView();
			}
		});
		
		popUpPanel.addCloseHandler(new CloseHandler<PopupPanel>() {
			
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				menuHide();
			}
		});
		
		popUpPanel.addAutoHidePartner(imgIcon.getElement());
		
		menuHide();

	}

	protected void menuHide() {
		imgIcon.setStyleName("menuNormal");
		menuInputPanel.setVisible(false);
		popUpPanel.setVisible(false);
	}

	protected void menuShow() {
		menuInputPanel.setVisible(true);	
		imgIcon.setStyleName("menuNormal:hover");
		popUpPanel.setVisible(true);
		popUpPanel.show();
		RootPanel.get().setWidgetPosition(popUpPanel, 5, imgIcon.getAbsoluteTop() + imgIcon.getOffsetHeight() + 4);
	}

	public static void clearContent() {
		
	}
}
