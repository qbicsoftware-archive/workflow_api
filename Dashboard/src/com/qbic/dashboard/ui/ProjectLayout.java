package com.qbic.dashboard.ui;

import java.io.File;
import java.util.List;


import ch.systemsx.cisd.openbis.generic.shared.api.v1.dto.Experiment;
import ch.systemsx.cisd.openbis.generic.shared.api.v1.dto.Project;

import com.qbic.openbismodel.OpenBisExperiment;
import com.qbic.openbismodel.OpenBisProject;
import com.vaadin.server.FileResource;
import com.vaadin.server.Resource;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Button.ClickEvent;

public class ProjectLayout extends GridLayout {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	OpenBisProject project;
	FileResource resource_traffic_light_red;
	FileResource resource_traffic_light_green;
	
	public ProjectLayout(OpenBisProject openbisProject, FileResource light_red,FileResource light_green){	
		int numberOfColumns = 6;
		int numberOfRows = 1 + 1 + 3*openbisProject.getExperiments().size() + 1;
		this.setColumns(numberOfColumns);
		this.setRows(numberOfRows);
		this.project = openbisProject;
		this.resource_traffic_light_green = light_green;
		this.resource_traffic_light_red = light_red;
		constructLayout();
	}

	private void constructLayout() {
		// TODO Auto-generated method stub
		this.addStyleName("The grid itself represents a second project");
		this.setMargin(new MarginInfo(true, true, true, true));
		this.setSpacing(true);
		
		
		Label project_name = new Label(this.project.getId());
		Label qbic_user = new Label(this.project.getQBiCUser());
		Label description = new Label(this.project.getDescription());
		Label qbic_scientist = new Label(this.project.getQBiCScientist());
		Label project_date_registration = new Label (this.project.getRegistrationDate().toString());

		Button detailsField = new Button("Description");
	    detailsField.setData(this.project.getDescription());
	    detailsField.addClickListener(new Button.ClickListener() {
	        public void buttonClick(ClickEvent event) {
	            // Get the item identifier from the user-defined data.
	            String tmp = (String)event.getButton().getData();
	            
	            Notification.show(tmp);
	        } 
	    });
	    detailsField.addStyleName("link");
		Image project_finished = new Image("project finished", traffic_light(this.project.isProject_finished()));
		project_finished.setHeight("3em");
		this.addComponent(project_name,0,0);
		this.addComponent(qbic_user,1,0);
		this.addComponent(detailsField,2,0);
		this.addComponent(qbic_scientist,3,0);
		this.addComponent(project_date_registration,4,0);
		this.addComponent(project_finished,5,0);
		this.setComponentAlignment(project_finished, Alignment.MIDDLE_CENTER);
		System.out.println(this.project.toString());
		int exp_position = 0;
		for(OpenBisExperiment exp : this.project.getExperiments()){
			
			Label experiment1 = new Label(exp.getId());
			Button experiment_description = new Button("Description");
			experiment_description.setData(exp.getDescription());
			experiment_description.addClickListener(new Button.ClickListener() {
		        public void buttonClick(ClickEvent event) {
		            // Get the item identifier from the user-defined data.
		            String tmp = (String)event.getButton().getData();
		            
		            Notification.show(tmp);
		        } 
		    });
			experiment_description.addStyleName("link");
			Label efdv = new Label(String.valueOf(exp.getEstimated_volume()));
			Label cv = new Label(String.valueOf(exp.getCurrent_volume()));
			// Show the image in the application
			Image experimental_design = new Image("experiment designed", traffic_light(exp.isExp_designed()));
			Image offer_accepted = new Image("offer accepted", traffic_light(exp.isOffer_accepted()));
			Image data_generated = new Image("data generated", traffic_light(exp.isData_generated()));
			Image data_processed = new Image("data processed", traffic_light(exp.isData_processed()));
			Image data_analyzed = new Image("data analyzed", traffic_light(exp.isData_analysed()));
			Image invoice_sent = new Image("invoice sent", traffic_light(exp.isInvoice_sent()));

			experimental_design.setHeight("3em");
			offer_accepted.setHeight("3em");
			data_generated.setHeight("3em");
			data_processed.setHeight("3em");
			data_analyzed.setHeight("3em");
			invoice_sent.setHeight("3em");
			project_finished.setHeight("3em");
			
			exp_position++;
			Label space2 = new Label("<hr width=\"75%\">",Label.CONTENT_XHTML);
			this.addComponent(space2,0,exp_position,5,exp_position);
			exp_position++;
			this.addComponent(experiment1,0,exp_position);
			this.addComponent(experiment_description,1,exp_position);
			this.addComponent(efdv,2,exp_position);
			this.addComponent(cv,3,exp_position);
			exp_position++;
			this.addComponent(experimental_design,0,exp_position);
			this.setComponentAlignment(experimental_design, Alignment.MIDDLE_CENTER);
			this.addComponent(offer_accepted,1,exp_position);
			this.setComponentAlignment(offer_accepted, Alignment.MIDDLE_CENTER);
			this.addComponent(data_generated,2,exp_position);
			this.setComponentAlignment(data_generated, Alignment.MIDDLE_CENTER);
			this.addComponent(data_processed,3,exp_position);
			this.setComponentAlignment(data_processed, Alignment.MIDDLE_CENTER);
			this.addComponent(data_analyzed,4,exp_position);
			this.setComponentAlignment(data_analyzed, Alignment.MIDDLE_CENTER);
			this.addComponent(invoice_sent,5,exp_position);
			this.setComponentAlignment(invoice_sent, Alignment.MIDDLE_CENTER);
/*
			Label space1 = new Label("<div style=\"font-size:xx-small; border-color:blue;border-style:dotted hidden dashed hidden;\">&nbsp;</div>", Label.CONTENT_XHTML);
			space1.setHeight("1em");
			this.addComponent(space1,0,3,5,3);
			this.addComponent(new Label("<div style=\"border&#58; solid 20px #ff0000;\"><b>Experiment2</b></div>",Label.CONTENT_XHTML), 0, 4);
			this.addComponent(new Label("<div style=\"font-size: large; border-color:blue;border-style:dotted hidden dashed hidden;\">Borders Test in HTML.</div>",Label.CONTENT_XHTML), 1, 4);
			this.addComponent(space2,0,5,5,5);*/
		}
		if(exp_position == 0){
			exp_position++;
			Label no_experiments = new Label("<font color=\"red\">No experiments registered!</font>", Label.CONTENT_XHTML);
			this.addComponent(no_experiments, 0, exp_position,5,exp_position);
		}
		exp_position++;
		Label space1 = new Label("<div style=\"font-size:xx-small; border-color:blue;border-style:dotted hidden dashed hidden;\">&nbsp;</div>", Label.CONTENT_XHTML);
		space1.setHeight("1em");
		this.addComponent(space1,0,exp_position,5,exp_position);
	}

	private Resource traffic_light(boolean green) {
		return (green)? this.resource_traffic_light_green : this.resource_traffic_light_red;
	}
	
}
