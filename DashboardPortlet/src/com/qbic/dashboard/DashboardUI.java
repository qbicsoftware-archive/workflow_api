package com.qbic.dashboard;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.annotation.WebServlet;

import com.qbic.dashboard.ui.ProjectLayout;
import com.qbic.openbismodel.OpenBisModel;
import com.qbic.openbismodel.OpenBisProject;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnHeaderMode;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;


import ch.systemsx.cisd.openbis.dss.client.api.v1.DataSet;
import ch.systemsx.cisd.openbis.dss.client.api.v1.IOpenbisServiceFacade;
import ch.systemsx.cisd.openbis.dss.client.api.v1.OpenbisServiceFacadeFactory;
import ch.systemsx.cisd.openbis.generic.shared.api.v1.dto.Experiment;
import ch.systemsx.cisd.openbis.generic.shared.api.v1.dto.Project;

import ch.systemsx.cisd.openbis.generic.shared.translator.ProjectTranslator;

@SuppressWarnings("serial")
@Theme("liferay")
public class DashboardUI extends UI {

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = DashboardUI.class)
	public static class Servlet extends VaadinServlet {
	}

	@Override
	protected void init(VaadinRequest request) {
		final VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		setContent(layout);
		
		OpenBisModel openbisModel = new OpenBisModel();
		List<OpenBisProject> projects = openbisModel.getProjects();
		
		// Find the application directory
		String basepath = VaadinService.getCurrent()
		                  .getBaseDirectory().getAbsolutePath();
		// Image as a file resource
		FileResource resource_traffic_light_red = new FileResource(new File(basepath +
		                        "/WEB-INF/images/traffic_light_red.PNG"));
		
		FileResource resource_traffic_light_green = new FileResource(new File(basepath +
                "/WEB-INF/images/traffic_light_green.PNG"));	
		
		
		for(OpenBisProject project  : projects){
			ProjectLayout projectLayout = new ProjectLayout(project,resource_traffic_light_red,resource_traffic_light_green);
			layout.addComponent(projectLayout);
		}
	}
	
	/*
	@Override
	protected void init(VaadinRequest request) {
		final VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		setContent(layout);

		Table projectAndExperimentTable = new Table();
		projectAndExperimentTable.addContainerProperty("Experiment", String.class,  null);
		projectAndExperimentTable.addItem(new Object[] {
			    "BWA_ALN"}, new Integer(1));
		/* Create the table with a caption. *
		Table table = new Table("This is my Table");

		/* Define the names and data types of columns.
		 * The "default value" parameter is meaningless here. *
		table.addContainerProperty("First Name", String.class,  null);
		table.addContainerProperty("Last Name",  String.class,  null);
		table.addContainerProperty("Year",       Integer.class, null);

		/* Add a few items in the table. *
		table.addItem(new Object[] {
		    "Nicolaus","Copernicus",new Integer(1473)}, new Integer(1));
		table.addItem(new Object[] {
		    "Tycho",   "Brahe",     new Integer(1546)}, new Integer(2));
		table.addItem(new Object[] {
		    "Giordano","Bruno",     new Integer(1548)}, new Integer(3));
		table.addItem(new Object[] {
		    "Galileo", "Galilei",   new Integer(1564)}, new Integer(4));
		table.addItem(new Object[] {
		    "Johannes","Kepler",    new Integer(1571)}, new Integer(5));
		table.addItem(new Object[] {
		    "Isaac",   "Newton",    new Integer(1643)}, new Integer(6));
		
		table.addContainerProperty("Expi", Table.class, null, null, null, null);
		table.addItem(new Object[] {
		    "Test1",   "test2",    new Integer(007),projectAndExperimentTable}, new Integer(7));
		
		Table table2 = new Table();
		table2.addContainerProperty("Project1",String.class,null);
		table2.addContainerProperty("QBIC user",String.class, null);
		table2.addContainerProperty("Description",String.class,null);
		table2.addContainerProperty("Qbic Scientist",String.class,null);
		table2.addContainerProperty("Date Project Registration",String.class,null);
		//table2.setColumnHeaderMode(ColumnHeaderMode.HIDDEN);
		table2.addItem(new Object[]{"Ebene2","Experiment1","Experiemt Description","estimated final data volume","current volume"}, new Integer(1));
		table2.addItem(new Object[]{"Ebene2","Experiment2","Experiemt Description","estimated final data volume","current volume"}, new Integer(2));
		table2.addItem(new Object[]{"Ebene2","Experiment3","Experiemt Description","estimated final data volume","current volume"}, new Integer(3));
		
		Table table3 = new Table();
		table3.addContainerProperty("experimental design",String.class,null);
		table3.addContainerProperty("offer accepted",String.class, null);
		table3.addContainerProperty("data generated",String.class,null);
		table3.addContainerProperty("data processed",String.class,null);
		table3.addContainerProperty("data analyzed",String.class,null);		
		table3.addContainerProperty("invoice sent",String.class,null);	
		table3.addContainerProperty("project finished",String.class,null);	
		
		table3.addItem(new Object[]{"redLight","redLight","redLight","redLight","redLight","redLight"}, new Integer(1));
		table3.addItem(new Object[]{"greenLight","greenLight","greenLight","greenLight","greenLight","greenLight"}, new Integer(2));
		
		GridLayout project1 = new GridLayout(2,2);
		project1.addStyleName("example-gridlayout");
		project1.addComponent(table2);
		project1.addComponent(table3);
		
		GridLayout project2 = new GridLayout(7,5);
		project2.addStyleName("The grid itself represents one project");
		Label project_name = new Label("Project2");
		Label qbic_user = new Label("QBIC user");
		Label description = new Label("Description");
		Label qbic_scientist = new Label("QBIC scientist");
		Label project_date_registration = new Label ("Date of Project Registraion");
		Label experiment1 = new Label("Experiment1");
		Label experiment_description = new Label("Experiment Description");
		Label efdv = new Label("estimated final data volume");
		Label cv = new Label("current volume");
		// Find the application directory
		String basepath = VaadinService.getCurrent()
		                  .getBaseDirectory().getAbsolutePath();
		// Image as a file resource
		FileResource resource_traffic_light_red = new FileResource(new File(basepath +
		                        "/WEB-INF/images/traffic_light_red.PNG"));
		
		FileResource resource_traffic_light_green = new FileResource(new File(basepath +
                "/WEB-INF/images/traffic_light_green.PNG"));	

		// Show the image in the application
		Image experimental_design = new Image("experiment designed", resource_traffic_light_red);
		Image offer_accepted = new Image("offer accepted", resource_traffic_light_green);
		Image data_generated = new Image("data generated", resource_traffic_light_red);
		Image data_processed = new Image("data processed", resource_traffic_light_green);
		Image data_analyzed = new Image("data analyzed", resource_traffic_light_red);
		Image invoice_sent = new Image("invoice sent", resource_traffic_light_green);
		Image project_finished = new Image("project finished", resource_traffic_light_red);
		experimental_design.setHeight("3em");
		offer_accepted.setHeight("3em");
		data_generated.setHeight("3em");
		data_processed.setHeight("3em");
		data_analyzed.setHeight("3em");
		invoice_sent.setHeight("3em");
		project_finished.setHeight("3em");
		project2.addComponent(project_name,0,0);
		project2.addComponent(qbic_user,1,0);
		project2.addComponent(description,2,0);
		project2.addComponent(qbic_scientist,3,0);
		project2.addComponent(project_date_registration,4,0);
		project2.addComponent(experiment1,0,1);
		project2.addComponent(experiment_description,1,1);
		project2.addComponent(efdv,2,1);
		project2.addComponent(cv,3,1);
		project2.addComponent(experimental_design,0,2);
		project2.addComponent(offer_accepted,1,2);
		project2.addComponent(data_generated,2,2);
		project2.addComponent(data_processed,3,2);
		project2.addComponent(data_analyzed,4,2);
		project2.addComponent(new Image("invoice sent", resource_traffic_light_green),5,2);
		project2.addComponent(new Image("project finished", resource_traffic_light_green),6,2);
		/*
		Button button = new Button("Click Me");
		button.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				layout.addComponent(new Label("Thank you for clicking"));
			}
		});*
		//layout.addComponent(table);
		Integer itemId = new Integer(42);
		Button detailsField = new Button("Description");
	    detailsField.setData(itemId);
	    detailsField.addClickListener(new Button.ClickListener() {
	        public void buttonClick(ClickEvent event) {
	            // Get the item identifier from the user-defined data.
	            Integer iid = (Integer)event.getButton().getData();
	            
	            Notification.show("Link " +
	                              iid.intValue() + " clicked.");
	        } 
	    });
	    detailsField.addStyleName("link");
		
		
		GridLayout project3 = new GridLayout(7,20);
		project3.addStyleName("The grid itself represents a second project");
		project3.setMargin(new MarginInfo(true, true, true, true));
		project3.setSpacing(true);
		
		Label project_name2 = new Label("Project 3");
		project3.addComponent(project_name2,0,0);
		project3.addComponent(qbic_user,1,0);
		project3.addComponent(detailsField,2,0);
		project3.addComponent(qbic_scientist,3,0);
		project3.addComponent(project_date_registration,4,0);
		project3.addComponent(experiment1,0,1);
		project3.addComponent(experiment_description,1,1);
		project3.addComponent(efdv,2,1);
		project3.addComponent(cv,3,1);
		project3.addComponent(experimental_design,0,2);
		project3.setComponentAlignment(experimental_design, Alignment.MIDDLE_CENTER);
		project3.addComponent(offer_accepted,1,2);
		project3.setComponentAlignment(offer_accepted, Alignment.MIDDLE_CENTER);
		project3.addComponent(data_generated,2,2);
		project3.setComponentAlignment(data_generated, Alignment.MIDDLE_CENTER);
		project3.addComponent(data_processed,3,2);
		project3.setComponentAlignment(data_processed, Alignment.MIDDLE_CENTER);
		project3.addComponent(data_analyzed,4,2);
		project3.setComponentAlignment(data_analyzed, Alignment.MIDDLE_CENTER);
		project3.addComponent(invoice_sent,5,2);
		project3.setComponentAlignment(invoice_sent, Alignment.MIDDLE_CENTER);
		project3.addComponent(project_finished,5,0);
		project3.setComponentAlignment(project_finished, Alignment.MIDDLE_CENTER);
		Label space1 = new Label("<div style=\"font-size:xx-small; border-color:blue;border-style:dotted hidden dashed hidden;\">&nbsp;</div>", Label.CONTENT_XHTML);
		space1.setHeight("1em");
		Label space2 = new Label("<hr width=\"75%\">",Label.CONTENT_XHTML);
		project3.addComponent(space1,0,3,6,3);
		project3.addComponent(new Label("<div style=\"border&#58; solid 20px #ff0000;\"><b>Experiment2</b></div>",Label.CONTENT_XHTML), 0, 4);
		project3.addComponent(new Label("<div style=\"font-size: large; border-color:blue;border-style:dotted hidden dashed hidden;\">Borders Test in HTML.</div>",Label.CONTENT_XHTML), 1, 4);
		project3.addComponent(space2,0,5,6,5);
		List<Project> projects = getProjects();
		project3.addComponents(new Label("Openbis:"));
		project3.addComponents(new Label(Integer.toString(projects.size())));
		
		for (Project project : projects)
		{
		    project3.addComponent(new Label(project.getCode()));
		    List<Experiment> exp = this.getExperimentsForProject(project);
		    for(Experiment e : exp){
		    	project3.addComponent(new Label(e.getCode()));
		    }
		}
		Accordion acco1 = new Accordion();
		//acco1.addTab(project1,"1");
		acco1.addTab(project2,"2");
		//acco1.addTab(project3,"3");
		//acco1.addComponent(project2);
		acco1.addComponent(project3);
		layout.addComponent(project2);
		//layout.addComponent(table3);
		layout.addComponent(project3);

		//layout.addComponent();
	}
	
	//TODO set all attributes
	public Label cloneLabel(Label label){
		Label ret = new Label();
		ret.setCaption(label.getCaption());
		ret.setEnabled(label.isEnabled());
		return label;
	}
	
	public List<Project> getProjects(){
		IOpenbisServiceFacade facade = OpenbisServiceFacadeFactory.tryCreate("wojnar", "opk49x3z", "https://qbis.informatik.uni-tuebingen.de:8443/openbis", 60000); 
		List<Project> projects = facade.listProjects();
		facade.logout();
		return projects;
	}
	public List<Experiment> getExperimentsForProject(Project project){
		IOpenbisServiceFacade facade = OpenbisServiceFacadeFactory.tryCreate("wojnar", "opk49x3z", "https://qbis.informatik.uni-tuebingen.de:8443/openbis", 60000); 
		List<String> temp = new ArrayList<String>();
		temp.add(project.getIdentifier());
		List<Experiment> experiments = facade.listExperimentsForProjects(temp);
		facade.logout();
		return experiments;		
	}*/
	

}