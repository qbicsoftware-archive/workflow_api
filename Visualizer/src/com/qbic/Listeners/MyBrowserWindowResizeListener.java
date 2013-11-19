package com.qbic.Listeners;

import com.example.visualizer.VisualizerUI;
import com.vaadin.server.Page.BrowserWindowResizeEvent;
import com.vaadin.server.Page.BrowserWindowResizeListener;
import com.vaadin.ui.UI;

public class MyBrowserWindowResizeListener implements BrowserWindowResizeListener{
	@Override
	public void browserWindowResized(BrowserWindowResizeEvent event) {
		// TODO Auto-generated method stub
		System.out.println("browser width: " + event.getWidth() + " browser height: " + event.getHeight());
		VisualizerUI ui = (VisualizerUI)UI.getCurrent();
		ui.setSize(event.getHeight(),event.getWidth());
	}	
}
