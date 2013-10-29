package com.example.vaadindashboardtest;

import java.io.File;
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
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
@Theme("liferay")
public class VaadindashboardtestUI extends UI {

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = VaadindashboardtestUI.class)
	public static class Servlet extends VaadinServlet {
	}

	final Button progressButton = new Button("Update Dashboard");
	final ProgressBar progressBar = new ProgressBar(new Float(0.0));
    VerticalLayout projectLayout = new VerticalLayout();
	@Override
	protected void init(VaadinRequest request) {
		final VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
		
		setContent(layout);
		
		//layout.removeAllComponents();
		//this.initProgressDisplay(layout);
		this.thread(layout);
		
	}
	void thread2(VerticalLayout layout) {
        // BEGIN-EXAMPLE: component.progressbar.thread
        HorizontalLayout barbar = new HorizontalLayout();
        layout.addComponent(barbar);
       
        // Create the indicator, disabled until progress is started
        final ProgressBar progress = new ProgressBar(new Float(0.0));
        progress.setEnabled(false);
        barbar.addComponent(progress);
       
        final Label status = new Label("not running");
        barbar.addComponent(status);

        // A button to start progress
        final Button button = new Button("Click to start");
        layout.addComponent(button);

        // A thread to do some work
        class WorkThread extends Thread {
            // Volatile because read in another thread in access()
            volatile double current = 0.0;

        @Override
            public void run() {
                // Count up until 1.0 is reached
                while (current < 1.0) {
                    current += 0.01;

                    // Do some "heavy work"
                    try {
                        sleep(50); // Sleep for 50 milliseconds
                    } catch (InterruptedException e) {}

                    // Update the UI thread-safely
                    UI.getCurrent().access(new Runnable() {
                        @Override
                        public void run() {
                            progress.setValue(new Float(current));
                            if (current < 1.0)
                                status.setValue("" +
                                    ((int)(current*100)) + "% done");
                            else
                                status.setValue("all done");
                        }
                    });
                }
               
                // Show the "all done" for a while
                try {
                    sleep(2000); // Sleep for 2 seconds
                } catch (InterruptedException e) {}

                // Update the UI thread-safely
                UI.getCurrent().access(new Runnable() {
                    @Override
                    public void run() {
                        // Restore the state to initial
                        progress.setValue(new Float(0.0));
                        progress.setEnabled(false);
                               
                        // Stop polling
                        UI.getCurrent().setPollInterval(-1);
                       
                        button.setEnabled(true);
                        status.setValue("not running");
                    }
                });
            }
        }

        // Clicking the button creates and runs a work thread
        button.addClickListener(new Button.ClickListener() {
            private static final long serialVersionUID = -1639461207460313184L;

            public void buttonClick(ClickEvent event) {
                final WorkThread thread = new WorkThread();
                thread.start();

                // Enable polling and set frequency to 0.5 seconds
                UI.getCurrent().setPollInterval(500);

                // Disable the button until the work is done
                progress.setEnabled(true);
                button.setEnabled(false);

                status.setValue("running...");
            }
        });
    // END-EXAMPLE: component.progressbar.thread
}
	
	
	void thread(final VerticalLayout layout) {
	        // BEGIN-EXAMPLE: component.progressbar.thread
	        HorizontalLayout barbar = new HorizontalLayout();
	        layout.addComponent(barbar);
	       
	        // Create the indicator, disabled until progress is started
	        final ProgressBar progress = new ProgressBar(new Float(0.0));
	        progress.setEnabled(false);
	        barbar.addComponent(progress);
	       
	        final Label status = new Label("not running");
	        barbar.addComponent(status);
	
	        // A button to start progress
	        final Button button = new Button("Update Dashboard");
	        layout.addComponent(button);
	        
	        projectLayout.setMargin(true);
	        projectLayout.setSpacing(true);
	        layout.addComponent(projectLayout);
	        // A thread to do some work
	        class WorkThread extends Thread {
	            // Volatile because read in another thread in access()
	            volatile double current = 0.0;
	            volatile double projectsRead = 0.0;
	
            @Override
	            public void run() {
        			layout.removeComponent(projectLayout);
        			projectLayout = new VerticalLayout();
        			projectLayout.setMargin(true);
        			projectLayout.setSpacing(true);
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
        				projectsRead++;
        				//Update progressBar
        				//System.out.println("before");
        				//System.out.println(current);
        				current = projectsRead/projects.size();
        				//System.out.println(projectsRead);
        				//System.out.println(current);
        				
        				
        				//do the heavy work
        				ProjectLayout pL = new ProjectLayout(project,resource_traffic_light_red,resource_traffic_light_green);
        				projectLayout.addComponent(pL);
                        // Do some "heavy work"
                        try {
                            sleep(50); // Sleep for 50 milliseconds
                        } catch (InterruptedException e) {}

        				UI.getCurrent().access(new Runnable (){
        					public void run(){
        						progress.setValue(new Float(current));
                                if (current < 1.0)
                                    status.setValue("" +
                                        ((int)(current*100)) + "% done");
                                else
                                    status.setValue("all done");
        					}
        				});
        			}
                    // Show the "all done" for a while
                    try {
                        sleep(2000); // Sleep for 2 seconds
                    } catch (InterruptedException e) {}
        			UI.getCurrent().access(new Runnable(){
        				public void run(){
        					progress.setValue(new Float(0.0));
        					progress.setEnabled(false);
        					progress.setIndeterminate(false);
        					UI.getCurrent().setPollInterval(-1);
        					button.setEnabled(true);      					
        					layout.addComponent(projectLayout);
                            status.setValue("not running");
        					
        				}
        			});
        		}
	        }
	
	        // Clicking the button creates and runs a work thread
	        button.addClickListener(new Button.ClickListener() {
	            private static final long serialVersionUID = -1639461207460313184L;
	
	            public void buttonClick(ClickEvent event) {
	                final WorkThread thread = new WorkThread();
	                thread.start();
	
	                // Enable polling and set frequency to 0.5 seconds
	                UI.getCurrent().setPollInterval(500);
	
	                // Disable the button until the work is done
	                progress.setEnabled(true);
	                progress.setIndeterminate(true);
	                button.setEnabled(false);
	
	                status.setValue("Connecting to OpenBis");
	            }
	        });
        // END-EXAMPLE: component.progressbar.thread
	}
	
	
	
	public void initProgressDisplay(final VerticalLayout layout){
		HorizontalLayout progress = new HorizontalLayout();
		progress.setSpacing(true);
		progress.addComponent(this.progressButton);
		this.progressBar.setValue(new Float(0.0));
		this.progressBar.setEnabled(false);
		progress.addComponent(progressBar);
		this.projectLayout.setMargin(true);
		this.projectLayout.setSpacing(true);
		layout.addComponent(projectLayout);
		
		this.progressButton.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				final RefreshDashboard thread = new RefreshDashboard(layout, projectLayout,progressButton,progressBar);
				thread.start();
				UI.getCurrent().setPollInterval(1000);
				progressBar.setEnabled(true);
				progressButton.setEnabled(false);
				System.out.println("Now there are " + projectLayout.getComponentCount() + " number of project in the Layout");
			}
		});	
		
		layout.addComponent(progress);
	}
	
	
	class RefreshDashboard extends Thread {
		volatile double current = 0.0;
		final VerticalLayout mainLayout;
		VerticalLayout projectLayout;
		final Button button;
		final ProgressBar progress;
		
		RefreshDashboard(VerticalLayout mainLayout,  VerticalLayout projectLayout, final Button button,final ProgressBar progress){
			this.mainLayout = mainLayout;
			this.projectLayout = projectLayout;
			this.button = button;
			this.progress = progress;
			mainLayout.removeComponent(projectLayout);
		}
		public void run(){
			final VerticalLayout tmpLayout = new VerticalLayout();
			tmpLayout.setMargin(this.projectLayout.getMargin());
			tmpLayout.setSpacing(this.projectLayout.isSpacing());
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
			
			double step = (double)projects.size() / 100.0;
			
			for(OpenBisProject project  : projects){
				ProjectLayout pL = new ProjectLayout(project,resource_traffic_light_red,resource_traffic_light_green);
				projectLayout.addComponent(pL);
				
				//Update progressBar
				current =+ step;
				UI.getCurrent().access(new Runnable (){
					public void run(){
						progress.setValue(new Float(current));
					}
				});
			}
			
			UI.getCurrent().access(new Runnable(){
				public void run(){
					progress.setValue(new Float(0.0));
					progress.setEnabled(false);
					UI.getCurrent().setPollInterval(-1);
					button.setEnabled(true);
					mainLayout.addComponent(tmpLayout);
					projectLayout = tmpLayout;
				}
			});
		}//END run()
	}//END RefreshDashboard
	
	

	public void getProjectsWithProgressBar(final VerticalLayout mainLayout,final Button button,final ProgressBar progress){
		
		
		class WorkThread extends Thread {
			volatile double current = 0.0;
			
			public void run(){
				final VerticalLayout layout = new VerticalLayout();
				layout.setMargin(true);
				layout.setSpacing(true);
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
				
				double step = (double)projects.size() / 100.0;
				
				for(OpenBisProject project  : projects){
					ProjectLayout projectLayout = new ProjectLayout(project,resource_traffic_light_red,resource_traffic_light_green);
					layout.addComponent(projectLayout);
					
					//Update progressBar
					current =+ step;
					UI.getCurrent().access(new Runnable (){
						public void run(){
							progress.setValue(new Float(current));
						}
					});
				}
				
				UI.getCurrent().access(new Runnable(){
					public void run(){
						progress.setValue(new Float(0.0));
						progress.setEnabled(false);
						UI.getCurrent().setPollInterval(-1);
						button.setEnabled(true);
						mainLayout.addComponent(layout);
					}
				});
			}//END run()
		}//END WorkThread
		
		button.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				final WorkThread thread = new WorkThread();
				thread.start();
				UI.getCurrent().setPollInterval(1000);
				progress.setEnabled(true);
				button.setEnabled(false);
			}
		});
		
	}
	
	
	
	public VerticalLayout getProjects(){
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
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
		return layout;
	}
}


/*package com.example.vaadindashboardtest;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;


@SuppressWarnings("serial")
@Theme("liferay")
public class VaadindashboardtestUI extends UI {

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = VaadindashboardtestUI.class)
	public static class Servlet extends VaadinServlet {
	}

	@Override
	protected void init(VaadinRequest request) {
		final VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		setContent(layout);

		Button button = new Button("Click Me");
		button.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				layout.addComponent(new Label("Thank you for clicking"));
			}
		});
		layout.addComponent(button);
	}

}*/