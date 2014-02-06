package de.uni_tuebingen.qbic.Listeners;

import de.uni_tuebingen.qbic.visportlet.VisportletUI;
import com.vaadin.server.Page.BrowserWindowResizeEvent;
import com.vaadin.server.Page.BrowserWindowResizeListener;
import com.vaadin.ui.UI;

public class MyBrowserWindowResizeListener implements BrowserWindowResizeListener{
	@Override
	public void browserWindowResized(BrowserWindowResizeEvent event) {
		// TODO Auto-generated method stub
		System.out.println("browser width: " + event.getWidth() + " browser height: " + event.getHeight());
		VisportletUI ui = (VisportletUI)UI.getCurrent();
		ui.setSize(event.getHeight(),event.getWidth());
	}	
}
