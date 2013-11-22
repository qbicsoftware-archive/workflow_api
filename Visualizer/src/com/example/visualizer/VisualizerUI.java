package com.example.visualizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.portlet.PortletRequest;
import javax.servlet.annotation.WebServlet;

import ch.systemsx.cisd.openbis.dss.client.api.v1.DataSet;
import ch.systemsx.cisd.openbis.dss.client.api.v1.IOpenbisServiceFacade;
import ch.systemsx.cisd.openbis.dss.generic.shared.api.v1.FileInfoDssDTO;
import ch.systemsx.cisd.openbis.generic.shared.api.v1.IGeneralInformationService;
import ch.systemsx.cisd.openbis.generic.shared.api.v1.dto.Experiment;
import ch.systemsx.cisd.openbis.generic.shared.api.v1.dto.Project;
import ch.systemsx.cisd.openbis.generic.shared.api.v1.dto.SearchCriteria;
import ch.systemsx.cisd.openbis.generic.shared.api.v1.dto.SearchCriteria.MatchClause;
import ch.systemsx.cisd.openbis.generic.shared.api.v1.dto.SearchCriteria.MatchClauseAttribute;
import ch.systemsx.cisd.openbis.plugin.proteomics.client.api.v1.FacadeFactory;
import ch.systemsx.cisd.openbis.plugin.proteomics.client.api.v1.IProteomicsDataApiFacade;

import ch.systemsx.cisd.openbis.common.api.client.ServiceFinder;


import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.service.UserServiceUtil;
import com.qbic.Listeners.MyBrowserWindowResizeListener;
import com.qbic.openbismodel.OpenBisClient;
import com.qbic.util.DashboardUtil;
import com.qbic.util.VisualConfigurationManager;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.server.Sizeable;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
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
	MyHorizontalSplitPanel hsplit;
	private Table table;
	private List<File> folderToDelete;
	public HashMap<String, BrowserFrame> frames;
	private int h;
	private int w;
	String liferayScreenName;
	
	@Override
	protected void init(VaadinRequest request) {
		//getliferay screen name. It will be used to get only the datasets from projects that user is allowed to see.
		initLiferayScreenName(request);
		//initialize main layout
		this.buildMainLayout();
		this.folderToDelete = new ArrayList<File>();
		//initialize table
		this.setTable2();
		VerticalLayout firstComponent = new VerticalLayout();
		final CheckBox checkbox1 = new CheckBox("Open results in new Window or Tab. Browser settings might need adjustments. E.g. allow jacascript at least temporarily.");
		checkbox1.setImmediate(true);
		firstComponent.addComponent(this.table);
		firstComponent.addComponent(checkbox1);
		final TabSheet tabsheet = new TabSheet();
		table.addValueChangeListener(new Property.ValueChangeListener() {
		    @Override
			public void valueChange(ValueChangeEvent event) {
		    	String datasetCode = "";
		    	String fastqc = "";
		    	System.out.println("checkbox set : " + checkbox1.getValue());
		    	try{
		    		datasetCode = (String)table.getItem(event.getProperty().getValue()).getItemProperty("CODE").getValue();	
		    	}
		    	catch (Exception e){
		    		//TODO: Probably no Value selected, but print stack in log to be sure.
		    		return;
		    	}
		    	
		    	VisualizerUI ui =(VisualizerUI) UI.getCurrent();
		    	if(!ui.frames.containsKey(datasetCode)){
			    	fastqc = ui.getFastQC(datasetCode);
					ThemeResource themeResource = new ThemeResource(fastqc);
					BrowserFrame browser = new BrowserFrame(fastqc , themeResource);
					System.out.println("UI.getCurrent().getContent() width: " + UI.getCurrent().getContent().getWidth() +", UI.getCurrent() height " + UI.getCurrent().getContent().getHeight());
					System.out.println("Before change: browser width: " + browser.getWidth() +", browser height " + browser.getHeight());
					browser.setWidth(((VisualizerUI)UI.getCurrent()).getW(),Sizeable.UNITS_PIXELS);
					browser.setHeight(((VisualizerUI)UI.getCurrent()).getH()-1,Sizeable.UNITS_PIXELS);
					System.out.println("After change: browser width: " + browser.getWidth() +", browser height " + browser.getHeight());
					ui.frames.put(datasetCode, browser);
					
					tabsheet.addComponent(browser);			
					tabsheet.getTab(browser).setClosable(true);
					tabsheet.getTab(browser).setCaption("Fastq Quality Control");
					tabsheet.setSelectedTab(browser);
					if(checkbox1.getValue()){
				    	JavaScript javaScript = JavaScript.getCurrent();
				    	String url = "/html/VAADIN/themes/liferay/" + fastqc;
				    	System.out.println("from existing fastqc: " + url);
				    	javaScript.execute("var win=window.open(\'"+ url + "\');win.focus();");	    	
			    	}
				}
				else{
					BrowserFrame frame = ui.frames.get(datasetCode);
					Tab tab = tabsheet.getTab(frame);
					if(tab == null){
						tabsheet.addComponent(frame);			
						tabsheet.getTab(frame).setClosable(true);
						tabsheet.getTab(frame).setCaption("Fastq Quality Control");
					}
					tabsheet.setSelectedTab(frame);
			    	if(checkbox1.getValue()){
				    	JavaScript javaScript = JavaScript.getCurrent();
				    	String url = "/html/VAADIN/themes/liferay/" + frame.getCaption();
				    	System.out.println("from existing frame: " + url);
				    	javaScript.execute("var win=window.open(\'"+ url + "\', \'_blank\');win.focus();");	    	
			    	}
				}

    	    }
		});
		
		hsplit.setFirstComponent(firstComponent);
		hsplit.setSecondComponent(tabsheet);
		//hsplit.addSplitterClickListener(new MySplitClickListener());
		hsplit.addSplitPositionChangeListener(new SplitPositionChangeListener());
		float wTmp = this.layout.getWidth();
		wTmp *= 0.2;
		hsplit.setSplitPosition(wTmp, this.layout.getWidthUnits());
	}
	
	private void buildMainLayout(){
		this.layout = new Panel("Quality Control Panel");
		this.hsplit =new MyHorizontalSplitPanel();
			
		
		this.frames = new HashMap<String,BrowserFrame>();
		this.setSize(this.getPage().getBrowserWindowHeight(),this.getPage().getBrowserWindowWidth());
		layout.setContent(hsplit);
		this.setContent(layout);
		//Adjust portlet to the size of the browser 
		this.getPage().addBrowserWindowResizeListener(new MyBrowserWindowResizeListener());
	}
	
	
	/**
	 * Deletes the File. If File is a folder it is deleted recursively.
	 * @param file - File or Folder. 
	 * @return true if the parameter file could be deleted.
	 */
	private boolean deleteFile(File file){
		
		if(file.isDirectory()){
			File [] subFiles = file.listFiles();
			for(int i = 0; i < subFiles.length;++i){
				deleteFile(subFiles[i]);
			}
		}
		System.out.println("Deleting: " + file.getAbsolutePath());
		return file.delete();
		
	}
	
	private String getFastQC(String datasetCode){
		VisualConfigurationManager manager = VisualConfigurationManager.getInstance();
		OpenBisClient openbisClient = new OpenBisClient(manager.getDataSourceUser(),manager.getDataSourcePassword(), manager.getDataSourceURL(), false);
		IOpenbisServiceFacade facade = openbisClient.getFacade();
		DataSet dataset = facade.getDataSet(datasetCode);

		FileInfoDssDTO[] filelist = dataset.listFiles("original", false);

		//System.out.println("getPathInListing: " +  filelist[0].getPathInListing() + "; getPathInDataSet: " + filelist[0].getPathInDataSet());
		//System.out.println("getExternalDataSetCode: " + dataset.getExternalDataSetCode()  + " ; getExternalDataSetLink: " +  dataset.getExternalDataSetLink() + " ; tryGetInternalPathInDataStore: " + dataset.getDataSetDss().tryGetInternalPathInDataStore());

		InputStream streamy = dataset.getFile(  filelist[0].getPathInDataSet());
		openbisClient.logout();
		
		
		// create a buffer to improve copy performance later.
		byte[] buffer = new byte[2048];
        
        // open the zip file stream
        ZipInputStream stream = new ZipInputStream(streamy);
        String outdir = manager.getThemeFolder();// + datasetCode;
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
	
	private void initLiferayScreenName(VaadinRequest request){
		
		Map userInfoMap = (Map)  request.getAttribute(PortletRequest.USER_INFO);
    	//explode city gots to figure out how to deal with it not being in the map
    	//String locationId = (String) userInfoMap.get("liferay.location.id");
    	//String companyId = (String) userInfoMap.get("liferay.company.id");
    	String userName = (String) userInfoMap.get("liferay.user.id");
    	try {
			this.liferayScreenName = UserServiceUtil.getUserById(Long.parseLong(userName)).getScreenName();
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
				
	}
	
	private void setTable2(){
		VisualConfigurationManager manager = VisualConfigurationManager.getInstance();
		table = new Table();
		//Allow selection
		table.setSelectable(true);
		// Trigger selection change events immediately
		//Unfortunately in version 7.1.7 one still has to set this true, otherwise selection can be pretty buggy
		table.setImmediate(true);
		table.addContainerProperty("Name",String.class,"");
		table.addContainerProperty("Experiment", String.class, "");	
		table.addContainerProperty("Size", String.class,"");
		table.addContainerProperty("Registration date", String.class, "");
		table.addContainerProperty("Data Set Type", String.class, "");
		table.addContainerProperty("CODE", String.class, "");
		OpenBisClient openbisClient = new OpenBisClient(manager.getDataSourceUser(),manager.getDataSourcePassword(), manager.getDataSourceURL(), false);
		IOpenbisServiceFacade facade = openbisClient.getFacade();
		
		ServiceFinder serviceFinder = new ServiceFinder("openbis", IGeneralInformationService.SERVICE_URL);
		IGeneralInformationService openbisInfoService = 
		    serviceFinder.createService(IGeneralInformationService.class, manager.getDataSourceURL());
		String sessionToken = openbisInfoService.tryToAuthenticateForAllServices(manager.getDataSourceUser(), manager.getDataSourcePassword());
		SearchCriteria sc = new SearchCriteria();
		sc.addMatchClause(MatchClause.createAttributeMatch(MatchClauseAttribute.TYPE, "ZIP"));
		List<ch.systemsx.cisd.openbis.generic.shared.api.v1.dto.DataSet> allDataSets = openbisInfoService.searchForDataSetsOnBehalfOfUser(sessionToken, sc, this.liferayScreenName);
		int i = 0;
		for (ch.systemsx.cisd.openbis.generic.shared.api.v1.dto.DataSet ds : allDataSets){
			String code  = ds.getCode();
			String exp_id = ds.getExperimentIdentifier();
			Date date = ds.getRegistrationDate();
			String ds_type = ds.getDataSetTypeCode();
			DataSet realDS = facade.getDataSet(code);
			FileInfoDssDTO[] filelist = realDS.listFiles("original", false);
			String dataSetSize = DashboardUtil.humanReadableByteCount(filelist[0].getFileSize(),false);
			String datasetName = filelist[0].getPathInListing();
			if(ds_type.toLowerCase().equals("zip")){
				table.addItem(new Object[] {
						datasetName,exp_id,dataSetSize, date.toString(),ds_type, code },new Integer(i));	
			}

			++i;
		}
		facade.logout();
		openbisInfoService.logout(sessionToken);
	}
	
	private void setTable(){
		VisualConfigurationManager manager = VisualConfigurationManager.getInstance();
		table = new Table();
		table.addContainerProperty("Name",String.class,"");
		table.addContainerProperty("Experiment", String.class, "");	
		table.addContainerProperty("Size", String.class,"");
		table.addContainerProperty("Registration date", String.class, "");
		table.addContainerProperty("Data Set Type", String.class, "");
		table.addContainerProperty("CODE", String.class, "");
		
		//Allow selection
		table.setSelectable(true);
		// Trigger selection change events immediately
		//Unfortunately in version 7.1.7 one still has to set this true, otherwise selection can be pretty buggy
		table.setImmediate(true);
		OpenBisClient openbisClient = new OpenBisClient(manager.getDataSourceUser(),manager.getDataSourcePassword(), manager.getDataSourceURL(), false);
		IOpenbisServiceFacade facade = openbisClient.getFacade();
		//IOpenbisServiceFacade facade = OpenbisServiceFacadeFactory.tryCreate(manager.getDataSourceUser(), manager.getDataSourcePassword(), "https://qbis.informatik.uni-tuebingen.de:8443/openbis", 60000);
		IProteomicsDataApiFacade facade2 = FacadeFactory.create(manager.getDataSourceURL(), manager.getDataSourceUser(),  manager.getDataSourcePassword() );
		List<Project> projects = facade2.listProjects(this.liferayScreenName);
		facade2.logout();
		int i = 0;

		for(Project project: projects){
			List<String> temp = new ArrayList<String>();
			temp.add(project.getIdentifier());
			List<Experiment> experiments = facade.listExperimentsForProjects(temp);
			for(Experiment experiment: experiments){
				ArrayList<String> eee = new ArrayList<String>();
				eee.add(experiment.getIdentifier());

				List<DataSet> datasets = facade.listDataSetsForExperiments(eee);
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
								datasetName,exp_id,dataSetSize, date.toString(),ds_type, code },new Integer(i));	
					}

					++i;
				}
			}
		}
		facade.logout();
	}
	
	@Override
	public void detach(){
		for(File file: this.folderToDelete){
			boolean fileDeleted = this.deleteFile(file);
			if(!fileDeleted){
				System.err.println(file.getAbsolutePath() + " could not be deleted");
			}
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
	public void setBrowserWidth(float newBrowserFrameW, float newTableW) {
		this.w = (int) newBrowserFrameW;
		for(BrowserFrame frame: this.frames.values()){
			frame.setWidth(this.intPixelToStringPixel((int)newBrowserFrameW));
		}
		this.table.setWidth(this.intPixelToStringPixel((int)newTableW));
	
		
		System.out.println("splith,splitw: "+ hsplit.getHeight() + ","+ hsplit.getWidth());
		System.out.println("h,w: "+ h + ","+ w);
	}
	
	
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
		float newPanelW = (float) (0.95*w);
		float newPanelH = (float) (0.95*h);
		layout.setWidth(this.intPixelToStringPixel((int)newPanelW));
		layout.setHeight(this.intPixelToStringPixel((int)newPanelH));
		float newHsplitW = newPanelW;//(float) (0.95*(float)newPanelW);
		float newHsplitH = (float) (0.95*newPanelH);
		this.hsplit.setWidth(this.intPixelToStringPixel((int)newHsplitW));
		this.hsplit.setHeight(this.intPixelToStringPixel((int)newHsplitH));
		
		
		float newFrameH = (float) (0.95*newHsplitH);
		float current = hsplit.getSplitPosition() - hsplit.getMinSplitPosition();
		float max = hsplit.getMaxSplitPosition() - hsplit.getMinSplitPosition();
		float percent = current/this.hsplit.getWidth();
		System.out.println("setSize: current,width,percent" + current + "," + this.hsplit.getWidth() + "," + percent);
		percent = 1f - percent;
		float newFrameW = Math.max(newHsplitW*percent,1);
		this.h = (int) newFrameH;
		this.w = (int) newFrameW;
		System.out.println("setSize: current,max,percent" + current + "," + max + "," + percent);
		System.out.println("splith,splitw: "+ newHsplitH + ","+ newHsplitW);
		System.out.println("h,w: "+ this.h + ","+ this.w);
		for(BrowserFrame frame: this.frames.values()){
			frame.setWidth(this.intPixelToStringPixel((int)newFrameW));
			frame.setHeight(this.intPixelToStringPixel((int)newFrameH));
		}
	}

}

