package com.ruvego.project.client;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.ruvego.project.client.ResultsDataGridView.ResultsColumnData;
import com.google.gwt.maps.client.InfoWindow;
import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.geocode.Geocoder;
import com.google.gwt.maps.client.geocode.LatLngCallback;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.Marker;

public class ObjectSortableDataGrid<T> extends SortableDataGrid<T> {
	
	private SingleSelectionModel<T> selectionModel;
	
	ResultsFetchAsync resultsFetchService = GWT.create(ResultsFetch.class);

	public ObjectSortableDataGrid(AbsolutePanel activityResultsPanel, ResultsPacket[] result, int numCols) {
		super(activityResultsPanel, result, numCols);
	}

	@Override
	public void initTableColumns(final DataGrid<T> dataGrid,
			ListHandler<T> sortHandler, ResultsPacket[] result, int numCols) {
		for (int i = 1; i <= 4; i++) {
			switch (i) {
			case 4:
				column4 = new Column<T, String>(
						new TextCell()) {
					@Override
					public String getValue(T object) {
						return ((ResultsColumnData) object).getCol4();
					}
				};

				column4.setSortable(true);

				sortHandler.setComparator(column4, new Comparator<T>() {
					public int compare(T o1, T o2) {
						return ((ResultsColumnData) o1).getCol4().toLowerCase().compareTo(
								((ResultsColumnData) o2).getCol4().toLowerCase());
					}
				});

				dataGrid.addColumn(column4, result[0].getColumnData(i - 1));
				break;
			case 3:
				column3 = new Column<T, String>(
						new TextCell()) {
					@Override
					public String getValue(T object) {
						return ((ResultsColumnData) object).getCol3();
					}
				};

				column3.setSortable(true);

				sortHandler.setComparator(column3, new Comparator<T>() {
					public int compare(T o1, T o2) {
						return ((ResultsColumnData) o1).getCol3().toLowerCase().compareTo(
								((ResultsColumnData) o2).getCol3().toLowerCase());
					}
				});

				dataGrid.addColumn(column3, result[0].getColumnData(i - 1));
				break;
			case 2:
				column2 = new Column<T, String>(
						new TextCell()) {
					@Override
					public String getValue(T object) {
						return ((ResultsColumnData) object).getCol2();
					}
				};

				column2.setSortable(true);

				sortHandler.setComparator(column2, new Comparator<T>() {
					public int compare(T o1, T o2) {
						return ((ResultsColumnData) o1).getCol2().toLowerCase().compareTo(
								((ResultsColumnData) o2).getCol2().toLowerCase());
					}
				});

				dataGrid.addColumn(column2, result[0].getColumnData(i - 1));
				break;
			case 1:
				column1 = new Column<T, String>(
						new TextCell()) {
					@Override
					public String getValue(T object) {
						return ((ResultsColumnData) object).getCol1();
					}
				};

				column1.setSortable(true);

				sortHandler.setComparator(column1, new Comparator<T>() {
					public int compare(T o1, T o2) {
						return ((ResultsColumnData) o1).getCol1().toLowerCase().compareTo(
								((ResultsColumnData) o2).getCol1().toLowerCase());
					}
				});

				dataGrid.addColumn(column1, result[0].getColumnData(i - 1));
				break;
				
			default:
				System.err.println("Something is wrong");
				break;
			}
		}
		
		final AsyncCallback<ResultsBriefPanelPacket> callbackBriefPanelResults = new AsyncCallback<ResultsBriefPanelPacket>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}

			public void onSuccess(final ResultsBriefPanelPacket result) {
				ResultsActivityMenu.menuHide();
				ResultsDataGridView.setHtmlName(result.getName(), result.getWebsite());
				ResultsDataGridView.htmlAddress.setHTML(result.getaddress());
				ResultsDataGridView.htmlBrief.setText(result.getBrief());
				ResultsDataGridView.setTimings(result.getTimings());
				ResultsDataGridView.setMiscinfo(result.getMiscinfo());
				ResultsDataGridView.contact.setHTML(result.getContact());
				ResultsDataGridView.rating = result.getRating();
				
				System.out.println("Image Path : " + result.getImagepath());
				ResultsDataGridView.imageResultsBrief.setUrl(result.getImagepath());
				
				Geocoder geocoder;
				geocoder = Ruvego.getGeocode();
				final InfoWindow info = Ruvego.getMapWidget().getInfoWindow();
				
				geocoder.getLatLng(result.getaddress(), new LatLngCallback() {

					@Override
					public void onFailure() {
						Window.alert("cannot geocode");
					}

					@Override
					public void onSuccess(LatLng point) {
						Ruvego.getMapWidget().clearOverlays();
						Ruvego.getMapWidget().setCenter(point, 13);
				        Marker marker = new Marker(point);
				        Ruvego.getMapWidget().addOverlay(marker);
				        info.open(marker, new InfoWindowContent(result.getaddress()));
					}
					
				});
				
				dataGrid.setSize("100%", "195px");
				
				ResultsDataGridView.resultsBriefPanel.setVisible(true);
				
				ResultsDataGridView.panelAlignments();
			}

		};


		selectionModel = new SingleSelectionModel<T>();
		dataGrid.setSelectionModel(selectionModel);

		selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			public void onSelectionChange(SelectionChangeEvent event) {
				ResultsColumnData selected = (ResultsColumnData) selectionModel.getSelectedObject();
				if (selected != null) {
					resultsFetchService.fetchBriefPanelResults(selected.getCol1(), callbackBriefPanelResults);
				}
			}
		});
		
		addTableColumns(dataGrid, sortHandler, result, 4, numCols);
	}
	
	public void addTableColumns(final DataGrid<T> dataGrid,
			ListHandler<T> sortHandler, ResultsPacket[] result, int presentCols, int numCols) {
		
		for (int i = 5; i <= numCols; i++) {
			switch (i) {

			case 7:
				column7 = new Column<T, String>(
						new TextCell()) {
					@Override
					public String getValue(T object) {
						return ((ResultsColumnData) object).getCol7();
					}
				};

				column7.setSortable(true);

				sortHandler.setComparator(column7, new Comparator<T>() {
					public int compare(T o1, T o2) {
						return ((ResultsColumnData) o1).getCol7().toLowerCase().compareTo(
								((ResultsColumnData) o2).getCol7().toLowerCase());
					}
				});

				dataGrid.addColumn(column7, result[0].getColumnData(i - 1));
				break;
			case 6:
				column6 = new Column<T, String>(
						new TextCell()) {
					@Override
					public String getValue(T object) {
						return ((ResultsColumnData) object).getCol6();
					}
				};

				column6.setSortable(true);

				sortHandler.setComparator(column6, new Comparator<T>() {
					public int compare(T o1, T o2) {
						return ((ResultsColumnData) o1).getCol6().toLowerCase().compareTo(
								((ResultsColumnData) o2).getCol6().toLowerCase());
					}
				});

				dataGrid.addColumn(column6, result[0].getColumnData(i - 1));
				break;
			case 5:
				column5 = new Column<T, String>(
						new TextCell()) {
					@Override
					public String getValue(T object) {
						return ((ResultsColumnData) object).getCol5();
					}
				};

				column5.setSortable(true);

				sortHandler.setComparator(column5, new Comparator<T>() {
					public int compare(T o1, T o2) {
						return ((ResultsColumnData) o1).getCol5().toLowerCase().compareTo(
								((ResultsColumnData) o2).getCol5().toLowerCase());
					}
				});

				dataGrid.addColumn(column5, result[0].getColumnData(i - 1));
				break;
			}
		}

	}

 }
