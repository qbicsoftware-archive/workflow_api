package com.example.visualizer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.portlet.PortletRequest;
import javax.servlet.annotation.WebServlet;

import org.vaadin.artur.icepush.ICEPush;

import ch.systemsx.cisd.openbis.dss.client.api.v1.DataSet;
import ch.systemsx.cisd.openbis.dss.client.api.v1.IOpenbisServiceFacade;
import ch.systemsx.cisd.openbis.dss.generic.shared.api.v1.FileInfoDssDTO;
import ch.systemsx.cisd.openbis.generic.shared.api.v1.dto.Experiment;
import ch.systemsx.cisd.openbis.generic.shared.api.v1.dto.Project;
import ch.systemsx.cisd.openbis.plugin.proteomics.client.api.v1.FacadeFactory;
import ch.systemsx.cisd.openbis.plugin.proteomics.client.api.v1.IProteomicsDataApiFacade;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.service.UserServiceUtil;

import com.qbic.openbismodel.OpenBisClient;
import com.qbic.util.CommandLine;
import com.qbic.util.ConfigurationManager;
import com.qbic.util.DashboardUtil;
import com.qbic.util.VisualConfigurationManager;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.FilesystemContainer;
import com.vaadin.data.util.TextFileProperty;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FileResource;
import com.vaadin.server.Page.BrowserWindowResizeEvent;
import com.vaadin.server.Page.BrowserWindowResizeListener;
import com.vaadin.server.Sizeable;
import com.vaadin.server.StreamResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinPortletService;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.CloseHandler;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;



@SuppressWarnings("serial")
@Theme("liferay")
public class VisualizerUI extends UI {

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = VisualizerUI.class)
	public static class Servlet extends VaadinServlet {
	}
	
	
	private Panel layout;
	HorizontalSplitPanel hsplit;
	private Table table;
	private List<File> folderToDelete;
	private int h;
	private int w;
	/**
	 * returns a vaadin conform string for sizing components in pixel.
	 * @param pixel - number of pixels. Can be either height or width.
	 * @return vaadin conform string
	 */
	private String intPixelToStringPixel(int pixel){
		return String.valueOf(pixel) + "px";
	}
	/**
	 * setSize in pixel. Width will be reduced to 95 % of the original w.
	 * h and w are supposed to be the height and width of the browser, respectively. 
	 * Hence, width is reduced. Because the portlet should fit into the browser. Height is done automatically.
	 * @param h
	 * @param w
	 */
	public void setSize(int h, int w){
		layout.setHeight(this.intPixelToStringPixel(h));
		float wTmp = (float) (0.95*(float)w);
		System.out.println(w + " " + wTmp);
		layout.setWidth(this.intPixelToStringPixel((int)wTmp));
		System.out.println(layout.getHeight() + " " + layout.getWidth());
		this.setH(h);
		this.setW(w);
	}
	
	@Override
	protected void init(VaadinRequest request) {
		this.buildMainLayout();
		this.folderToDelete = new ArrayList<File>();
		System.out.println("version 5");
		
		//TODO change with setTable for production
		this.setTable(request);
		final TabSheet tabsheet = new TabSheet();
		System.out.println("hsplit width: " + this.hsplit.getWidth() +", hsplit height " + this.hsplit.getHeight() +" split position" + this.hsplit.getSplitPosition());
		table.addValueChangeListener(new Property.ValueChangeListener() {
		    @Override
			public void valueChange(ValueChangeEvent event) {
				System.out.println(event.getProperty().getValue());
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

				/*tabsheet.setCloseHandler(new CloseHandler() {
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
		});
		
		hsplit.setFirstComponent(this.table);
		hsplit.setSecondComponent(tabsheet);
		float wTmp = (float) this.layout.getWidth();
		wTmp *= 0.25; 
		hsplit.setSplitPosition(wTmp, this.layout.getWidthUnits());
	}
	
	private void buildMainLayout(){
		this.layout = new Panel("Quality Control Panel");
		this.setSize(this.getPage().getBrowserWindowHeight(),this.getPage().getBrowserWindowWidth());
		hsplit = new HorizontalSplitPanel();
		layout.setContent(hsplit);
		this.setContent(layout);
		//Use full size of the browser 
		this.getPage().addBrowserWindowResizeListener(new MyBrowserWindowResizeListener());
	}
	
	private boolean deleteFolder(File folder){
		
		if(folder.isDirectory()){
			File [] subFiles = folder.listFiles();
			for(int i = 0; i < subFiles.length;++i){
				deleteFolder(subFiles[i]);
			}
		}
		System.out.println("Deleting: " + folder.getAbsolutePath());
		return folder.delete();
		
	}
	
	private String getFastQC(String datasetCode){
		//VisualConfigurationManager manager = VisualConfigurationManager.getInstance();
		//IOpenbisServiceFacade facade = OpenbisServiceFacadeFactory.tryCreate(manager.getDataSourceUser(), manager.getDataSourcePassword(), "https://qbis.informatik.uni-tuebingen.de:8443/openbis", 60000);
		OpenBisClient openbisClient = new OpenBisClient("wojnar", "opk49x3z", "https://qbis.informatik.uni-tuebingen.de:8443/openbis", true);
		IOpenbisServiceFacade facade = openbisClient.getFacade();
		DataSet dataset = facade.getDataSet(datasetCode);

		FileInfoDssDTO[] filelist = dataset.listFiles("original", false);

		System.out.println("getPathInListing: " +  filelist[0].getPathInListing() + "; getPathInDataSet: " + filelist[0].getPathInDataSet());
		System.out.println("getExternalDataSetCode: " + dataset.getExternalDataSetCode()  + " ; getExternalDataSetLink: " +  dataset.getExternalDataSetLink() + " ; tryGetInternalPathInDataStore: " + dataset.getDataSetDss().tryGetInternalPathInDataStore());

		InputStream streamy = dataset.getFile(/*"/" + dataset.getDataSetDss().tryGetInternalPathInDataStore() + "/" +*/  filelist[0].getPathInDataSet());
		//DownloadStream tmp = new DownloadStream(streamy,"attachment",datasetType);
		///System.out.println(tmp.getStream().toString());
		openbisClient.logout();
		
		
		// create a buffer to improve copy performance later.
		byte[] buffer = new byte[2048];
        
        // open the zip file stream
        ZipInputStream stream = new ZipInputStream(streamy);
        String outdir = "/home/guseuser/guse/apache-tomcat-6.0.36/webapps/ROOT/html/VAADIN/themes/liferay";// + datasetCode;
        String ret = "";
        try
        {
            // now iterate through each item in the stream. The get next
            // entry call will return a ZipEntry for each file in the
            // stream
            ZipEntry entry;
            try {
				while((entry = stream.getNextEntry())!=null)
				{
				    String s = String.format("Entry: %s len %d added %TD",
				                    entry.getName(), entry.getSize(),
				                    new Date(entry.getTime()));
				    System.out.println(s);
				    
				    // Once we get the entry from the stream, the stream is
				    // positioned read to read the raw data, and we keep
				    // reading until read returns 0 or less.
				    String outpath = outdir + "/" + entry.getName();
				    File zippedFile = new File(outpath);
				    if(entry.isDirectory()){
				    	zippedFile.mkdir();
				    	continue;
				    }
				    if(zippedFile.getName().equals("fastqc_report.html")){
				    	ret = entry.getName();
				    	folderToDelete.add(zippedFile.getParentFile());
				    }
				    FileOutputStream output = null;
				    try
				    {
				        output = new FileOutputStream(zippedFile);
				        int len = 0;
				        while ((len = stream.read(buffer)) > 0)
				        {
				            output.write(buffer, 0, len);
				        }
				    }
				    finally
				    {
				        // we must always close the output file
				        if(output!=null) output.close();
				    }
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        finally
        {
            // we must always close the zip file.
            try {
				stream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				stream = null;
				e.printStackTrace();
			}
        }
        return ret;
	}
	
	private void getFastqcViaCommandLine(){
		CommandLine cmdLine = new CommandLine();
		cmdLine.addCmd("getdataset -o /home/guseuser/guse/apache-tomcat-6.0.36/webapps/ROOT/html/VAADIN/ -r /home/guseuser/guse/apache-tomcat-6.0.36/webapps/ROOT/html/VAADIN/tab1 20130723123759231-449");
		Process p = cmdLine.execute();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				p.getInputStream()));
		
		ArrayList<String> list = new ArrayList<String>();
		String line = null;
		try {
			line = reader.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		list.add(line);
		while (line != null) {
			try {
				line = reader.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			list.add(line);
		}

		if (list.size() > 0) {
			for (String ip : list) {
				System.out.println(ip);
			}
		} else {
			System.out.println("No Output");
		}
		
		cmdLine.clear();
		cmdLine.addCmd("unzip /home/guseuser/guse/apache-tomcat-6.0.36/webapps/ROOT/html/VAADIN/20130723123759231-449/QSNYD001X5_fastqc.zip");
		p = cmdLine.execute();
		reader = new BufferedReader(new InputStreamReader(
				p.getInputStream()));
		
		list = new ArrayList<String>();
		line = null;
		try {
			line = reader.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		list.add(line);
		while (line != null) {
			try {
				line = reader.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			list.add(line);
		}

		if (list.size() > 0) {
			for (String ip : list) {
				System.out.println(ip);
			}
		} else {
			System.out.println("No Output");
		}
		
		
	}
	
	
	private void setDummyTable(VaadinRequest request){
		table = new Table();
		table.addContainerProperty("CODE", String.class, "");
		table.addContainerProperty("Data Set Type", String.class, "");
		table.addContainerProperty("Experiment", String.class, "");
		table.addContainerProperty("Registration date", String.class, "");
		table.addContainerProperty("Size", String.class,"");
		table.addContainerProperty("Name",String.class,"");
		table.setSelectable(true);
		table.setImmediate(true);
		table.addItem(new Object[] {
			    "123","zip","QBIC/test1", "13-11-2013","2.1 GiB", "dummy1.zip"},new Integer(1));
		
		table.addItem(new Object[] {
			    "456","zip","QBIC/test1", "13-11-2013","2.1 GiB", "dummy2.zip"},new Integer(2));
		
		table.addItem(new Object[] {
			    "789","qcML","QBIC/test1", "13-11-2013","2.1 GiB", "dummy3.zip"},new Integer(3));
		
		table.addItem(new Object[] {
			    "012","fastq","QBIC/test1", "13-11-2013","2.1 GiB", "dummy4.zip"},new Integer(4));
		table.setSizeFull();
		
	}
	
	private void setTable(VaadinRequest request){
		
		ConfigurationManager manager = ConfigurationManager.getInstance();
		Map userInfoMap = (Map)  request.getAttribute(PortletRequest.USER_INFO);
    	//explode city gots to figure out how to deal with it not being in the map
    	String locationId = (String) userInfoMap.get("liferay.location.id");
    	String companyId = (String) userInfoMap.get("liferay.company.id");
    	String userName = (String) userInfoMap.get("liferay.user.id");
    	String screenName = "dumdidum";
    	try {
			screenName = UserServiceUtil.getUserById(Long.parseLong(userName)).getScreenName();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PortalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		table = new Table();
		table.addContainerProperty("CODE", String.class, "");
		table.addContainerProperty("Data Set Type", String.class, "");
		table.addContainerProperty("Experiment", String.class, "");
		table.addContainerProperty("Registration date", String.class, "");
		table.addContainerProperty("Size", String.class,"");
		table.addContainerProperty("Name",String.class,"");
		//Allow selection
		table.setSelectable(true);
		// Trigger selection change events immediately
		//Unfortunately in version 7.1.7 one still has to set this true, otherwise selection can be pretty buggy
		table.setImmediate(true);
		System.out.println("starting");
		OpenBisClient openbisClient = new OpenBisClient("wojnar", "opk49x3z", "https://qbis.informatik.uni-tuebingen.de:8443/openbis", false);
		IOpenbisServiceFacade facade = openbisClient.getFacade();
		//IOpenbisServiceFacade facade = OpenbisServiceFacadeFactory.tryCreate(manager.getDataSourceUser(), manager.getDataSourcePassword(), "https://qbis.informatik.uni-tuebingen.de:8443/openbis", 60000);
		IProteomicsDataApiFacade facade2 = FacadeFactory.create(manager.getDataSourceURL(), manager.getDataSourceUser(),  manager.getDataSourcePassword() );
		List<Project> projects = facade2.listProjects(screenName);
		facade2.logout();
		int i = 0;
		int j = 0;
		int k = 0;
		for(Project project: projects){
			List<String> temp = new ArrayList<String>();
			temp.add(project.getIdentifier());
			List<Experiment> experiments = facade.listExperimentsForProjects(temp);

			k++;
			for(Experiment experiment: experiments){
				//List<DataSet> datasets = facade.listDataSetsForExperiment(experiment.getIdentifier());
				ArrayList<String> eee = new ArrayList<String>();
				eee.add(experiment.getIdentifier());
				//System.out.println(facade.listSamplesForExperiments(eee).size());
				//System.out.println(facade.listDataSetsForExperiments(eee).size());
				List<DataSet> datasets = facade.listDataSetsForExperiments(eee);
				j++;
				for (DataSet ds : datasets){
					String code  = ds.getCode();
					String exp_id = ds.getExperimentIdentifier();
					Date date = ds.getRegistrationDate();
					String ds_type = ds.getDataSetTypeCode();

					FileInfoDssDTO[] filelist = ds.listFiles("original", false);
					String dataSetSize = DashboardUtil.humanReadableByteCount(filelist[0].getFileSize(),false);
					String datasetName = filelist[0].getPathInListing();
					if(ds_type.toLowerCase().equals("zip")){
						table.addItem(new Object[] {
							    code,ds_type,exp_id, date.toString(),dataSetSize, datasetName},new Integer(i));	
					}

					++i;
				}
			}
		}
		facade.logout();
	}
	
	
	/*
	@Override
	protected void init(VaadinRequest request) {
		final ICEPush pusher = new ICEPush();
		final Label label  =new Label();
		label.setValue("browser width: " + this.getPage().getBrowserWindowWidth() + " browser height: " + this.getPage().getBrowserWindowHeight());
		this.getPage().addBrowserWindowResizeListener(new BrowserWindowResizeListener(){

			@Override
			public void browserWindowResized(BrowserWindowResizeEvent event) {
				// TODO Auto-generated method stub
				System.out.println("browser width: " + event.getWidth() + " browser height: " + event.getHeight());
				label.setValue("browser width: " + event.getWidth() + " browser height: " + event.getHeight());
			}
			
		});
		
		this.setContent(label);
	}	
	*/
	
	//TODO delete all unused files at the end. IN close or detach???
	/*public void close(){
		
		super.close();
	}*/
	
	public void detach(){
		for(File file: this.folderToDelete){
			this.deleteFolder(file);
		}
		super.detach();
	}
	
	
	public int getH() {
		return h;
	}
	public void setH(int h) {
		this.h = h;
	}


	public int getW() {
		return w;
	}
	public void setW(int w) {
		this.w = w;
	}


	class MyBrowserWindowResizeListener implements BrowserWindowResizeListener{
		@Override
		public void browserWindowResized(BrowserWindowResizeEvent event) {
			// TODO Auto-generated method stub
			System.out.println("browser width: " + event.getWidth() + " browser height: " + event.getHeight());
			VisualizerUI ui = (VisualizerUI)UI.getCurrent();
			ui.setSize(event.getHeight(),event.getWidth());
		}	
	}
	
	/*	
	FilesystemContainer docs = new FilesystemContainer(new File("/home/guseuser/dummyDropbox/ALL_GS1000692_fastqc"));
	Table docList = new Table("Documents", docs);
	Label docView = new Label("", ContentMode.HTML);
	BrowserFrame browser;

	@Override
	protected void init(VaadinRequest request) {
		//HorizontalSplitPanel split = new HorizontalSplitPanel();
		//setContent(split);
		//split.addComponent(docList);
		//split.addComponent(docView);
		final VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		setContent(layout);
		
		// Have a panel to put stuff in
		File base = request.getService().getBaseDirectory();
		Label basePath = new Label(base.getAbsolutePath());
		Panel panel = new Panel(base.getAbsolutePath());
		panel.setHeight("1200px");
		panel.setWidth("100%");
		// Have a horizontal split panel as its content
		HorizontalSplitPanel hsplit = new HorizontalSplitPanel();
		panel.setContent(hsplit);
		//Label firstComponent = new Label("ls -la tralalala");
		Process p = CommandLine.executeCmd("ls -la");
		
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			
			ArrayList<String> list = new ArrayList<String>();
			String line = reader.readLine();
			list.add(line);
			while (line != null) {
				line = reader.readLine();
				list.add(line);
			}
 
			if (list.size() > 0) {
				for (String ip : list) {
					System.out.println(ip);
				}
			} else {
				System.out.println("No Output");
			}

		hsplit.setFirstComponent(new Label(base.getAbsolutePath()));
		
		//FileResource baseDirFile = new FileResource(new File(base.getAbsolutePath() + "/" + "ALL_GS1000692_fastqc/fastqc_report.html"));
		FileResource baseDirFile = new FileResource(new File("fastqc_report.html"));
		System.out.println(baseDirFile.getFilename());
		System.out.println(baseDirFile.getSourceFile().getAbsolutePath());
		System.out.println("2");
		ThemeResource themeResource = new ThemeResource("ALL_GS1000692_fastqc/fastqc_report.html");
		System.out.println(themeResource.toString());
		browser = new BrowserFrame("Name is : " , themeResource);//baseDirFile ); //new ExternalResource(url) /*For some reason the file is not shown :/*
		System.out.println("3");
		browser.setWidth("100%");
		System.out.println("4");
		browser.setHeight("1200px");
		System.out.println("5");
		//layout.addComponent(browser);
		hsplit.setSecondComponent(browser);
		System.out.println("6");
		layout.addComponent(panel);
		System.out.println("7");
	}*/

}

