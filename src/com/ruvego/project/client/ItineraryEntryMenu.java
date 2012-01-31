package com.ruvego.project.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ItineraryEntryMenu {
	
	private PopupPanel popUpPanel;
	
	private VerticalPanel menuInputPanel;
	
	protected Label btnMore;

	private AbsolutePanel boxResultPanel;
	
	private HorizontalPanel movePanel;
	
	protected int entryPosition = 0;
	
	private DayActivityPlan dayActivityPlan;
	
	private Label lblMoveText;
	
	public ItineraryEntryMenu(AbsolutePanel panel, int posVertical, int entryNum, DayActivityPlan temp) {
		dayActivityPlan = temp;
		boxResultPanel = panel;
		entryPosition = entryNum + 1;
		
		popUpPanel = new PopupPanel(true, true);
		popUpPanel.setStyleName("itineraryEntryPopUpPanel");
		menuInputPanel = new VerticalPanel();
		menuInputPanel.setWidth("250px");
		menuInputPanel.setSpacing(4);
		
		btnMore = new Label("More", true);
		btnMore.setStyleName("boxBtnMore");
		boxResultPanel.add(btnMore, boxResultPanel.getOffsetWidth() - 60, posVertical);
		
		Label lblCreateItinerary = new Label("Create a new Itinerary");
		lblCreateItinerary.setStyleName("menuItemText");
		lblCreateItinerary.setWidth("100%");
		
		popUpPanel.add(menuInputPanel);
		RootPanel.get().add(popUpPanel);
		RootPanel.get().setWidgetPosition(popUpPanel, btnMore.getAbsoluteLeft(), btnMore.getAbsoluteTop() - popUpPanel.getOffsetHeight());
		
		menuInputPanel.add(lblCreateItinerary);
		menuInputPanel.setStyleName("menuInputPanel");
		
		movePanel = new HorizontalPanel();
		
		lblMoveText = new Label("Move entry from " + entryPosition + " to ");
		lblMoveText.setStyleName("menuItemTextNormal");
		
		final TextBox txtBoxMove = new TextBox();
		txtBoxMove.setPixelSize(18, 15);
		movePanel.add(lblMoveText);
		movePanel.add(txtBoxMove);
		movePanel.setSpacing(5);
		
		Label btnMove = new Label("Move");
		btnMove.setStyleName("boxBtnMove");
		movePanel.add(btnMove);
		
		btnMove.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				menuHide();
				
				/* Adding -1 beacuse the arrays start from 0. But GUI display starts from 1 */
				dayActivityPlan.reorganizePositions(entryPosition - 1, Integer.parseInt(txtBoxMove.getText()) - 1);
			}
		});
		
		menuInputPanel.add(movePanel);
		
		btnMore.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if (menuInputPanel.isVisible() == false) {
					txtBoxMove.setText("");
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
				
			}
		});
		
		popUpPanel.addCloseHandler(new CloseHandler<PopupPanel>() {
			
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				menuHide();
			}
		});
		
		popUpPanel.addAutoHidePartner(btnMore.getElement());
		
		menuHide();

		
	}
	
	
	protected void updateEntryPosition(int newPos) {
		entryPosition = newPos;
		lblMoveText.setText("Move entry from " + entryPosition + " to ");
	}
	
	protected void menuHide() {
		btnMore.setStyleName("boxBtnMore");
		menuInputPanel.setVisible(false);
		popUpPanel.setVisible(false);
	}

	protected void menuShow() {
		menuInputPanel.setVisible(true);	
		btnMore.setStyleName("boxBtnMoreClick");
		popUpPanel.setVisible(true);
		popUpPanel.show();
		RootPanel.get().setWidgetPosition(popUpPanel, btnMore.getAbsoluteLeft(), btnMore.getAbsoluteTop() - popUpPanel.getOffsetHeight());
	}


}
