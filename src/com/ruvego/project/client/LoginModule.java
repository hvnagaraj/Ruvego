package com.ruvego.project.client;

import java.util.Date;

import org.vaadin.gwtgraphics.client.DrawingArea;
import org.vaadin.gwtgraphics.client.Line;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;

public class LoginModule {
	private static LoginModule module;

	protected static int FACEBOOK_LEFT = 0;
	protected static int FACEBOOK_TOP = 0;
	private static final int LEFT_DETAILS_INDENT = 20;

	private static boolean USER_AUTHENTICATED = false;
	private static String USERNAME;

	private static HorizontalPanel loginPanel;
	private static FocusPanel loginWrapper;
	protected static AbsolutePanel loginDetails;
	protected static AbsolutePanel logoutDetails;
	private static Label lblUsername;
	private static TextBox txtBoxUsername;
	private static Label lblPassword;
	private static PasswordTextBox txtBoxPassword;
	private static Button btnSignIn;
	private static Button btnLogOut;
	private static DrawingArea drawingArea;
	private static Line line;
	private static Label lblOrSignIn;
	private static Label lblFacebook;
	private static Label lblLogin;

	private static PopupPanel loginPopUpPanel;
	private static PopupPanel logoutPopUpPanel;

	public static LoginModule getModule() {
		if (module == null) {
			module = new LoginModule();
		}
		return module;
	}

	public static boolean isUserAuthenticated() {
		return USER_AUTHENTICATED;
	}

	public static String getUsername() {
		return USERNAME;
	}

	private LoginModule() {
		loginPanel = new HorizontalPanel();
		loginWrapper = new FocusPanel();
		loginWrapper.add(loginPanel);
		logoutDetails = new AbsolutePanel();
		loginPopUpPanel = new PopupPanel(true, true);
		logoutPopUpPanel = new PopupPanel(true, true);

		loginDetails = new AbsolutePanel();

		loginPanel.setWidth("100%");

		String cookieValue = Cookies.getCookie("username");
		if (cookieValue != null) {
			lblLogin = new Label(cookieValue, false);
			USERNAME = lblLogin.getText();
			USER_AUTHENTICATED = true;
		} else {
			lblLogin = new Label("Sign-In", false);
		}

		lblLogin.setStyleName("loginText");
		loginPanel.add(lblLogin);

		Image imgDropDown = new Image("Images/dropdownbutton.png");
		loginPanel.add(imgDropDown);
		imgDropDown.setPixelSize(12, 12);

		loginPanel.setSpacing(2);

		loginPanel.setStyleName("loginPanel");

		lblUsername = new Label("Username");
		loginDetails.add(lblUsername, LEFT_DETAILS_INDENT, 20);
		lblUsername.setStyleName("contributeText");

		txtBoxUsername = new TextBox();
		loginDetails.add(txtBoxUsername);
		txtBoxUsername.setSize("190px", "18px");

		lblPassword = new Label("Password");
		loginDetails.add(lblPassword);
		lblPassword.setStyleName("contributeText");

		txtBoxPassword = new PasswordTextBox();
		loginDetails.add(txtBoxPassword);
		txtBoxPassword.setSize("190px", "18px");

		btnSignIn = new Button("Sign-In");
		loginDetails.add(btnSignIn);
		btnSignIn.setStyleName("button");

		btnLogOut = new Button("Log-Out");
		logoutDetails.add(btnLogOut, LEFT_DETAILS_INDENT, 20);
		btnLogOut.setStyleName("button");


		Ruvego.headerPanel.add(loginWrapper, Ruvego.getClientWidth() - loginWrapper.getOffsetWidth() - Ruvego.imgBox.getOffsetWidth() - 40, 5);

		loginDetails.setPixelSize(500, 300);
		logoutDetails.setPixelSize(100, 100);

		loginPopUpPanel.add(loginDetails);
		logoutPopUpPanel.add(logoutDetails);

		Ruvego.getRootPanel().add(loginPopUpPanel);
		Ruvego.getRootPanel().add(logoutPopUpPanel);

		loginDetails.setStyleName("loginDetailsPanel");
		logoutDetails.setStyleName("loginDetailsPanel");

		loginWrapper.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (USER_AUTHENTICATED == false) {
					if (loginPopUpPanel.isVisible() == true) {
						hideLogin();
					} else {
						showLogin();
					}
				} else {
					if (logoutPopUpPanel.isVisible() == true) {
						hideLogout();
					} else {
						showLogout();
					}
				}
			}
		});

		drawingArea = new DrawingArea(4, 150);
		line = new Line(1, 1, 1, 300);
		drawingArea.add(line);
		loginDetails.add(drawingArea, 5, 5);
		line.setStrokeColor("grey");
		line.setStrokeWidth(2);
		line.setVisible(true);

		lblOrSignIn = new Label("Or Sign In with");
		loginDetails.add(lblOrSignIn);
		lblOrSignIn.setStyleName("contributeText");

		lblFacebook = new Label("Facebook");
		loginDetails.add(lblFacebook);
		lblFacebook.setStyleName("contributeText");

		final AsyncCallback<Boolean> callbackAuthenticate = new AsyncCallback<Boolean>() {

			@Override
			public void onSuccess(Boolean result) {
				if (result == true) {
					long DURATION = 1000 * 60 * 60 * 24 * 14; //duration remembering login. 2 weeks in this example.
					Date expires = new Date(System.currentTimeMillis() + DURATION);

					lblLogin.setText(USERNAME);
					USER_AUTHENTICATED = true;
					loginPanelAlignments();
					hideLogin();
					Cookies.setCookie("username", USERNAME, expires, null, "/", false);

					userLoggedIn();
				} else {
					Window.alert("Invalid Username or Password. Access Denied");
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Server is busy. Please try again after some time");
			}
		};

		btnSignIn.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				USERNAME = txtBoxUsername.getText();
				Ruvego.getResultsFetchAsync().authenticateUser(txtBoxUsername.getText(), txtBoxPassword.getText(), callbackAuthenticate);
			}
		});

		btnLogOut.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Cookies.removeCookie("username");
				lblLogin.setText("Sign-In");
				hideLogout();
				USER_AUTHENTICATED = false;

				userLoggedOut();
			}
		});

		loginPopUpPanel.addCloseHandler(new CloseHandler<PopupPanel>() {

			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (loginPopUpPanel.isVisible() == true) {
					System.out.println("Login event triggered");
					hideLogin();
				}
			}
		});

		logoutPopUpPanel.addCloseHandler(new CloseHandler<PopupPanel>() {

			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (logoutPopUpPanel.isVisible() == true) {
					System.out.println("Logout event triggered");
					hideLogout();
				}
			}
		});

		loginPopUpPanel.addAutoHidePartner(loginWrapper.getElement());
		logoutPopUpPanel.addAutoHidePartner(loginWrapper.getElement());

		loginPanelAlignments();
		hideLogin();
		hideLogout();

	}

	protected void userLoggedIn() {

	}

	protected void userLoggedOut() {
		// TODO Auto-generated method stub
		Ruvego.userLoggedOut();
		ResultsActivityMenu.userLoggedOut();
		History.newItem("homePage");
	}

	protected void showLogout() {
		System.out.println("Show logout");
		logoutPopUpPanel.setVisible(true);
		logoutPopUpPanel.show();
		loginPanel.setStyleName("loginPanelWhite");
		lblLogin.setStyleName("loginTextBlack");
		logoutPanelAlignments();
	}

	protected void hideLogout() {
		System.out.println("Hide logout");
		logoutPopUpPanel.setVisible(false);
		logoutPopUpPanel.hide();
		loginPanel.setStyleName("loginPanel");
		lblLogin.setStyleName("loginText");
	}

	public static void showLogin() {
		System.out.println("Show login");
		loginPopUpPanel.setVisible(true);
		loginPopUpPanel.show();
		txtBoxUsername.setText("");
		txtBoxPassword.setText("");
		loginPanel.setStyleName("loginPanelWhite");
		lblLogin.setStyleName("loginTextBlack");
		txtBoxUsername.setFocus(true);
		loginPanelAlignments();
		Application.panelAlignments();
	}

	public static void hideLogin() {
		System.out.println("Hide login");
		loginPopUpPanel.setVisible(false);
		loginPopUpPanel.hide();
		loginPanel.setStyleName("loginPanel");
		lblLogin.setStyleName("loginText");
	}

	public static void loginPanelAlignments() {		
		int RIGHT_DETAILS_INDENT;
		int LOGIN_PANEL_LEFT = Ruvego.getClientWidth() - loginWrapper.getOffsetWidth() - Ruvego.imgBox.getOffsetWidth() - 40;

		loginDetails.setPixelSize(500, 300);
		Ruvego.headerPanel.setWidgetPosition(loginWrapper, Ruvego.getClientWidth() - loginWrapper.getOffsetWidth() - Ruvego.imgBox.getOffsetWidth() - 40, 5);

		loginDetails.setWidgetPosition(txtBoxUsername, LEFT_DETAILS_INDENT, lblUsername.getAbsoluteTop() - loginDetails.getAbsoluteTop() + lblUsername.getOffsetHeight()); 
		loginDetails.setWidgetPosition(lblPassword, LEFT_DETAILS_INDENT, txtBoxUsername.getAbsoluteTop() - loginDetails.getAbsoluteTop() + txtBoxUsername.getOffsetHeight() + 10);
		loginDetails.setWidgetPosition(txtBoxPassword, LEFT_DETAILS_INDENT, lblPassword.getAbsoluteTop() - loginDetails.getAbsoluteTop() + lblPassword.getOffsetHeight());
		loginDetails.setWidgetPosition(btnSignIn, LEFT_DETAILS_INDENT, txtBoxPassword.getAbsoluteTop() - loginDetails.getAbsoluteTop() + txtBoxPassword.getOffsetHeight() + 10);
		loginDetails.setWidgetPosition(drawingArea, txtBoxUsername.getAbsoluteLeft() - loginDetails.getAbsoluteLeft() + txtBoxUsername.getOffsetWidth() + 
				LEFT_DETAILS_INDENT, 15);

		RIGHT_DETAILS_INDENT = drawingArea.getAbsoluteLeft() - loginDetails.getAbsoluteLeft() + LEFT_DETAILS_INDENT;
		loginDetails.setWidgetPosition(lblOrSignIn, RIGHT_DETAILS_INDENT, 20);
		loginDetails.setWidgetPosition(lblFacebook, RIGHT_DETAILS_INDENT, lblOrSignIn.getAbsoluteTop() - loginDetails.getAbsoluteTop() + 
				lblOrSignIn.getOffsetHeight() + 20);
		FACEBOOK_LEFT = RIGHT_DETAILS_INDENT;
		FACEBOOK_TOP = lblFacebook.getAbsoluteTop() - loginDetails.getAbsoluteTop() + lblFacebook.getOffsetHeight();


		loginDetails.setWidth((lblOrSignIn.getAbsoluteLeft() - loginDetails.getAbsoluteLeft() + lblOrSignIn.getOffsetWidth() + LEFT_DETAILS_INDENT) + "px");
		loginDetails.setHeight((btnSignIn.getAbsoluteTop() - loginDetails.getAbsoluteTop() + btnSignIn.getOffsetHeight() + 20) + "px");

		if (loginPopUpPanel.isVisible() == true) {
			RootPanel.get().setWidgetPosition(loginPopUpPanel, LOGIN_PANEL_LEFT - (loginPopUpPanel.getOffsetWidth() - loginWrapper.getOffsetWidth()), 
					loginWrapper.getAbsoluteTop() + loginWrapper.getOffsetHeight());
		}

	}

	public static void logoutPanelAlignments() {
		int LOGIN_PANEL_LEFT = Ruvego.getClientWidth() - loginWrapper.getOffsetWidth() - Ruvego.imgBox.getOffsetWidth() - 40;

		logoutDetails.setPixelSize(100, 100);

		Ruvego.headerPanel.setWidgetPosition(loginWrapper, Ruvego.getClientWidth() - loginWrapper.getOffsetWidth() - Ruvego.imgBox.getOffsetWidth() - 40, 5);
		logoutDetails.setWidth((btnLogOut.getAbsoluteLeft() - logoutDetails.getAbsoluteLeft() + btnLogOut.getOffsetWidth() + LEFT_DETAILS_INDENT) + "px");
		logoutDetails.setHeight((btnLogOut.getAbsoluteTop() - logoutDetails.getAbsoluteTop() + btnLogOut.getOffsetHeight() + 20) + "px");

		if (logoutPopUpPanel.isVisible() == true) {
			RootPanel.get().setWidgetPosition(logoutPopUpPanel, LOGIN_PANEL_LEFT - (logoutPopUpPanel.getOffsetWidth() - loginWrapper.getOffsetWidth()), 
					loginWrapper.getAbsoluteTop() + loginWrapper.getOffsetHeight());
		}

	}


	public static void panelALignments() {
		if (loginDetails.isVisible()) {
			loginPanelAlignments();	
		}

		if (logoutDetails.isVisible()) {
			logoutPanelAlignments();
		}
	}

}
