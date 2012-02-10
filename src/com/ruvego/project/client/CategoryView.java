package com.ruvego.project.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public abstract class CategoryView {
	final static private int ACTIVITY_PANEL_FIXED_HEIGHT = 205;

	private String type;

	protected ScrollPanel resultsPanel;

	private Grid gridPanel;

	protected static ResultsDataGridView activityResults;

	private Image[] image;
	private Label[] imageCaption;

	protected Label lblHier;
	protected Label lblHierDiv;

	protected AsyncCallback<CategoryPacket> callbackCategoryResults;

	public Label getLblHier() {
		return lblHier;
	}

	public Label getLblHierDiv() {
		return lblHierDiv;
	}

	public static int getAllResultsPanelHeight() {
		return CategoryView.ACTIVITY_PANEL_FIXED_HEIGHT;
	}

	public CategoryView() { 
		System.out.println("Created object of type ResultsCategoryView");

		Window.addResizeHandler(new ResizeHandler() {

			public void onResize(ResizeEvent event) {
				if (History.getToken().equalsIgnoreCase("homePage")) {
					panelResizeAlignments();
				}
			}
		});

		/* Setup Category related panels */
		resultsPanel = new ScrollPanel();
		Ruvego.getRootPanel().add(resultsPanel);
		resultsPanel.setStyleName("categoryResutsPanel");

		setLblHier();
	}

	protected void panelResizeAlignments() {
		int width = Ruvego.getClientWidth();
		int height = Ruvego.getClientHeight();

		Ruvego.errorDisplayAlignments();
		resultsPanel.setPixelSize(width - Ruvego.getIndent(), ACTIVITY_PANEL_FIXED_HEIGHT);
		Ruvego.getRootPanel().setWidgetPosition(resultsPanel, Ruvego.getIndent(),
				height - Ruvego.getFooterHeight() - ACTIVITY_PANEL_FIXED_HEIGHT);
		Ruvego.getMapsPanel().setWidgetPosition(RuvegoHomePage.getLblHierPanel(), 0, resultsPanel.getAbsoluteTop() - Ruvego.getMapsPanel().getAbsoluteTop() -
					RuvegoHomePage.getLblHierPanel().getOffsetHeight());
	}

	private void setLblHier() {
		lblHierDiv = new Label(" / ");
		RuvegoHomePage.getLblHierPanel().add(lblHierDiv);
		lblHierDiv.setVisible(false);
		lblHierDiv.setStyleName("lblHierNormal");

		lblHier = new Label("Hier");
		RuvegoHomePage.getLblHierPanel().add(lblHier);

		lblHier.setVisible(false);
		lblHier.setStyleName("lblHierNormal");
	}

	public void fetchCategoryResults() {
		if (callbackCategoryResults == null) {
			callbackCategoryResults = new AsyncCallback<CategoryPacket>() {

				public void onFailure(Throwable caught) {
					categoryResultsOnFailure();
				}

				public void onSuccess(CategoryPacket result) {
					if (result.getNumElems() == 0) {
						noActivitiesErrorDisplay();
						return;
					} 

					RuvegoHomePage.getLblHier1().setVisible(true);
					RuvegoHomePage.getLblHierPanel().setVisible(true);
					RuvegoHomePage.getLblHier1().setText(Ruvego.getPlace());

					Ruvego.noContent.setVisible(false);
					resultsPanel.setVisible(true);

					int numOfRowElements = 0;
					/* This calculates the No of Category items to be displayed in a row based on the Window size */
					if (Window.getClientWidth() < 1000) {
						/* Minimum Width 1000 and 15 for the indent */
						numOfRowElements = (1000 - 15)/158;
					} else {
						numOfRowElements = Window.getClientWidth()/158;
					}

					int numRows = (result.getNumElems() / numOfRowElements) + 1;
					gridPanel = new Grid(numRows, numOfRowElements);

					resultsPanel.clear();
					image = new Image[result.getNumElems()];
					imageCaption = new Label[result.getNumElems()];
					for (int j = 0; j < numRows; j++) {
						int loop;

						if (j == numRows - 1) {
							loop = result.getNumElems() % numOfRowElements;
						} else {
							loop = numOfRowElements;
						}
						for (int i = 0; i < loop; i++) {
							image[i + numOfRowElements * j] = new Image(result.getImagePath(i));
							image[i + numOfRowElements * j].setLayoutData(result.getNext(i) + " ; " + result.getImageCaption(i) + " ; " + type);
							image[i + numOfRowElements * j].setStyleName("image");
							image[i + numOfRowElements * j].addClickHandler(new ClickHandler() {

								@Override
								public void onClick(ClickEvent event) {
									Image img = (Image) event.getSource();
									String inputLine = (String) img.getLayoutData();
									categoryOnClick(inputLine);
								}
							});

							imageCaption[i + numOfRowElements * j] = new Label(result.getImageCaption(i));
							imageCaption[i + numOfRowElements * j].setStyleName("imageCaption");
							imageCaption[i + numOfRowElements * j].setPixelSize(130, 12);
							imageCaption[i + numOfRowElements * j].setLayoutData(result.getNext(i) + " ; " + result.getImageCaption(i) + " ; " + type);

							imageCaption[i + numOfRowElements * j].addClickHandler(new ClickHandler() {

								@Override
								public void onClick(ClickEvent event) {
									Label lbl = (Label) event.getSource();
									String inputLine = (String) lbl.getLayoutData();
									categoryOnClick(inputLine);
								}
							});

							VerticalPanel imagePanel = new VerticalPanel();
							imagePanel.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);
							imagePanel.add(image[i + numOfRowElements * j]);
							imagePanel.add(imageCaption[i + numOfRowElements * j]);
							imagePanel.setWidth("132px");

							gridPanel.getCellFormatter().setWidth(j, i, imageCaption[i + numOfRowElements * j].getOffsetWidth() + "px");
							gridPanel.setWidget(j, i, imagePanel);					
						}
					}
					gridPanel.setCellPadding(11);
					resultsPanel.add(gridPanel);
					panelResizeAlignments();
				}
			};
		}

		ResultsCategoryView.clearSubCategoryAndDataGrid();
		lblHier.setVisible(false);
		lblHierDiv.setVisible(false);

		fetchResults();
	}

	protected abstract void noActivitiesErrorDisplay();

	protected abstract void categoryResultsOnFailure();

	protected abstract void fetchResults();

	protected abstract void panelsView();

	protected abstract void lblHierStyling();

	protected abstract void lblClickHandler();

	protected void setType(String type) {
		this.type = type;
	}

	protected void panelAlignments() {
		Ruvego.setMapsPosition(0, 0);
		Ruvego.setMinimumPageHeight(RuvegoHomePage.HOMEPAGE_PAGE_HEIGHT);
		Ruvego.panelAlignments();
		panelResizeAlignments();
	}

	private void categoryOnClick(String inputLine) {
		resultsPanel.setVisible(false);

		String delims = " ; ";
		String[] tokens = inputLine.split(delims);

		System.out.println(inputLine);

		System.out.println("Token 0 : " + tokens[0]);
		System.out.println("Token 1 : " + tokens[1]);
		System.out.println("Token 2 : " + tokens[2]);

		RuvegoHomePage.getLblHier1().setStyleName("lblHierNormalActive");
		System.out.println("Setting styling of lbl hier1");
		lblHier.setText(tokens[1]);
		lblHier.setVisible(true);
		lblHierDiv.setVisible(true);
		resultsPanel.setVisible(false);

		if (tokens[0].equals("Activity")) {
			lblHierStyling();
			lblClickHandler();
			activityResults = ResultsDataGridView.getPage();
			System.out.println("Request Type : " + tokens[1]);
			activityResults.fetchActivityResults(type, tokens[1]);
		} else if (tokens[0].equals("SubCategory")) {
			setupSubCategoryView(tokens[1]);
			System.out.println("Inside : " + this.type);
		} else {
			System.err.println("Something is wrong");
		}

	}

	protected abstract void setupSubCategoryView(String subCategory);

}
