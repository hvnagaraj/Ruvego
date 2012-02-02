package com.ruvego.project.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;    
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.ruvego.project.client.json.ExceptionJso;
import com.ruvego.project.client.json.UserJso;
import com.ruvego.project.client.json.UserRefJso;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Application extends Composite {
	private static ApplicationUiBinder uiBinder = GWT
			.create(ApplicationUiBinder.class);

	interface ApplicationUiBinder extends UiBinder<Widget, Application> {
	}

	@UiField
	protected static HTML fbLoginButton;

	@UiField
	HTML fbProfile;

	@UiHandler("fbLogout")
	public void onLogoutButtonPressed(ClickEvent event) {
		fbLogout();
	}

	@UiHandler("fbMe")
	public void onMeButtonPressed(ClickEvent event) {
		getMe();
	}

	@UiHandler("fbFriends")
	public void onFriendsButtonPressed(ClickEvent event) {
		getFriends();
	}

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		initWidget(uiBinder.createAndBindUi(this));
	//	RootPanel.get().add(this);

		/* Init Facebook API */
		exportMethods(this);
		//initFacebookAPI();

		// use facebooks xfbml to produce login button (could be done using
		// FB.api only)
		
		LoginModule.loginDetails.add(fbLoginButton, LoginModule.FACEBOOK_LEFT, LoginModule.FACEBOOK_TOP);
		fbLoginButton.setHTML("<fb:login-button></fb:login-button>");
	}	

	private native String initFacebookAPI()
	/*-{
		$wnd.FB.init({appId: '281845898542466', status: true, cookie: true, xfbml: true});
		$wnd.FB.Event.subscribe('auth.sessionChange', function(response) {
		  if (response.session) {
		    // A user has logged in, and a new cookie has been saved
		    $wnd.onLogin();
		  } else {
		    // The user has logged out, and the cookie has been cleared
		    $wnd.onLogout();
		  }		  			  	
		});
	}-*/;

	private native void callAPI(String path, AsyncCallback<JavaScriptObject> callback) /*-{
		$wnd.FB.api(path, function(response) {
			// on error, this callback is never called in Firefox - why?
			if (!response) {
			    alert('Error occured');
			} else if (response.error) {
				alert($wnd.dump(response));
			    // call callback with the actual error
				$wnd.onAPICall(callback, null, response.error);
			} else if (response.data) {
				alert($wnd.dump(response));
				// call callback with the actual json-array
				$wnd.onAPICall(callback, response.data, null);
			} else {
				alert($wnd.dump(response));
				// call callback with the actual json-object
				$wnd.onAPICall(callback, response, null);
			} 
		});
	}-*/;

	private native void fbLogout() /*-{
		$wnd.FB.logout();
	}-*/;

	/**
	 * To be called after an XFBML-tag has been inserted into the DOM
	 */
	private native void parseDomTree() /*-{
		$wnd.FB.XFBML.parse();
	}-*/;

	private native void exportMethods(Application instance) /*-{
		$wnd.onLogin = function() {
		return instance.@com.ruvego.project.client.Application::onLogin()();
		}
		$wnd.onLogout = function() {
		return instance.@com.ruvego.project.client.Application::onLogout()();
		}
		$wnd.onAPICall = function(callback, response, exception) {
		return instance.@com.ruvego.project.client.Application::onAPICall(Lcom/google/gwt/user/client/rpc/AsyncCallback;Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(callback, response, exception);
		}
	}-*/;

	public void onAPICall(AsyncCallback<JavaScriptObject> callback,
			JavaScriptObject response, JavaScriptObject exception) {
		if (response != null) {
			callback.onSuccess(response);
		} else {
			ExceptionJso e = (ExceptionJso) exception;
			callback.onFailure(new Exception(e.getType() + ": " + e.getMessage()));
		}
	}

	public void onLogin() {
		// just to show that xfbml works here, as well
		fbProfile.setHTML("<span>"
				+ "<fb:profile-pic uid=loggedinuser facebook-logo=true></fb:profile-pic>"
				+ "Welcome, <fb:name uid=loggedinuser useyou=false></fb:name>.</span>");

		// make sure the xfbml is rendered
		parseDomTree();
	}

	public void onLogout() {
		fbProfile.setHTML("");
	}

	public void getMe() {
		callAPI("/me", new AsyncCallback<JavaScriptObject>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}

			@Override
			public void onSuccess(JavaScriptObject result) {
				UserJso fbUser = (UserJso) result;
				Window.alert(fbUser.getFullName());
			}
		});
	}

	public void getFriends() {
		callAPI("/me/friends", new AsyncCallback<JavaScriptObject>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());

			}

			@SuppressWarnings("unchecked")
			@Override
			public void onSuccess(JavaScriptObject result) {
				JsArray<UserRefJso> friends = (JsArray<UserRefJso>) result;
				StringBuffer buf = new StringBuffer();
				for (int i = 0, n = friends.length(); i < n; ++i) {
					buf.append(friends.get(i).getName());
					if (i < n) {
						buf.append(", ");
					}
				}

				Window.alert("" + buf);
			}
		});
	}

	public static void panelAlignments() {
		LoginModule.loginDetails.setWidgetPosition(fbLoginButton, LoginModule.FACEBOOK_LEFT, LoginModule.FACEBOOK_TOP);
	}
}
