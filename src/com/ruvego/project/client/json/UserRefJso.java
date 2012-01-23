package com.ruvego.project.client.json;

import com.google.gwt.core.client.JavaScriptObject;

public class UserRefJso extends JavaScriptObject {
	  // Overlay types always have protected, zero-arg constructors
	  protected UserRefJso() { }

	  public final native String getId()  /*-{ return this.id;  }-*/;
	  public final native String getName() /*-{ return this.name; }-*/;	  
}
