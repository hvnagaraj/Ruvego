package com.ruvego.project.client;

import java.util.ArrayList;
import java.util.List;

import javax.swing.plaf.ProgressBarUI;

import sun.net.ProgressMonitor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.ruvego.project.client.ResultsDataGridView.DataGridResources;
import com.ruvego.project.client.ResultsDataGridView.ResultsColumnData;

public abstract class SortableDataGrid<T> {
	protected DataGrid<T> dataGrid;
	private String height;
	private String width;
	private ListDataProvider<T> dataProvider;
	private List<T> dataList;
	ListHandler<T> sortHandler;
	protected Column<T, String> column1;
	protected Column<T, String> column2;
	protected Column<T, String> column3;
	protected Column<T, String> column4;
	protected Column<T, String> column5;
	protected Column<T, String> column6;
	protected Column<T, String> column7;
	
	public SortableDataGrid(AbsolutePanel activityResultsPanel, ResultsPacket[] result, int numCols) {
		dataGrid = new DataGrid<T>(100, GWT.<DataGridResources>create(DataGridResources .class));

		dataProvider = new ListDataProvider<T>();
		dataProvider.setList(new ArrayList<T>());
		dataGrid.setEmptyTableWidget(new HTML("No Data to Display"));
		dataGrid.setVisible(true);
		activityResultsPanel.add(dataGrid, 0, 8);
		dataGrid.setSize("100%", "195px");
		
		sortHandler = new ListHandler<T>(dataProvider.getList());
		initTableColumns(dataGrid, sortHandler, result, numCols);
		setDataGridColumnWidth(numCols);
		
		dataGrid.addColumnSortHandler(sortHandler);
		dataProvider.addDataDisplay(dataGrid);
	}

	public void setEmptyTableWidget() {

		dataGrid.setEmptyTableWidget(new HTML(
				"The current request has taken longer than the allowed time limit. Please try your report query again."));
	}
	/**
	 *
	 * Abstract Method to implements for adding Column into Grid
	 *
	 * @param dataGrid
	 * @param sortHandler
	 * @param numCols 
	 * @param result 
	 */
	public abstract void initTableColumns(DataGrid<T> dataGrid,  ListHandler<T> sortHandler, ResultsPacket[] result, int numCols);
	
	public String getHeight() {
		return height;
	}
	
	public void setHeight(String height) {
		this.height = height;
		dataGrid.setHeight(height);
	}
	
	public void setWidth(String varWidth) {
		this.width = varWidth;
		dataGrid.setHeight(width);
	}
	
	public List<T> getDataList() {
		return dataList;
	}
	
	public void setDataList(List<T> dataList) {
		this.dataList = dataList;
		List<T> list = dataProvider.getList();
		list.clear();
		list.addAll(this.dataList);
		dataProvider.refresh();
	}

	public ListDataProvider<T> getDataProvider() {
		return dataProvider;
	}
	
	public void setDataProvider(ListDataProvider<T> dataProvider) {
		this.dataProvider = dataProvider;
	}
	
	public void setColumns(int numCols, ResultsPacket[] result) {
		if (this.dataGrid.getColumnCount() > numCols) {
			System.out.println("Client: Present cols - " + this.dataGrid.getColumnCount() + " Deleting " + (this.dataGrid.getColumnCount() - numCols) + " to the Grid");
			for (int i = this.dataGrid.getColumnCount() - 1; i >= numCols; i--) {
				dataGrid.removeColumn(i);
			}
		} else if (this.dataGrid.getColumnCount() < numCols) {
			System.out.println("Client: Present cols - " + this.dataGrid.getColumnCount() + " Adding " + (numCols - this.dataGrid.getColumnCount()) + " to the Grid");
			addTableColumns(dataGrid, this.sortHandler, result, this.dataGrid.getColumnCount(), numCols);
		} else {
			System.out.println("Client: No of Columns unchanged");
		}
		
		setDataGridColumnWidth(numCols);
		
		System.out.println("Client: Total cols - " + this.dataGrid.getColumnCount());
	}

	private void setDataGridColumnWidth(int numCols) {
		for (int i = 1; i <= numCols; i++) {
			switch (i) {
			case 1:
				dataGrid.setColumnWidth(column1, "250px");
				break;
			case 2:
				if (numCols == 4) {
					dataGrid.setColumnWidth(column2, "100%");
				} else {
					dataGrid.setColumnWidth(column2, "80px");
				}
				break;
			case 3:
				if (numCols == 4) {
					dataGrid.setColumnWidth(column3, "100%");
				} else {
					dataGrid.setColumnWidth(column3, "120px");
				}
				break;
			case 4:
				if (numCols == 4) {
					dataGrid.setColumnWidth(column4, "100%");
				} else {
					dataGrid.setColumnWidth(column4, "60px");
				}
				break;
			case 5:
				dataGrid.setColumnWidth(column5, "100%");
				break;
			case 6:
				dataGrid.setColumnWidth(column6, "100%");
				break;
			case 7:
				dataGrid.setColumnWidth(column7, "100%");
				break;
			}
		}
		dataGrid.setWidth("100%");
	}

	public abstract void addTableColumns(DataGrid<T> dataGrid2, ListHandler<T> sortHandler2, ResultsPacket[] result, int presentCols, int numCols);

}


