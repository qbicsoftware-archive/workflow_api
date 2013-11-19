package com.qbic.util;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;

import com.liferay.util.portlet.PortletProps;

public class VisualConfigurationManager {

    public static final String CONFIGURATION_SUFFIX = ".configuration";
    public static final String DATASOURCE_KEY = "datasource";
    public static final String DATASOURCE_USER = "datasource.user"; 
    public static final String DATASOURCE_PASS = "datasource.password"; 
    public static final String DATASOURCE_URL = "datasource.url"; 
    public static final String DSSCLIENTCMD = "dss.client";
    public static final String UNZIPCMD = "unzip";
    public static final String CLEANUP = "clean.up";
    public static final String THEME = "theme";
    
    private static Logger log = new Logger(ConfigurationManager.class);
    private static VisualConfigurationManager INSTANCE = new VisualConfigurationManager();


    private String configurationFileName;
    private String dataSource;
    private String dataSourceUser;
    private String dataSourcePass;
    private String dataSourceURL;
    private String dssClientCmd;
    private String unzipCmd;
    private String cleanUpCmd;
    private String theme;
    
    
    private VisualConfigurationManager()  {
	Properties portletConfig = PortletProps.getProperties();
	//FacesContext context = FacesContext.getCurrentInstance();
	//String serverName = context.getExternalContext().getRequestServerName();
	Properties config = new Properties();
	//configurationFileName = portletConfig.getProperty(serverName + CONFIGURATION_SUFFIX);
	//if(configurationFileName == null) {
	    configurationFileName = portletConfig.getProperty("default" + CONFIGURATION_SUFFIX);
	 //   log.warn("No configfile path found for host " + " @TODO "+", using default "+configurationFileName);
	//}
	try {
		dssClientCmd = config.getProperty(DSSCLIENTCMD + CONFIGURATION_SUFFIX);
		unzipCmd = config.getProperty(UNZIPCMD + CONFIGURATION_SUFFIX);
		cleanUpCmd  = config.getProperty(CLEANUP + CONFIGURATION_SUFFIX);
	    theme = config.getProperty(THEME + CONFIGURATION_SUFFIX);
		config.load(new FileReader(configurationFileName));
	    StringWriter configDebug = new StringWriter();
	    config.list( new PrintWriter(configDebug));
	    log.debug("Loading configuration: from " + configurationFileName + " [" + configDebug.toString() + "]");

	    dataSource = config.getProperty(DATASOURCE_KEY,"openBIS");
	    dataSourceUser = config.getProperty(DATASOURCE_USER);
	    dataSourcePass = config.getProperty(DATASOURCE_PASS);
	    dataSourceURL = config.getProperty(DATASOURCE_URL);

	} catch (IOException e) {
	    log.error("Failed to load configuration: ", e);
	}
    }

    public static VisualConfigurationManager getInstance() {
	return INSTANCE;
    }

 
    public String getConfigurationFileName() {
	return configurationFileName;
    }

    public String getDataSource() {
	return dataSource;
    }

    public String getDataSourceUser() {
	return dataSourceUser;
    }

    public String getDataSourcePassword() {
	return dataSourcePass;
    }

    public String getDataSourceURL() {
    	return dataSourceURL;
    }
    
    public String getDSSClientCmd() {
	return dssClientCmd;
    }
    
    public String getUnzipCmd() {
	return unzipCmd;
    }
    
    public String getCleanUpCmd() {
	return cleanUpCmd;
    }
    
    public String getTheme(){
    	return theme;
    }
}

