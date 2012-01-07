package com.ruvego.project.client;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.Handler;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import java.util.Comparator;


public class ResultsDataGridView {
	
	private static ResultsDataGridView page;

	int NUM_COLS = 7;
	static private int IMAGE_INDENT;
	final static int IMAGE_WIDTH = 255;
	final static int IMAGE_HEIGHT = 158;
	final static int RESULTS_BRIEF_PANEL_WIDTH = 275;

	static private ObjectSortableDataGrid<ResultsColumnData> grid = null;	
	static private AbsolutePanel activityResultsPanel = new AbsolutePanel();
	static protected AbsolutePanel resultsBriefPanel = new AbsolutePanel();
	
	static private AbsolutePanel timingsPanel = new AbsolutePanel();
	static private AbsolutePanel infoPanel = new AbsolutePanel();
	static private Label btnMoreDetails;
	static private Label btnAddCart;

	static private AsyncCallback<ResultsPacket[]> activityResulstCallback;


	protected static HTML htmlName;
	
	public static void setHtmlName(String name, String hyperlink) {
		if (hyperlink.equalsIgnoreCase("")) {
			htmlName.setHTML(name);
		} else {
			htmlName.setHTML("<a href=" + hyperlink + " target=\"_blank\"><font color=\"silver\">" + name + "</font></a>");
		}
	}

	static HTML htmlAddress;
	static Image imageResultsBrief;
	static HTML htmlBrief;
	static protected HTML contact;

	static private HTML timings;
	public static void setTimings(String timings) {
		ResultsDataGridView.timings.setHTML(timings);
	}

	static private HTML info;
	public static void setMiscinfo(String miscinfo) {
		ResultsDataGridView.info.setHTML(miscinfo);		
	}


	public class ResultsColumnData {
		private String col1;
		private String col2;
		private String col3;
		private String col4;
		private String col5;
		private String col6;
		private String col7;

		public String getCol7() {
			return this.col7;
		}

		public String getCol6() {
			return this.col6;
		}

		public String getCol5() {
			return this.col5;
		}

		public String getCol4() {
			return this.col4;
		}

		public String getCol3() {
			return this.col3;
		}

		public String getCol2() {
			return this.col2;
		}

		public String getCol1() {
			return this.col1;
		}

		public ResultsColumnData(String col1, String col2, String col3, String col4, String col5, String col6, String col7) {
			this.col1 = col1;
			this.col2 = col2;
			this.col3 = col3;
			this.col4 = col4;
			this.col5 = col5;
			this.col6 = col6;
			this.col7 = col7;
		}
	}
	
	public static ResultsDataGridView getPage() {
		if (page == null) {
			page = new ResultsDataGridView();
		}
		return page;
	}


	private ResultsDataGridView() {
		System.out.println("Successfully created Datagrid View object");
		/* Activity Panel */
		Ruvego.getRootPanel().add(activityResultsPanel);
		activityResultsPanel.setVisible(false);
		activityResultsPanel.setStyleName("gridBackground");

		Window.addResizeHandler(new ResizeHandler() {

			public void onResize(ResizeEvent event) {
				activityResultsAlignments();
			}
		});

		setResultsBriefPanel();
		activityResultsAlignments();
	}



	interface DataGridResources extends DataGrid.Resources {
		@Source(value = { DataGrid.Style.DEFAULT_CSS, "DataGridStyle.css" })
		DataGrid.Style dataGridStyle();
	}

	private final List<ResultsColumnData> LIST = new LinkedList<ResultsDataGridView.ResultsColumnData>();
	
	private void setResultsBriefPanel() {
		Ruvego.getRootPanel().add(resultsBriefPanel);
		resultsBriefPanel.setSize("275px", "380px");
		resultsBriefPanel.setStyleName("resultsBriefPanel");

		htmlName = new HTML("<a href=\"http://www.google.com\" target=\"_blank\">Golden Gate Bridge</a>", true);
		htmlName.setStyleName("resultsDetailTitle");
		resultsBriefPanel.add(htmlName, 54, 10);
		htmlName.setWidth("220px");

		IMAGE_INDENT = (RESULTS_BRIEF_PANEL_WIDTH - IMAGE_WIDTH) / 2;
		imageResultsBrief = new Image("Images/images.jpg");
		imageResultsBrief.setPixelSize(IMAGE_WIDTH, IMAGE_HEIGHT);
		resultsBriefPanel.add(imageResultsBrief, 10, resultsBriefPanel.getAbsoluteTop() + timingsPanel.getOffsetHeight());

		htmlAddress = new HTML("San Francisco, <BR>California 94129", true);
		htmlAddress.setStyleName("address");
		resultsBriefPanel.add(htmlAddress, 54, htmlName.getOffsetHeight() + htmlName.getAbsoluteTop() - resultsBriefPanel.getAbsoluteTop() + 5);
		htmlAddress.setWidth("125px");

		Image imageMarker = new Image("Images/marker.png");
		resultsBriefPanel.add(imageMarker, 10, 10);
		imageMarker.setSize("32px", "48px");
		
		contact = new HTML("+1 (704) 724-4751"); 
		contact.setStyleName("address");
		resultsBriefPanel.add(contact, 0, htmlAddress.getAbsoluteTop() + htmlAddress.getOffsetHeight());

		resultsBriefPanel.add(infoPanel, 0, htmlAddress.getAbsoluteTop() + htmlAddress.getOffsetHeight());
		resultsBriefPanel.add(timingsPanel, 0, htmlAddress.getAbsoluteTop() + htmlAddress.getOffsetHeight());

		timingsPanel.setSize("340px", "100px");
		timingsPanel.setStyleName("timingsPanel");
		infoPanel.setStyleName("infoPanel");

		timings = new HTML("Mon - Fri : 10:30a - 10:30p<BR>Sat       : 10:30a - 10:30p<BR>Sun       : 10:30a - 10:30p");
		info = new HTML("Entry Fee : 0$<BR>Toll : 0$");

		timings.setStyleName("timings");
		info.setStyleName("info");

		timingsPanel.add(timings, 5, 0);
		infoPanel.add(info, 5, 0);

		timingsPanel.setHeight(timings.getOffsetHeight() + "px");
		infoPanel.setHeight(info.getOffsetHeight() + "px");

		htmlBrief = new HTML("", true);
		resultsBriefPanel.add(htmlBrief, 10, 350);
		htmlBrief.setStyleName("briefDesc");
		//htmlBrief.setWidth(RESULTS_BRIEF_PANEL_WIDTH - 20 + "px");
		htmlBrief.setText("The Golden Gate Bridge is a suspension bridge spanning the Golden Gate, the opening of the San Francisco Bay into the Pacific Ocean. As part of both U.S. Route 101 and California State Route 1, the structure links the city of San Francisco, on the northern tip of the San Francisco Peninsula, to Marin County.");

		btnMoreDetails = new Label("More details");
		btnMoreDetails.setStyleName("btnMoreDetails");
		resultsBriefPanel.add(btnMoreDetails, RESULTS_BRIEF_PANEL_WIDTH - 200, 
				resultsBriefPanel.getOffsetHeight() - Ruvego.getFooterHeight() - btnMoreDetails.getOffsetHeight());

		btnAddCart = new Label("Add to Box");
		btnAddCart.setStyleName("btnMoreDetails");
		btnMoreDetails.setWidth("87px");
		resultsBriefPanel.add(btnAddCart, 12, resultsBriefPanel.getOffsetHeight() - Ruvego.getFooterHeight() - btnAddCart.getOffsetHeight());
		
		btnAddCart.addClickHandler(new ClickHandler() {		
			@Override
			public void onClick(ClickEvent event) {
				Ruvego.incCount();
			}
		});
	}

	public void fetchActivityResults(String prevType, final String request) {

		if (activityResulstCallback == null) {
			System.out.println("Client: Initializing the Activity Results Callback");
			activityResulstCallback = new AsyncCallback<ResultsPacket[]>() {
				public void onFailure(Throwable caught) {
					System.out.println("Failure");
				}

				@Override
				public void onSuccess(ResultsPacket[] result) {
					System.out.println("Client: Loading Activity Results");
					int numCols = result[0].getNumCols();
					int numRows = result[0].getNumElem();

					prepareResults(result, numRows, numCols);
					/*
				DialogBox loadingDialog = new DialogBox();
				loadingDialog.setWidget(new HTML("Loading..."));
				loadingDialog.setGlassEnabled(true);
				loadingDialog.setVisible(true);
				loadingDialog.setText("Dialog");
				loadingDialog.center();
					 */

					if (grid == null) {
						grid = new ObjectSortableDataGrid<ResultsDataGridView.ResultsColumnData>(activityResultsPanel, result, numCols);
					} else {
						grid.setColumns(numCols, result);
					}

					grid.setDataList(LIST);

					resultsBriefPanel.setVisible(false);
					activityResultsPanel.setVisible(true);

					activityResultsAlignments();
				}

				private void prepareResults(ResultsPacket[] result, int numRows, int numCols) {
					String[] rowData = new String[numCols];

					System.out.println("Rows : " + numRows + " Cols : " + numCols);
					LIST.clear();
					for (int i = 1; i <= numRows; i++) {
						rowData = result[i].getColumnData();
						ResultsColumnData e = new ResultsColumnData(rowData[0], rowData[1], rowData[2], rowData[3], rowData[4], rowData[5], rowData[6]);
						LIST.add(e);
					}
				}
			};
		}


		/* Load the initial results from the server */
		/* Request server to send data */
		Ruvego.getResultsFetchAsync().fetchResults(Ruvego.getPlace(), Ruvego.getWithinRange(), Ruvego.getTimeoftheday(), prevType, request, activityResulstCallback);
	}
	
	public static void activityResultsAlignments() {
		int width, height;

		width = Ruvego.getClientWidth();
		height = Ruvego.getClientHeight();

		activityResultsPanel.setPixelSize(width - RESULTS_BRIEF_PANEL_WIDTH - Ruvego.getIndent() * 2, ResultsCategoryView.getAllResultsPanelHeight());
		resultsBriefPanel.setPixelSize(RESULTS_BRIEF_PANEL_WIDTH, Ruvego.getMapsPanel().getOffsetHeight());
		timingsPanel.setPixelSize(RESULTS_BRIEF_PANEL_WIDTH, timings.getOffsetHeight() + 10);
		infoPanel.setPixelSize(RESULTS_BRIEF_PANEL_WIDTH, info.getOffsetHeight() + 10);
		htmlBrief.setWidth((RESULTS_BRIEF_PANEL_WIDTH - 20) + "px");

		Ruvego.getRootPanel().setWidgetPosition(resultsBriefPanel, width - resultsBriefPanel.getOffsetWidth(), Ruvego.getOtherWidgetTop());
		
		activityResultsPanel.setPixelSize(width - RESULTS_BRIEF_PANEL_WIDTH - Ruvego.getIndent() * 2, ResultsCategoryView.getAllResultsPanelHeight());
		Ruvego.getRootPanel().setWidgetPosition(activityResultsPanel, Ruvego.getIndent(), height - Ruvego.getFooterHeight() -
				ResultsCategoryView.getAllResultsPanelHeight());

		resultsBriefPanel.setWidgetPosition(htmlAddress, 54, htmlName.getOffsetHeight() + htmlName.getAbsoluteTop() - resultsBriefPanel.getAbsoluteTop() + 5);

		resultsBriefPanel.setWidgetPosition(btnAddCart, 10, resultsBriefPanel.getOffsetHeight() - btnAddCart.getOffsetHeight() - 10);
		
		resultsBriefPanel.setWidgetPosition(btnMoreDetails, RESULTS_BRIEF_PANEL_WIDTH - btnMoreDetails.getOffsetWidth() - 10, 
				resultsBriefPanel.getOffsetHeight() - btnMoreDetails.getOffsetHeight() - 10);
		
		resultsBriefPanel.setWidgetPosition(contact, 54, htmlAddress.getAbsoluteTop() + htmlAddress.getOffsetHeight() - resultsBriefPanel.getAbsoluteTop() + 1);

		resultsBriefPanel.setWidgetPosition(timingsPanel, 0, contact.getAbsoluteTop() + contact.getOffsetHeight() - resultsBriefPanel.getAbsoluteTop() + 5);

		resultsBriefPanel.setWidgetPosition(imageResultsBrief, IMAGE_INDENT, timingsPanel.getAbsoluteTop() - resultsBriefPanel.getAbsoluteTop()
				+ timingsPanel.getOffsetHeight() + 10);
		
		resultsBriefPanel.setWidgetPosition(htmlBrief, IMAGE_INDENT, imageResultsBrief.getAbsoluteTop() - resultsBriefPanel.getAbsoluteTop() +
				IMAGE_HEIGHT + 8); 

		resultsBriefPanel.setWidgetPosition(infoPanel, 0, htmlBrief.getAbsoluteTop() - resultsBriefPanel.getAbsoluteTop() + 
				htmlBrief.getOffsetHeight() + 10);

		timingsPanel.setWidgetPosition(timings, 10, 5); 

		infoPanel.setWidgetPosition(info, 10, 5); 

	}

	public static void clearContent() {
		resultsBriefPanel.setVisible(false);
		activityResultsPanel.setVisible(false);
	}
}
