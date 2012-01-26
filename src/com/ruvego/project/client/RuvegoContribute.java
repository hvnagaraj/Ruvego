package com.ruvego.project.client;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;

public class RuvegoContribute {

	final static private int ACTIVITY = 0;
	final static private int CATEGORY = 1;
	final static private int PLACE = 2;

	private static RuvegoContribute page;

	private static ContributeAddPlace addPlace;
	private static ContributeAddActivity addActivity;
	private static ContributeAddCategory addCategory;

	static protected AbsolutePanel contributePanel;

	static private AsyncCallback<Boolean> callbackWrite;

	static private ListBox listBox;



	public static AbsolutePanel getContributePanel() {
		return RuvegoContribute.contributePanel;
	}

	public static ListBox getTypeListbox() {
		return RuvegoContribute.listBox;
	}

	public static AsyncCallback<Boolean> getWriteCallback() {
		return RuvegoContribute.callbackWrite;
	}


	private RuvegoContribute() {
		contributePanel = new AbsolutePanel();
		Ruvego.getRootPanel().add(contributePanel, (Window.getClientWidth() - 1000)/2, Ruvego.getOtherWidgetTop());

		listBox = new ListBox();
		listBox.addItem("Activity");
		listBox.addItem("Category");
		listBox.addItem("Place");
		listBox.setStyleName("blackText");
		contributePanel.add(listBox, 40, 14);
		listBox.setVisibleItemCount(1);

		listBox.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {

				switch (listBox.getSelectedIndex()) {
				case ACTIVITY:
					setupAddActivity();
					break;
				case CATEGORY:
					setupAddCategory();
					break;
				case PLACE:
					setupAddPlace();
					break;
				}
			}
		});

		Window.addResizeHandler(new ResizeHandler() {

			public void onResize(ResizeEvent event) {
				if (History.getToken().equalsIgnoreCase("contributePage")) {
					panelResizeAlignments();
				}
			}
		});

		contributePanel.setStyleName("contributePanelBG");

		callbackWrite = new AsyncCallback<Boolean>() {

			public void onFailure(Throwable caught) {
				System.err.println("Call Back Write has incurred an error");
			}

			public void onSuccess(Boolean result) {
			}
		};

		setupAddActivity();
		panelAlignments();
	}

	protected static void panelResizeAlignments() {
		int width = Ruvego.getClientWidth();
		
		contributePanel.setPixelSize(1000 - Ruvego.getIndent(), ContributeAddActivity.getPanel().getAbsoluteTop()
				+ ContributeAddActivity.getPanel().getOffsetHeight()
				- contributePanel.getAbsoluteTop());
		Ruvego.rootPanel.setWidgetPosition(contributePanel, Ruvego.getIndent() + (width - Ruvego.getIndent() - contributePanel.getOffsetWidth())/2, 
				Ruvego.getOtherWidgetTop());
	}

	protected void setupAddPlace() {
		addPlace = ContributeAddPlace.getPage();
		addPlace.panelsView();
		ContributeAddActivity.clearContent();
		ContributeAddCategory.clearContent();
	}

	protected void setupAddCategory() {
		addCategory = ContributeAddCategory.getPage();
		addCategory.panelsView();
		ContributeAddActivity.clearContent();
		ContributeAddPlace.clearContent();
	}

	protected void setupAddActivity() {
		addActivity = ContributeAddActivity.getPage();
		addActivity.panelsView();
		ContributeAddPlace.clearContent();
		ContributeAddCategory.clearContent();
	}

	static protected void panelAlignments() {
		panelResizeAlignments();
		Ruvego.setMinimumPageHeight(contributePanel.getOffsetHeight() + Ruvego.getFooterHeight() + Ruvego.getOtherWidgetTop());
		Ruvego.panelAlignments();
	}

	/* Singleton implementation */
	public static RuvegoContribute getPage() {
		if (page == null) {
			page = new RuvegoContribute();
		}
		return page;
	}

	public void panelsView() {
		contributePanel.setVisible(true);
		listBox.setSelectedIndex(0);
		setupAddActivity();
		panelAlignments();
	}
	
	public void clearContent() {
		contributePanel.setVisible(false);
	}
	
	static String toCamelCase(String s) {
		if (s.equalsIgnoreCase("")) {
			return "";
		}
		String[] parts = s.split(" ");
		String camelCaseString = "";
		
		int i = 0;
		for (String part : parts) {
			if (i != 0) {
				camelCaseString = camelCaseString + " ";
			}
			camelCaseString = camelCaseString + toProperCase(part);
			i++;
		}
		return camelCaseString;
	}
	
	static String toProperCase(String s) {
	    return s.substring(0, 1).toUpperCase() +
	               s.substring(1).toLowerCase();
	}
}

