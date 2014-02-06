package com.example.genomeviewervaadinportlet;

import javax.servlet.annotation.WebServlet;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.qbic.javascript.JSGenomeViewerComponent;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.AbstractJavaScriptComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.JavaScriptFunction;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
@Theme("liferay")
public class GenomeviewervaadinportletUI extends UI {

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = GenomeviewervaadinportletUI.class)
	public static class Servlet extends VaadinServlet {
	}

	@Override
	protected void init(VaadinRequest request) {
		final VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		setContent(layout);
		Button button = new Button("test Button");
		AbstractJavaScriptComponent genomeViewer = new JSGenomeViewerComponent("blabal");
		layout.addComponent(button);
		button.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				//JavaScript.getCurrent().execute();
			}
		});
		
		JavaScript.getCurrent().addFunction("changeButtonText",
                new JavaScriptFunction() {
			@Override
			public void call(org.json.JSONArray arguments) throws org.json.JSONException {
					String message = arguments.getString(0);
					Notification.show(message);
					layout.addComponent(new Label("something changed"));
			}
});
		
		layout.addComponent(genomeViewer);
	}

}