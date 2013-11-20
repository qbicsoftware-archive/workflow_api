package com.qbic.Listeners;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;

public class MyTableValueChangeListener implements Property.ValueChangeListener {
    @Override
	public void valueChange(ValueChangeEvent event) {
/*		System.out.println(event.getProperty().getValue());
		String d_type = (String)table.getItem(event.getProperty().getValue()).getItemProperty("Data Set Type").getValue();
		if(d_type.toLowerCase().equals("zip")){
	    	String datasetCode = (String)table.getItem(event.getProperty().getValue()).getItemProperty("CODE").getValue();
			String datasetType = (String)table.getItem(event.getProperty().getValue()).getItemProperty("Name").getValue();
			//VerticalLayout tab = new VerticalLayout();
			//tab.addComponent(new Label(datasetCode));
			//tab.addComponent(new Label(datasetType));
			String fastqc = ((VisualizerUI) UI.getCurrent()).getFastQC(datasetCode);
			ThemeResource themeResource = new ThemeResource(fastqc);
			BrowserFrame browser = new BrowserFrame(" : " , themeResource);//baseDirFile ); //new ExternalResource(url) /*For some reason the file is not shown :/*
			System.out.println("UI.getCurrent().getContent() width: " + UI.getCurrent().getContent().getWidth() +", UI.getCurrent() height " + UI.getCurrent().getContent().getHeight());
			System.out.println("Before change: browser width: " + browser.getWidth() +", browser height " + browser.getHeight());
			browser.setWidth(((VisualizerUI)UI.getCurrent()).getW(),Sizeable.UNITS_PIXELS);
			browser.setHeight(((VisualizerUI)UI.getCurrent()).getH()-1,Sizeable.UNITS_PIXELS);
			System.out.println("After change: browser width: " + browser.getWidth() +", browser height " + browser.getHeight());
			//tab.addComponent(browser);
			//tab.setWidth("100%");
			//tab.setHeight("100%");
			tabsheet.addComponent(browser);			
			tabsheet.getTab(browser).setClosable(true);
			tabsheet.getTab(browser).setCaption("Fastq Quality Control");
			tabsheet.setSelectedTab(browser);
			//tabsheet.getTab(tab).setClosable(true);
			//tabsheet.setSelectedTab(tab);
		}

		*tabsheet.setCloseHandler(new CloseHandler() {
		    @Override
		    public void onTabClose(TabSheet tabsheet,
		                           Component tabContent) {
		        Tab tab = tabsheet.getTab(tabContent);
		        Notification.show("Closing " + tab.getCaption());
		        
		        // We need to close it explicitly in the handler
		        tabsheet.removeTab(tab);
		    }
		});*/
    }
}