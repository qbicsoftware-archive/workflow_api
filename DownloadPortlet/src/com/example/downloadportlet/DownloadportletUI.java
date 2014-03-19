package com.example.downloadportlet;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.portlet.PortletRequest;
import javax.servlet.annotation.WebServlet;

import ch.systemsx.cisd.openbis.dss.client.api.v1.DataSet;
import ch.systemsx.cisd.openbis.dss.client.api.v1.IDataSetDss;
import ch.systemsx.cisd.openbis.dss.client.api.v1.IOpenbisServiceFacade;
import ch.systemsx.cisd.openbis.dss.generic.shared.api.v1.FileInfoDssDTO;
import ch.systemsx.cisd.openbis.generic.shared.api.v1.dto.Experiment;
import ch.systemsx.cisd.openbis.generic.shared.api.v1.dto.Project;
import ch.systemsx.cisd.openbis.plugin.proteomics.client.api.v1.FacadeFactory;
import ch.systemsx.cisd.openbis.plugin.proteomics.client.api.v1.IProteomicsDataApiFacade;






import com.qbic.openbismodel.OpenBisClient;
import com.qbic.util.ConfigurationManager;
import com.qbic.util.DashboardUtil;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserServiceUtil;
import com.liferay.portal.util.PortalUtil;


@SuppressWarnings("serial")
@Theme("liferay")
public class DownloadportletUI extends UI {

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = DownloadportletUI.class)
	public static class Servlet extends VaadinServlet {
	}

	@Override
	protected void init(VaadinRequest request) {
		final VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		//setContent(layout);
		
		
		//ConfigurationManager manager = ConfigurationManager.getInstance();
		Map userInfoMap = (Map) request.getAttribute(PortletRequest.USER_INFO);
    	//explode city gots to figure out how to deal with it not being in the map
    	Set<String> keySet = userInfoMap.keySet();
    	Iterator<String> itr = keySet.iterator();
    	while(itr.hasNext()) {
            Object element = itr.next();
            System.out.print(element + " ");
         }
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
    	
    	//System.out.println(locationId + " " + companyId + " " + userName + " " + screenName + " " + manager.getDataSource() + " " + manager.getDataSourcePassword() + " " + manager.getDataSourceURL() + " " + manager.getDataSourceUser());
    	
		final Table table = new Table();
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
		
		//OpenBisClient openbisClient = new OpenBisClient(manager.getDataSourceUser(), manager.getDataSourcePassword(), manager.getDataSourceURL(), false);
		OpenBisClient openbisClient = new OpenBisClient("QBIC", "qbicuseracc", "https://qbis.informatik.uni-tuebingen.de:8443", false);
		IOpenbisServiceFacade facade = openbisClient.getFacade();
		//IProteomicsDataApiFacade facade2 = FacadeFactory.create(manager.getDataSourceURL(), manager.getDataSourceUser(),  manager.getDataSourcePassword() );
		IProteomicsDataApiFacade facade2 = FacadeFactory.create("https://qbis.informatik.uni-tuebingen.de:8443", "QBIC",  "qbicuseracc" );
		List<Project> projects = facade2.listProjects(screenName);
		facade2.logout();
		int i = 0;
		int j = 0;
		int k = 0;
		for(Project project: projects){
			List<Experiment> experiments = openbisClient.listExperimentsForProject(project);
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
					
					table.addItem(new Object[] {
						    code,ds_type,exp_id, date.toString(),dataSetSize, datasetName},new Integer(i));
					++i;
				}
			}
		}
		facade.logout();
		System.out.println(k);
		System.out.println(j);
		System.out.println(i);
		final Button downloadButton = new Button("Download old way");
		

		String datasetCode = (String)table.getItem(0).getItemProperty("CODE").getValue();
		String datasetType = (String)table.getItem(0).getItemProperty("Name").getValue();
		StreamResource firstResource = createOpenBisResource(datasetCode,datasetType);
		final FileDownloader fileDownloader = new FileDownloader(firstResource);
		
		
		//Handle selection change.
		table.addValueChangeListener(new Property.ValueChangeListener() {
		    @Override
			public void valueChange(ValueChangeEvent event) {
				System.out.println(event.getProperty().getValue());
		    	String datasetCode = (String)table.getItem(event.getProperty().getValue()).getItemProperty("CODE").getValue();
				String datasetType = (String)table.getItem(event.getProperty().getValue()).getItemProperty("Name").getValue();
				
		        StreamResource myResource = createOpenBisResource(datasetCode,datasetType);
		        myResource.setBufferSize(2048);
		        myResource.setCacheTime(200);
		        fileDownloader.setFileDownloadResource(myResource);
		        //Resource resource = createOpenBisResource(datasetCode,datasetType);
		        fileDownloader.extend(downloadButton);
		       // layout.addComponent(downloadButton);
		       // layout.addComponent(table);
		       // setContent(layout); 
		    }
			private StreamResource createOpenBisResource(final String datasetCode,final String datasetType){
				return new StreamResource(new StreamResource.StreamSource(){
					ConfigurationManager manager = ConfigurationManager.getInstance();
					@Override
					public InputStream getStream(){
						ConfigurationManager manager = ConfigurationManager.getInstance();
						//OpenBisClient openbisClient = new OpenBisClient(manager.getDataSourceUser(),manager.getDataSourcePassword(),  manager.getDataSourceURL(), false);
						OpenBisClient openbisClient = new OpenBisClient("QBIC", "qbicuseracc", "https://qbis.informatik.uni-tuebingen.de:8443", false);
						IOpenbisServiceFacade facade = openbisClient.getFacade();
						DataSet dataset = facade.getDataSet(datasetCode);

						FileInfoDssDTO[] filelist = dataset.listFiles("original", false);

						System.out.println("getPathInListing: " +  filelist[0].getPathInListing() + "; getPathInDataSet: " + filelist[0].getPathInDataSet());
						System.out.println("getExternalDataSetCode: " + dataset.getExternalDataSetCode()  + " ; getExternalDataSetLink: " +  dataset.getExternalDataSetLink() + " ; tryGetInternalPathInDataStore: " + dataset.getDataSetDss().tryGetInternalPathInDataStore());

						InputStream streamy = dataset.getFile(/*"/" + dataset.getDataSetDss().tryGetInternalPathInDataStore() + "/" +*/  filelist[0].getPathInDataSet());
						//DownloadStream tmp = new DownloadStream(streamy,"attachment",datasetType);
						System.out.println(streamy.toString());
						///System.out.println(tmp.getStream().toString());
						openbisClient.logout();
						facade.logout();
						return 	 streamy;//dataset.getFile(/*"/" + dataset.getDataSetDss().tryGetInternalPathInDataStore() + "/" +*/  filelist[0].getPathInDataSet());

					}
				},datasetType);
			}
		});
		/*
		String datasetCode = (String)table.getItem(table.getValue()).getItemProperty("CODE").getValue();
		
        StreamResource myResource = createOpenBisResource(datasetCode);
        FileDownloader fileDownloader = new FileDownloader(myResource);
        fileDownloader.extend(downloadButton);*/
        layout.addComponent(downloadButton);
        layout.addComponent(table);
        final Button butt = new Button("Download through URL");
        
        butt.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				String qcMLFile  = "https://qbis.informatik.uni-tuebingen.de:8444/datastore_server/20130828152157222-557/original/QBWAT001TA_GCCAAT_L001_R2_001.fastq.gz?mode=simpleHtml&sessionID=wojnar-140318161313908xBA60CAB58DE6CDBFDA33147F1B87ED2B";//"https://qbis.informatik.uni-tuebingen.de:8444/datastore_server/20140123135231839-1211/original/QMTST001TT_4.mzML.QCCalculator.qcML.QCEmbedder.qcML.QCShrinker.qcML";
				String better = "https://qbis.informatik.uni-tuebingen.de:8444/datastore_server/20140123135231839-1211/original/QMTST001TT_4.mzML.QCCalculator.qcML.QCEmbedder.qcML.QCShrinker.qcML?mode=simpleHtml&sessionID=wojnar-140318154200715x23379DF20426F94C5DE9E03142371B00";
				ExternalResource resource = new ExternalResource(qcMLFile);//("https://qbis.informatik.uni-tuebingen.de:8443" + attachments.get(0).getDownloadLink());
				//Page.getCurrent().open(resource.getURL(), "Download", true);
				fileDownloader.setFileDownloadResource(resource);
		        //Resource resource = createOpenBisResource(datasetCode,datasetType);
		        fileDownloader.extend(downloadButton);
			}
		});
        
        layout.addComponent(butt);
        setContent(layout);
	}
	
	private StreamResource createOpenBisResource(final String datasetCode, final String Name){
		return new StreamResource(new StreamResource.StreamSource(){
			@Override
			public InputStream getStream(){
				ConfigurationManager manager = ConfigurationManager.getInstance();
				OpenBisClient openbisClient = new OpenBisClient(manager.getDataSourceUser(),manager.getDataSourcePassword(),  manager.getDataSourceURL(), false);
				IOpenbisServiceFacade facade = openbisClient.getFacade();
				DataSet dataset = facade.getDataSet(datasetCode);
				IDataSetDss datasetDSS = dataset.getDataSetDss();
				java.io.File file = dataset.tryLinkToContents(null);
				FileInfoDssDTO[] filelist = dataset.listFiles("original", false);
				//InputStream ip = dataset.getFile("original");
				//if(ip != null){
				//	System.out.println(ip.toString());
				//}
				System.out.println("getPathInListing: " +  filelist[0].getPathInListing() + "; getPathInDataSet: " + filelist[0].getPathInDataSet());
				System.out.println("getExternalDataSetCode: " + dataset.getExternalDataSetCode()  + " ; getExternalDataSetLink: " +  dataset.getExternalDataSetLink() + " ; tryGetInternalPathInDataStore: " + dataset.getDataSetDss().tryGetInternalPathInDataStore());
				if(file != null){
					System.out.println("absolute path of 20130828152157222-557 is " + file.getAbsolutePath());
					try {
						return new FileInputStream(file);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
						return null;
					}
				}
				else {
					System.out.println("file is null");
				}
				InputStream streamy = dataset.getFile(/*"/" + dataset.getDataSetDss().tryGetInternalPathInDataStore() + "/" +*/  filelist[0].getPathInDataSet());
				System.out.println(streamy.toString());
				facade.logout();
				return 	 streamy;//dataset.getFile(/*"/" + dataset.getDataSetDss().tryGetInternalPathInDataStore() + "/" +*/  filelist[0].getPathInDataSet());

			}
		},Name);
	}
	
	private StreamResource createFileResource(){
		return new StreamResource(new StreamResource.StreamSource() {
			
			@Override
			public InputStream getStream() {
				String fileName = "C:/Users/wojnar/Documents/ALL_GS1000692_fastqc/fastqc_data.txt";
				try{
					return new FileInputStream(fileName);
				}catch (FileNotFoundException e){
					e.printStackTrace();
					return null;
				}
 			}
		},"statistics.txt");
	}
	
	 private StreamResource createResource() {
	        return new StreamResource(new StreamResource.StreamSource() {
	            @Override
	            public InputStream getStream() {
	                String text = "My image";

	                BufferedImage bi = new BufferedImage(100, 30, BufferedImage.TYPE_3BYTE_BGR);
	                bi.getGraphics().drawChars(text.toCharArray(), 0, text.length(), 10, 20);

	                try {
	                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
	                    ImageIO.write(bi, "png", bos);
	                    return new ByteArrayInputStream(bos.toByteArray());
	                } catch (IOException e) {
	                    e.printStackTrace();
	                    return null;
	                }

	            }
	        }, "traffic_light_green.PNG");
	    }
	 

	
	

}