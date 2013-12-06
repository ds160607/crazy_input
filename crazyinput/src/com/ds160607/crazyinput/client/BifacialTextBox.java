package com.ds160607.crazyinput.client;

import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.Window.Navigator;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;

public class BifacialTextBox extends Composite {
	private ListBox ptb = null;
	private TextBox tb = null;
	public BifacialTextBox() {
		/*
		if ((!(Navigator.getPlatform().equals("HP-UX")))
				&& (!(Navigator.getPlatform().equals("Linux i686")))
				//&& (!(Navigator.getPlatform().equals("Linux armv7l"))) - android
				&& (!(Navigator.getPlatform().equals("Mac68K")))
				&& (!(Navigator.getPlatform().equals("MacPPC")))
				&& (!(Navigator.getPlatform().equals("MacIntel")))
				&& (!(Navigator.getPlatform().equals("Win16")))
				&& (!(Navigator.getPlatform().equals("Win32")))) {
			ptb = new ListBox();
			ptb.getElement().setAttribute("contenteditable","true");
			initWidget(ptb);
		} else {
			tb = new TextBox();
			initWidget(tb);
		}
		*/
		tb = new TextBox();
		initWidget(tb);
		
	}

	public void setValue(String s) {
		if (tb != null) {
			tb.setValue(s);
		}
	}

	public String getValue() {
		if (tb != null) {
			return tb.getValue();
		}
		return "";
	}

	public void setSelectionRange(int s, int e) {
		if (tb != null) {
			tb.setSelectionRange(s, e);
		}
	}

	public void addBlurHandler(BlurHandler bh) {
		if (tb != null) {
			tb.addBlurHandler(bh);
		}
		if (ptb != null) {
			ptb.addBlurHandler(bh);
		}
	}

	public void addFocusHandler(FocusHandler fh) {
		if (tb != null) {
			tb.addFocusHandler(fh);
		}
		if (ptb != null) {
			ptb.addFocusHandler(fh);
		}
	}

	public void addDoubleClickHandler(DoubleClickHandler dch) {
		if (tb != null) {
			tb.addDoubleClickHandler(dch);
		}
		if (ptb != null) {
			ptb.addDoubleClickHandler(dch);
		}
	}

	public void addMouseDownHandler(MouseDownHandler mdh) {
		if (tb != null) {
			tb.addMouseDownHandler(mdh);
		}
		if (ptb != null) {
			ptb.addMouseDownHandler(mdh);
		}
	}

	public void addKeyPressHandler(KeyPressHandler kph) {
		if (tb != null) {
			tb.addKeyPressHandler(kph);
		}
		if (ptb != null) {
			ptb.addKeyPressHandler(kph);
		}
	}

	public void addKeyDownHandler(KeyDownHandler kdh) {
		if (tb != null) {
			tb.addKeyDownHandler(kdh);
		}
		if (ptb != null) {
			ptb.addKeyDownHandler(kdh);
		}
	}

}
