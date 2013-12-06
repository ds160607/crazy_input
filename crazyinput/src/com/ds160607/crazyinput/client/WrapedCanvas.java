package com.ds160607.crazyinput.client;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;

public class WrapedCanvas  extends Composite {	
	private Canvas canvas;
	static final String UNSUPPORTED_BROWSER = "Your browser does not support the HTML5 Canvas";
	
	public WrapedCanvas(){
		canvas = Canvas.createIfSupported();
		if (canvas == null) {
			initWidget(new Label(UNSUPPORTED_BROWSER));
			return;
		}
		initWidget(canvas);
	}
	
	public Canvas getCanvas() {
		return canvas;
	}
}
