package com.ds160607.crazyinput.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

/** 
 * Web page which demonstrate a CrazyInput element.
 * @author Dmitry Grushin
 *
 */
public class Crazyinput implements EntryPoint {
	
	private CrazyInputWidget crInput;

	public void onModuleLoad() {	

		RootPanel rootPanel = RootPanel.get();		
		
		//Create an instance of CrazyInput
		crInput = new CrazyInputWidget();	
		
		//Add some style from Crazyinput.css
		crInput.addStyleName("crazyInput");
		
		//Put the element to a body
		rootPanel.add(crInput);
		
	}
}
