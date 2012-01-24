package com.ruvego.project.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ResultsCategoryView extends CategoryView {
	
	private static ResultsCategoryView page;
	
	private static ResultsSubCategoryView subCategoryResults = null;

	private ResultsCategoryView() {
	}
	
	public static ResultsCategoryView getPage() {
		if (page == null) {
			page = new ResultsCategoryView();
			page.setType("Category");
		}
		return page;
	}
	
	
	protected void categoryResultsOnFailure() {
		Ruvego.noContent.setVisible(true);
		Ruvego.noContent.setHTML("The systems are being upgraded for high performance. The systems will be back in a while. Sorry for the inconvenience");
		panelAlignments();
	}
	
	protected void noActivitiesErrorDisplay() {
		Ruvego.errorDisplay("No Activities Found. Change your search criteria and try again");
		resultsPanel.setVisible(false);
		Ruvego.errorDisplayAlignments();
	}
	
	protected void setupSubCategoryView(String subCategory) {
		if (subCategoryResults == null) {
			subCategoryResults = new ResultsSubCategoryView();
			subCategoryResults.setType("SubCategory");
		}
		
		subCategoryResults.fetchCategoryResults();
	}

	public void clearContent() {
		Ruvego.noContent.setVisible(false);
		lblHier.setVisible(false);
		lblHierDiv.setVisible(false);
		resultsPanel.setVisible(false);
		lblHier.setStyleName("lblHierNormal");
		clearSubCategoryAndDataGrid();
	}

	static void clearSubCategoryAndDataGrid() {
		if (subCategoryResults != null) {
			subCategoryResults.clearContent();
		}
		ResultsDataGridView.clearContent();
	}

	/** Call this when moving from higher state to lower state. The Panels are already there and they have the right results.
	 *  Just make them visible
	 */
	public void panelsView() {
		resultsPanel.setVisible(true);
		clearSubCategoryAndDataGrid();
	}

	@Override
	protected void fetchResults() {
		Ruvego.getResultsFetchAsync().fetchCategoryResults(Ruvego.getPlace(), Ruvego.getWithinRange(), 
				Ruvego.getTimeoftheday(), callbackCategoryResults);
	}

	@Override
	protected void lblHierStyling() {
	}

	@Override
	protected void lblClickHandler() {
		// TODO Auto-generated method stub
		
	}

	
}