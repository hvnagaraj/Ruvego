package com.ruvego.project.client;

import java.util.LinkedList;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.core.client.GWT;

public class ContributeAddCategory {

	private static ContributeAddCategory page;

	public static int PANEL_TOP = 0;
	final public static int LBL_FIXED_DELTA = 15;
	final public static int CHOOSE = 0;
	final public static int NO_SUB_CATEGORY = 1;
	final public static int HAS_SUB_CATEGORY = 2;
	final public static int IS_A_SUB_CATEGORY = 3;
	
	private static final String UPLOAD_ACTION_URL = GWT.getModuleBaseURL() + "upload";

	static private AbsolutePanel categoryPanel;

	static private AsyncCallback<String[]> callbackCategoryList;

	static private Label lblError;
	
	static private AsyncCallback<Boolean> callbackCategorySubmit;


	private static 	Button btnSubmit;
	private static FormPanel formPanel;
	private static FileUpload fileUpload;
	private static WriteCategoryPacket writeData;

	private static TextBox textBoxName;
	private static TextBox textBox1;
	private static TextBox textBox2;
	private static TextBox textBox3;

	private static TextBox textBoxAddType;

	private static Label lblColumn1;
	private static Label lblColumn2;
	private static Label lblColumn3;

	private static Button btnAdd;

	private static ListBox listBoxTypeOptions;

	private static LinkedList<String> typeOptions = new LinkedList<String>();
	
	private static ListBox listBoxCategoryType;
	
	private static ListBox listBoxCategory;
	private static Label lblCategory;

	private ContributeAddCategory() {
		categoryPanel = new AbsolutePanel();
		categoryPanel.setSize("1000px", "539px");

		//TODO To be changed. RootPanel must not be accessed directly
		/*
		RootPanel rootPanel = RootPanel.get();
		rootPanel.add(categoryPanel, 10, 10);
		*/

		lblError = new Label("Error");
		categoryPanel.add(lblError, 133, 11);
		lblError.setSize("725px", "18px");
		lblError.setStyleName("orangeText");

		RuvegoContribute.getContributePanel().add(categoryPanel, 0, 50);

		btnSubmit = new Button("New button");
		categoryPanel.add(btnSubmit, 471, 493);
		btnSubmit.setText("Submit");
		btnSubmit.setStyleName("button");
		btnSubmit.setPixelSize(70, 30);

		AbsolutePanel backPanel1 = new AbsolutePanel();
		categoryPanel.add(backPanel1, 133, 47);
		backPanel1.setSize("265px", "422px");
		backPanel1.setStyleName("backPanel");

		PANEL_TOP = backPanel1.getAbsoluteTop();

		Label lblName = new Label("Name");
		backPanel1.add(lblName, 46, 35);
		lblName.setStyleName("contributeText");

		textBoxName = new TextBox();
		backPanel1.add(textBoxName, 46, lblName.getAbsoluteTop() - PANEL_TOP + LBL_FIXED_DELTA);
		textBoxName.setFocus(true);

		lblCategory = new Label("Category");
		backPanel1.add(lblCategory, 46, 103);
		lblCategory.setStyleName("contributeText");

		listBoxCategory = new ListBox();
		backPanel1.add(listBoxCategory, 46, lblCategory.getAbsoluteTop() - PANEL_TOP + LBL_FIXED_DELTA);
		listBoxCategory.setVisibleItemCount(1);
		listBoxCategory.setSize("173px", "18px");
		listBoxCategory.addItem("Choose");

		lblColumn1 = new Label("Column1");
		backPanel1.add(lblColumn1, 46, 168);
		lblColumn1.setStyleName("contributeText");

		textBox1 = new TextBox();
		backPanel1.add(textBox1, 46, lblColumn1.getAbsoluteTop() - PANEL_TOP + LBL_FIXED_DELTA);

		lblColumn2 = new Label("Column2");
		backPanel1.add(lblColumn2, 46, 233);
		lblColumn2.setStyleName("contributeText");

		textBox2 = new TextBox();
		backPanel1.add(textBox2, 46, lblColumn2.getAbsoluteTop() - PANEL_TOP + LBL_FIXED_DELTA);

		lblColumn3 = new Label("Column3");
		backPanel1.add(lblColumn3, 46, 297);
		lblColumn3.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		lblColumn3.setStyleName("contributeText");

		textBox3 = new TextBox();
		backPanel1.add(textBox3, 46, lblColumn3.getAbsoluteTop() - PANEL_TOP + LBL_FIXED_DELTA);

		formPanel = new FormPanel();
		backPanel1.add(formPanel, 10, 374);
		formPanel.setMethod(FormPanel.METHOD_POST);
		formPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
		formPanel.setAction(UPLOAD_ACTION_URL);

		fileUpload = new FileUpload();
		formPanel.setWidget(fileUpload);
		fileUpload.setSize("100%", "100%");
		fileUpload.setName("uploadFormElement");

		AbsolutePanel backPanel2 = new AbsolutePanel();
		categoryPanel.add(backPanel2, 516, 47);
		backPanel2.setSize("342px", "146px");
		backPanel2.setStyleName("backPanel");

		final Label lblSubActivityPresent = new Label("Category Type");
		backPanel2.add(lblSubActivityPresent, 96, 52);
		lblSubActivityPresent.setStyleName("contributeText");
		lblSubActivityPresent.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);

		listBoxCategoryType = new ListBox();
		backPanel2.add(listBoxCategoryType, 96, lblSubActivityPresent.getAbsoluteTop() - PANEL_TOP + LBL_FIXED_DELTA);
		listBoxCategoryType.setVisibleItemCount(1);
		listBoxCategoryType.setWidth("150px");
		listBoxCategoryType.addItem("Choose");
		listBoxCategoryType.addItem("Doesn't have a Sub-Category");
		listBoxCategoryType.addItem("Has a Sub-Category");
		listBoxCategoryType.addItem("Is a Sub-Category");
		lblError.setVisible(false);

		AbsolutePanel backPanel3 = new AbsolutePanel();
		categoryPanel.add(backPanel3, 516, 219);
		backPanel3.setSize("342px", "250px");
		backPanel3.setStyleName("backPanel");

		textBoxAddType = new TextBox();
		backPanel3.add(textBoxAddType, 31, 36);

		listBoxTypeOptions = new ListBox();
		listBoxTypeOptions.setMultipleSelect(true);
		backPanel3.add(listBoxTypeOptions, 31, 90);
		listBoxTypeOptions.setSize("279px", "96px");
		listBoxTypeOptions.setVisibleItemCount(1);

		btnAdd = new Button("Add");
		backPanel3.add(btnAdd, 250, 37);
		btnAdd.setText("+");
		btnAdd.setSize("30px", "30px");
		btnAdd.setVisible(false);


		btnAdd.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {

				if (!textBoxAddType.getText().equalsIgnoreCase("")) {
					listBoxTypeOptions.addItem(textBoxAddType.getText());
					typeOptions.add(textBoxAddType.getText());
					textBoxAddType.setText("");
				}
			}
		});
		listBoxTypeOptions.setVisible(false);
		textBoxAddType.setVisible(false);

		listBoxCategoryType.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				if (listBoxCategoryType.getSelectedIndex() == IS_A_SUB_CATEGORY) {
					Ruvego.getResultsFetchAsync().fetchCategoryList(callbackCategoryList);
					lblCategory.setVisible(true);
					listBoxCategory.setVisible(true);

					fieldsEnable();
				} else if (listBoxCategoryType.getSelectedIndex() == NO_SUB_CATEGORY) {
					fieldsEnable();
					listBoxCategory.setVisible(false);
					lblCategory.setVisible(false);
				} else {
					lblCategory.setVisible(false);
					listBoxCategory.setVisible(false);

					fieldsDisable();
				}
			}
		});
		
		/* Submit Activity into Database */
		callbackCategorySubmit = new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				System.err.println("Error");
			}

			@Override
			public void onSuccess(Boolean result) {
				if (result == false) {
					throwError("Entry already present");
					return;
				}
				
				
				resetTableData();
				
				throwError("Successfully added the Category");
				System.out.println("Client: Successfully added the category into DB");
			}
		};
		

		// Add an event handler to the form.
		formPanel.addSubmitHandler(new FormPanel.SubmitHandler() {
			public void onSubmit(SubmitEvent event) {
				// This event is fired just before the form is submitted. We can
				// take this opportunity to perform validation.
				if (fileUpload.getFilename().isEmpty() == true) {
					Window.alert("Choose a file to upload");
					event.cancel();
				}
			}
		});

		formPanel.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
			public void onSubmitComplete(SubmitCompleteEvent event) {
				// When the form submission is successfully completed, this
				// event is fired. Assuming the service returned a response of type
				// text/html, we can get the result text here (see the FormPanel
				// documentation for further explanation).
				//Window.alert(event.getResults());
				System.out.println("Server: Category image upload successful");

				String delims;
				String[] tokens = null;
				if (event.getResults().contains("Created filename :")) {
					delims = "Created filename : ";
					tokens = event.getResults().split(delims);

					delims = "</pre>";
					tokens = tokens[1].split(delims);

					System.out.println("File: " + tokens[0]);
				}


				if (listBoxCategoryType.getSelectedIndex() == NO_SUB_CATEGORY) {
					/* Doesnt have a Sub-Category */
					writeData = new WriteCategoryPacket(RuvegoContribute.toCamelCase(textBoxName.getText()), RuvegoContribute.toCamelCase(textBox1.getText()),
							RuvegoContribute.toCamelCase(textBox2.getText()), RuvegoContribute.toCamelCase(textBox3.getText()), typeOptions, null, tokens[0]);
				} else if (listBoxCategoryType.getSelectedIndex() == HAS_SUB_CATEGORY) {
					/* Has a Sub-Category */
					writeData = new WriteCategoryPacket(RuvegoContribute.toCamelCase(textBoxName.getText()), "subcategory",
							RuvegoContribute.toCamelCase(textBox2.getText()), RuvegoContribute.toCamelCase(textBox3.getText()), typeOptions, null, tokens[0]);
				} else {
					/* Is a Sub-Category */
					writeData = new WriteCategoryPacket(RuvegoContribute.toCamelCase(textBoxName.getText()), RuvegoContribute.toCamelCase(textBox1.getText()),
							RuvegoContribute.toCamelCase(textBox2.getText()), RuvegoContribute.toCamelCase(textBox3.getText()), typeOptions, 
							listBoxCategory.getItemText(listBoxCategory.getSelectedIndex()),
							tokens[0]);					
				}

				Ruvego.getResultsWriteService().writeResults(writeData, callbackCategorySubmit);
			}
		});
		textBox3.setVisible(false);
		lblColumn3.setVisible(false);
		textBox2.setVisible(false);
		lblColumn2.setVisible(false);
		textBox1.setVisible(false);
		lblColumn1.setVisible(false);
		listBoxCategory.setVisible(false);
		lblCategory.setVisible(false);


		btnSubmit.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				if (textBoxName.getText().equalsIgnoreCase("")) {
					lblError.setText("Category Name cannot be left blank");
					lblError.setVisible(true);
					return;
				} else {
					lblError.setVisible(false);
				}

				if (listBoxCategoryType.getSelectedIndex() == CHOOSE) { 
					lblError.setText("Choose the Category to which this Sub-Category has to be added");
					lblError.setVisible(true);
					return;
				}

				formPanel.submit();

			}
		});

		/* Initially set the category list visible as false */

		/* Fetch the Category List */

		// Set up the callback object.
		callbackCategoryList = new AsyncCallback<String[]>() {

			@Override
			public void onFailure(Throwable caught) {
				System.out.println("AJAX error");	
			}

			@Override
			public void onSuccess(String[] result) {
				listBoxCategory.clear();
				listBoxCategory.addItem("Choose");
				for (int i = 0; i < result.length; i++) {
					listBoxCategory.addItem(result[i]);
				}

			}

		};
	}

	protected void resetTableData() {
		textBoxName.setText("");
		textBox1.setText("");
		textBox2.setText("");
		textBox3.setText("");
		textBoxAddType.setText("");
		listBoxTypeOptions.clear();
		listBoxCategoryType.setSelectedIndex(0);
		listBoxCategory.setSelectedIndex(0);
		formPanel.reset();
		fieldsDisable();
	}

	protected void throwError(String error) {
		lblError.setText(error);
		lblError.setVisible(true);
	}

	public static ContributeAddCategory getPage() {
		if (page == null) {
			page = new ContributeAddCategory();
		}
		return page;
	}


	protected void fieldsDisable() {	
		textBox1.setVisible(false);
		textBox2.setVisible(false);
		textBox3.setVisible(false);

		lblColumn1.setVisible(false);
		lblColumn2.setVisible(false);
		lblColumn3.setVisible(false);

		btnAdd.setVisible(false);
		listBoxTypeOptions.setVisible(false);
		
		listBoxCategory.setVisible(false);
		lblCategory.setVisible(false);

		textBoxAddType.setVisible(false);
	}


	protected void fieldsEnable() {	
		textBox1.setVisible(true);
		textBox2.setVisible(true);
		textBox3.setVisible(true);

		lblColumn1.setVisible(true);
		lblColumn2.setVisible(true);
		lblColumn3.setVisible(true);

		btnAdd.setVisible(true);
		listBoxTypeOptions.setVisible(true);

		textBoxAddType.setVisible(true);
	}

	public void panelsView() {
		categoryPanel.setVisible(true);
		resetTableData();
	}

	public static void clearContent() {
		if (categoryPanel != null) {
			categoryPanel.setVisible(false);
		}
	}

}

