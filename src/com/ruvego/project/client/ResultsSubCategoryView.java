package com.ruvego.project.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;

public class ResultsSubCategoryView extends CategoryView {
	
	public ResultsSubCategoryView() {
	}
	
	protected void categoryResultsOnFailure() {
	}
	
	protected void noActivitiesErrorDisplay() {
	}
	
	protected void setupSubCategoryView(String subCategory) {
	}

	public void clearContent() {
		lblHier.setStyleName("lblHierNormal");
		lblHier.setVisible(false);
		lblHierDiv.setVisible(false);
		resultsPanel.setVisible(false);
	}

	@Override
	protected void fetchResults() {
		Ruvego.getResultsFetchAsync().fetchSubcategoryResults(Ruvego.getPlace(), Ruvego.getWithinRange(), Ruvego.getTimeoftheday(), callbackCategoryResults);
	}

	@Override
	protected void panelsView() {
		resultsPanel.setVisible(true);
		ResultsDataGridView.clearContent();
		lblHier.setVisible(false);
		lblHierDiv.setVisible(false);
	}

	@Override
	protected void lblHierStyling() {
		ResultsCategoryView.getPage().getLblHier().setStyleName("lblHierNormalActive");
	}

	@Override
	protected void lblClickHandler() {	
		ResultsCategoryView.getPage().getLblHier().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (lblHier.isVisible() == true) {
					ResultsCategoryView.getPage().getLblHier().setStyleName("lblHierNormal");
					panelsView();
				}
			}
		});
	}
	
}