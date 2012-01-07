package com.ruvego.project.client;

import java.awt.Scrollbar;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

public class Results {
	int STATE = 0;
	
	int PREVIOUS_ROW_SELECTED = -1;
	
	public Results(AbsolutePanel p_absPanel, String type) {
		create_panel(p_absPanel, type);
	}
	/**
	 * @wbp.parser.entryPoint
	 */
	ScrollPanel scrollPanel = new ScrollPanel();
	FlexTable flexTableHeader = new FlexTable();
	FlexTable flexTable = new FlexTable();
	
	public void onModuleLoad(AbsolutePanel p_absPanel, String type) {
		RootPanel rootPanel = RootPanel.get();
		rootPanel.setSize("100%", "100%");
		
		//AbsolutePanel absolutePanel = p_absPanel;
		AbsolutePanel absolutePanel = new AbsolutePanel();
		//absolutePanel.setStyleName("flex_table");
		rootPanel.add(absolutePanel, 10, 10);
		//absolutePanel.setSize("100%", "289px");
		
		Label lblHier1 = new Label("Hier 1");
		absolutePanel.add(lblHier1, 5, 0);
		lblHier1.setText(type);
		
		Label lblHier2 = new Label("> Hier 2");
		absolutePanel.add(lblHier2, lblHier1.getAbsoluteLeft() - absolutePanel.getAbsoluteLeft() + 
					lblHier1.getOffsetWidth() + 5, 0);
		
		Label lblHier3 = new Label("> Hier 3");
		absolutePanel.add(lblHier3, lblHier2.getAbsoluteLeft() - absolutePanel.getAbsoluteLeft() + 
				lblHier1.getOffsetWidth() + 5, 0);
		
		absolutePanel.add(scrollPanel, 0, 72);
		scrollPanel.setSize("100%", "95px");
		flexTable.setStyleName("flex_table");		
		
		scrollPanel.setWidget(flexTable);
		flexTable.setSize("100%", "25px");

		
		Label label = new Label("Place");
		label.setStyleName("lbl_results_letters_disp");
		flexTable.setWidget(0, 0, label);
		label.setSize("100%", "15px");
		
		Label label_1 = new Label("Location");
		label_1.setStyleName("lbl_results_letters_disp");
		flexTable.setWidget(0, 1, label_1);
		label_1.setWidth("100%");
		
		Label label_2 = new Label("Distance");
		label_2.setStyleName("lbl_results_letters_disp");
		flexTable.setWidget(0, 2, label_2);
		label_2.setWidth("100%");
		
		Label label_3 = new Label("Rating");
		label_3.setStyleName("lbl_results_letters_disp");
		flexTable.setWidget(0, 3, label_3);
		label_3.setWidth("100%");
		
		Label label_4 = new Label("Place");
		label_4.setStyleName("lbl_results_dark_letters_disp");
		flexTable.setWidget(1, 0, label_4);
		label_4.setWidth("100%");
		
		Label label_9 = new Label("Location");
		label_9.setStyleName("lbl_results_dark_letters_disp");
		flexTable.setWidget(1, 1, label_9);
		label_9.setWidth("100%");
		
		Label label_14 = new Label("Distance");
		label_14.setStyleName("lbl_results_dark_letters_disp");
		flexTable.setWidget(1, 2, label_14);
		label_14.setWidth("100%");
		
		Label label_19 = new Label("Rating");
		label_19.setStyleName("lbl_results_dark_letters_disp");
		flexTable.setWidget(1, 3, label_19);
		label_19.setWidth("100%");
		
		Label label_5 = new Label("Place");
		label_5.setStyleName("lbl_results_letters_disp");
		flexTable.setWidget(2, 0, label_5);
		label_5.setWidth("100%");
		
		Label label_10 = new Label("Location");
		label_10.setStyleName("lbl_results_letters_disp");
		flexTable.setWidget(2, 1, label_10);
		label_10.setWidth("100%");
		
		Label label_15 = new Label("Distance");
		label_15.setStyleName("lbl_results_letters_disp");
		flexTable.setWidget(2, 2, label_15);
		label_15.setWidth("100%");
		
		Label label_20 = new Label("Rating");
		label_20.setStyleName("lbl_results_letters_disp");
		flexTable.setWidget(2, 3, label_20);
		label_20.setWidth("100%");
		
		Label label_6 = new Label("Place");
		label_6.setStyleName("lbl_results_dark_letters_disp");
		flexTable.setWidget(3, 0, label_6);
		label_6.setWidth("100%");
		
		Label label_11 = new Label("Location");
		label_11.setStyleName("lbl_results_dark_letters_disp");
		flexTable.setWidget(3, 1, label_11);
		label_11.setSize("100%", "100%");
		
		Label label_16 = new Label("Distance");
		label_16.setStyleName("lbl_results_dark_letters_disp");
		flexTable.setWidget(3, 2, label_16);
		label_16.setSize("100%", "100%");
		
		Label label_21 = new Label("Rating");
		label_21.setStyleName("lbl_results_dark_letters_disp");
		flexTable.setWidget(3, 3, label_21);
		label_21.setSize("100%", "100%");
		
		Label label_7 = new Label("Place");
		label_7.setStyleName("lbl_results_letters_disp");
		flexTable.setWidget(4, 0, label_7);
		label_7.setSize("100%", "100%");
		
		Label label_12 = new Label("Location");
		label_12.setStyleName("lbl_results_letters_disp");
		flexTable.setWidget(4, 1, label_12);
		label_12.setSize("100%", "100%");
		
		Label label_17 = new Label("Distance");
		label_17.setStyleName("lbl_results_letters_disp");
		flexTable.setWidget(4, 2, label_17);
		label_17.setSize("100%", "100%");
		
		Label label_22 = new Label("Rating");
		label_22.setStyleName("lbl_results_letters_disp");
		flexTable.setWidget(4, 3, label_22);
		label_22.setSize("100%", "100%");
		
		Label label_8 = new Label("Place");
		label_8.setStyleName("lbl_results_dark_letters_disp");
		flexTable.setWidget(5, 0, label_8);
		label_8.setSize("100%", "100%");
		
		Label label_13 = new Label("Location");
		label_13.setStyleName("lbl_results_dark_letters_disp");
		flexTable.setWidget(5, 1, label_13);
		label_13.setSize("100%", "100%");
		
		Label label_18 = new Label("Distance");
		label_18.setStyleName("lbl_results_dark_letters_disp");
		flexTable.setWidget(5, 2, label_18);
		label_18.setSize("100%", "100%");
		
		Label label_23 = new Label("Rating");
		label_23.setStyleName("lbl_results_dark_letters_disp");
		flexTable.setWidget(5, 3, label_23);
		label_23.setSize("100%", "100%");
		
		flexTableHeader.setStyleName("flex_table");
		absolutePanel.add(flexTableHeader, 0, 16);
		flexTableHeader.setSize(flexTable.getOffsetWidth() + "px", "25px");
		
		Label lblPlace = new Label("Place");
		flexTableHeader.setWidget(0, 0, lblPlace);
		lblPlace.setStyleName("lbl_header");
		lblPlace.setSize("100%", "16px");
		
		Label lblLocation = new Label("Location");
		flexTableHeader.setWidget(0, 1, lblLocation);
		lblLocation.setStyleName("lbl_header");
		lblLocation.setSize("100%", "16px");
		
		Label lblDistance = new Label("Distance");
		flexTableHeader.setWidget(0, 2, lblDistance);
		lblDistance.setStyleName("lbl_header");
		lblDistance.setSize("100%", "16px");
		
		Label lblRating = new Label("Rating");
		flexTableHeader.setWidget(0, 3, lblRating);
		lblRating.setStyleName("lbl_header");
		lblRating.setSize("100%", "16px");
		

		/* Constant throughout */
		absolutePanel.setWidgetPosition(flexTableHeader, 0, 17);
		absolutePanel.setWidgetPosition(scrollPanel, 0, flexTableHeader.getOffsetHeight() + 17);
		
		scrollPanel.setSize("100%", (absolutePanel.getOffsetHeight() - (scrollPanel.getAbsoluteTop() - lblHier1.getAbsoluteTop())) + "px");
		
		flexTableHeader.getCellFormatter().setStyleName(0, 0, "cell_dark_grey");
		flexTableHeader.getCellFormatter().setStyleName(0, 1, "cell_dark_grey");
		flexTableHeader.getCellFormatter().setStyleName(0, 2, "cell_dark_grey");
		flexTableHeader.getCellFormatter().setStyleName(0, 3, "cell_dark_grey");
		
		flexTableHeader.getCellFormatter().setWidth(0, 0, flexTableHeader.getOffsetWidth()/4 + "px");
		flexTableHeader.getCellFormatter().setWidth(0, 1, flexTableHeader.getOffsetWidth()/4 + "px");
		flexTableHeader.getCellFormatter().setWidth(0, 2, flexTableHeader.getOffsetWidth()/4 + "px");
		flexTableHeader.getCellFormatter().setWidth(0, 3, (flexTableHeader.getOffsetWidth()/4 + 15) + "px");
		
		flexTable.getCellFormatter().setWidth(0, 0, flexTable.getOffsetWidth()/4 + "px");
		flexTable.getCellFormatter().setWidth(0, 1, flexTable.getOffsetWidth()/4 + "px");
		flexTable.getCellFormatter().setWidth(0, 2, flexTable.getOffsetWidth()/4 + "px");
		flexTable.getCellFormatter().setWidth(0, 3, flexTable.getOffsetWidth()/4 + "px");
		
		
		
		flexTableHeader.getCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_MIDDLE);
		flexTableHeader.getCellFormatter().setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_MIDDLE);
		flexTableHeader.getCellFormatter().setVerticalAlignment(0, 2, HasVerticalAlignment.ALIGN_MIDDLE);
		flexTableHeader.getCellFormatter().setVerticalAlignment(0, 3, HasVerticalAlignment.ALIGN_MIDDLE);

	}
	
	public void create_panel(AbsolutePanel p_absPanel, String type) {
		final AbsolutePanel absolutePanel = p_absPanel;
		
		final Label lblHier1 = new Label("Hier 1");
		absolutePanel.add(lblHier1, 5, 0);
		lblHier1.setText(type);
		lblHier1.setStyleName("lbl_hier_normal");
		
		final Label lblHier2 = new Label("> Hier 2");
		lblHier2.setStyleName("lbl_hier_normal");
		absolutePanel.add(lblHier2, lblHier1.getAbsoluteLeft() - absolutePanel.getAbsoluteLeft() + 
				lblHier1.getOffsetWidth() + 5, 0);
		lblHier2.setVisible(false);
	
		
		final Label lblHier3 = new Label("> Hier 3");
		lblHier3.setStyleName("lbl_hier_final");
		absolutePanel.add(lblHier3, lblHier2.getAbsoluteLeft() - absolutePanel.getAbsoluteLeft() + 
				lblHier1.getOffsetWidth() + 5, 0);
		lblHier3.setVisible(false);
		
		absolutePanel.add(scrollPanel, 0, 72);
		scrollPanel.setSize("100%", "95px");
		flexTable.setStyleName("flex_table");		
		
		scrollPanel.setWidget(flexTable);
		flexTable.setSize("100%", "25px");

		flexTableHeader.setStyleName("flex_table");
		absolutePanel.add(flexTableHeader, 0, 16);
		flexTableHeader.setSize(flexTable.getOffsetWidth() + "px", "25px");
		
		
		
		form_results_table(0, 6);
		
		lblHier1.addMouseOutHandler(new MouseOutHandler() {
			
			@Override
			public void onMouseOut(MouseOutEvent event) {
				// TODO Auto-generated method stub
				lblHier1.setStyleName("lbl_hier_normal");
			}
		});
		
		lblHier1.addMouseOverHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
				// TODO Auto-generated method stub
				lblHier1.setStyleName("lbl_hier_mouse_over");
			}
		});
		
		lblHier1.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				STATE = 0;
				lblHier2.setVisible(false);
				lblHier3.setVisible(false);
				populate_results();
			}
		});
		
		lblHier2.addMouseOutHandler(new MouseOutHandler() {
			
			@Override
			public void onMouseOut(MouseOutEvent event) {
				// TODO Auto-generated method stub
				lblHier2.setStyleName("lbl_hier_normal");
			}
		});
		
		lblHier2.addMouseOverHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
				lblHier2.setStyleName("lbl_hier_mouse_over");
			}
		});
		
		lblHier2.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				STATE = 1;
				lblHier3.setVisible(false);
				populate_results();
			}
		});
		
		flexTable.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				int row;
				
				if (STATE == 0) {
					lblHier2.setVisible(true);
					lblHier2.setText("> Sight Seeing");
					absolutePanel.setWidgetPosition(lblHier2, lblHier1.getAbsoluteLeft() - absolutePanel.getAbsoluteLeft() + 
							lblHier1.getOffsetWidth() + 5, 0);
					STATE++;
				} else if (STATE == 1) {
					lblHier3.setVisible(true);
					lblHier3.setText("> Golden Gate Bridge");
					absolutePanel.setWidgetPosition(lblHier3, lblHier2.getAbsoluteLeft() - absolutePanel.getAbsoluteLeft() + 
							lblHier2.getOffsetWidth() + 5, 0);
					
					row = flexTable.getCellForEvent(event).getRowIndex();
					selected_row_visual_change(row, PREVIOUS_ROW_SELECTED);
					PREVIOUS_ROW_SELECTED = row;					 
					STATE++;
					return;
				} else {
					row = flexTable.getCellForEvent(event).getRowIndex();
					if (row != PREVIOUS_ROW_SELECTED) {
						selected_row_visual_change(row, PREVIOUS_ROW_SELECTED);
						PREVIOUS_ROW_SELECTED = row;
					}
					return;
				}
				// TODO Have to set focus to the filter criteria element
				//populate_results();
				
			}

			private void selected_row_visual_change(int selected_row, int prev_selected_row) {
				Label lbl = null;
				
				for (int i = 0; i < 4; i++) {
					lbl = (Label) flexTable.getWidget(selected_row, i);
					lbl.setStyleName("");
					//lbl.setStyleName("lbl_results_letters_disp");
					//lbl.setVisible(false);
					flexTable.getCellFormatter().setStyleName(selected_row, i, "cell_results_selected");
				}
				
				for (int i = 0; (i < 4 && prev_selected_row != -1); i++) {
					lbl = (Label) flexTable.getWidget(prev_selected_row, i);
					//lbl.setStyleName("");
					if (prev_selected_row % 2 == 0) {
						flexTable.getCellFormatter().setStyleName(prev_selected_row, i, "cell_results_letters_disp");
						//lbl.setStyleName("lbl_results_dark_letters_disp");
					} else {
						flexTable.getCellFormatter().setStyleName(prev_selected_row, i, "cell_results_dark_letters_disp");
//						lbl.setStyleName("lbl_results_letters_disp");
					}
				}
				
			}
		});
		
		
		Label lblPlace = new Label("");
		HTML htmlPlace = new HTML();
		flexTableHeader.setWidget(0, 0, htmlPlace);
		htmlPlace.setSize("100%", "16px");
		//lblPlace.setStyleName("lbl_header");
		htmlPlace.setText("Pdsadadasdsa ");
		
		
		Label lblLocation = new Label("");
		HTML htmlLocation = new HTML();
		flexTableHeader.setWidget(0, 1, htmlLocation);
		htmlLocation.setSize("100%", "16px");
		//lblLocation.setStyleName("lbl_header");
		htmlLocation.setText("P");
		
		Label lblDistance = new Label("");
		HTML htmlDistance = new HTML();
		flexTableHeader.setWidget(0, 2, htmlDistance);
		htmlDistance.setSize("100%", "16px");
	//	lblDistance.setStyleName("lbl_header");
		htmlDistance.setText("P");
		
		Label lblRating = new Label("");
		HTML htmlRating = new HTML();
		flexTableHeader.setWidget(0, 3, htmlRating);
		htmlRating.setSize("100%", "16px");
		//lblRating.setStyleName("lbl_header");
		htmlRating.setText("Pxcssssssssssssssssssssssssssssssssssssssssssssssssssssssssss");
		
	
		
		
		
		/* Constant throughout */
		absolutePanel.setWidgetPosition(flexTableHeader, 0, 17);
		absolutePanel.setWidgetPosition(scrollPanel, 0, flexTableHeader.getOffsetHeight() + 17);
		
		scrollPanel.setSize("100%", (absolutePanel.getOffsetHeight() - (scrollPanel.getAbsoluteTop() - lblHier1.getAbsoluteTop())) + "px");
		//flexTableHeader.clear();
		
		flexTable.setWidth("100%");
		flexTableHeader.getCellFormatter().setStyleName(0, 0, "cell_dark_grey");
		flexTableHeader.getCellFormatter().setStyleName(0, 1, "cell_dark_grey");
		flexTableHeader.getCellFormatter().setStyleName(0, 2, "cell_dark_grey");
		flexTableHeader.getCellFormatter().setStyleName(0, 3, "cell_dark_grey");
		
		int width = flexTable.getOffsetWidth()/4;
		
		flexTable.getCellFormatter().setWidth(0, 0, width + "px");
		flexTable.getCellFormatter().setWidth(0, 1, width + "px");
		flexTable.getCellFormatter().setWidth(0, 2, width + "px");
		flexTable.getCellFormatter().setWidth(0, 3, width + "px");
			
		flexTableHeader.setSize(flexTable.getOffsetWidth() + "px", "25px");
		flexTableHeader.getCellFormatter().setWidth(0, 0, width + "px");
		flexTableHeader.getCellFormatter().setWidth(0, 1, width + "px");
		flexTableHeader.getCellFormatter().setWidth(0, 2, width + "px");
		flexTableHeader.getCellFormatter().setWidth(0, 3, width + "px");
		
		System.out.println(flexTableHeader.getAbsoluteLeft());
		System.out.println(flexTable.getAbsoluteLeft());
		System.out.println(flexTable.getOffsetWidth());
		System.out.println(flexTableHeader.getOffsetWidth());
		System.out.println(width);
		
		flexTable.setStyleName("flex_table");
		flexTableHeader.setStyleName("flex_table");
		
		
		
		
		
		
/*		
		flexTableHeader.getCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_MIDDLE);
		flexTableHeader.getCellFormatter().setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_MIDDLE);
		flexTableHeader.getCellFormatter().setVerticalAlignment(0, 2, HasVerticalAlignment.ALIGN_MIDDLE);
		flexTableHeader.getCellFormatter().setVerticalAlignment(0, 3, HasVerticalAlignment.ALIGN_MIDDLE);
*/
	}

	public void form_results_table(int start_row, int num_elem) {
	
		DOM.setStyleAttribute(scrollPanel.getElement(), "overflowY", "scroll");
		DOM.setStyleAttribute(scrollPanel.getElement(), "overflowX", "hidden");
		
		System.out.println("Adding " + num_elem + " rows to the result table with start row " + start_row);
		
		for (int i = start_row; i < start_row + num_elem; i++) {
			for (int j = 0; j < 4; j++) {
				Label lbl = new Label();
				
				lbl.setText("");
				
				if (i % 2 == 0) {
					lbl.setStyleName("lbl_results_letters_disp");
					/* Set the CSS style */
					flexTable.getCellFormatter().setStyleName(i, j, "cell_results_letters_disp");
				} else {
					lbl.setStyleName("lbl_results_dark_letters_disp");
					/* Set the CSS style */
					flexTable.getCellFormatter().setStyleName(i, j, "cell_results_dark_letters_disp");
					
				}
				flexTable.setWidget(i, j, lbl);
				lbl.setSize("100%", "15px");
				//flexTable.getCellFormatter().setVerticalAlignment(i, j, HasVerticalAlignment.ALIGN_MIDDLE);
				
			}
			/* Set height for all the rows */
			flexTable.getCellFormatter().setHeight(i, 0, "20px");
		
		}
	}
	
	
	
	public void populate_results() {
		int num_elem = 10, num_rows;
		Label lbl = null;
		
		num_rows = flexTable.getRowCount();
		
		if (num_rows < num_elem) {
			form_results_table(num_rows, num_elem - num_rows);
		} else {
			//form_results_table(num_rows, num_elem - num_rows);
		}
		
		for (int i = 0; i < num_elem; i++) {
			for (int j = 0; j < 4; j++) {
				lbl = (Label) flexTable.getWidget(i, j);
				if (STATE == 0) {
					lbl.setText("first");
				} else if (STATE == 1) {
					lbl.setText("second");
				} else {
					lbl.setText("third");
				}
			}
		}
	}
}
