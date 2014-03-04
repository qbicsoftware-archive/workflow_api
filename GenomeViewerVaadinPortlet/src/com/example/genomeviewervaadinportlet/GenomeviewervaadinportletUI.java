package com.example.genomeviewervaadinportlet;

import javax.servlet.annotation.WebServlet;

import com.qbic.javascript.JSGenomeViewerComponent;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickListener;

@SuppressWarnings("serial")
@Theme("liferay")
public class GenomeviewervaadinportletUI extends UI {
	
	TextField chrInput;
	TextField startInput;
	TextField endInput;
	Label region;
	int changes = 0;
	JSGenomeViewerComponent genomeViewer;

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = GenomeviewervaadinportletUI.class)
	public static class Servlet extends VaadinServlet {
	}

	@Override
	protected void init(VaadinRequest request) {
		final VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		setContent(layout);

		genomeViewer = new JSGenomeViewerComponent("1","9968","10132");

		// Process a value input by the user from the client-side
		genomeViewer.addValueChangeListener(
		        new JSGenomeViewerComponent.ValueChangeListener() {
		    @Override
		    public void valueChange() {
		    	changes++;
		    	region.setValue("Changes: "+Integer.toString(changes));
		    }
		});

		// Set the value from server-side
		chrInput = new TextField("Chromosome");
		startInput = new TextField("Start");
		endInput = new TextField("End");
		region = new Label("Changes: "+Integer.toString(changes));
		Panel regionPanel = new Panel(region);
		
		Button send = new Button("Send to JS");
		ClickListener bLis = new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				changeVal();
			}
		};
		send.addClickListener(bLis);
		
		layout.addComponent(chrInput);
		layout.addComponent(startInput);
		layout.addComponent(endInput);
		layout.addComponent(send);
		layout.addComponent(regionPanel);
		layout.addComponent(genomeViewer);
	}
	
	private void changeVal() {
		genomeViewer.setValue(chrInput.getValue(), startInput.getValue(), endInput.getValue());
	}

}