package de.uni_tuebingen.qbic.util;

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
    public static final String THEME_FODLER = "themeFolder";
    
    private static Logger log = new Logger(VisualConfigurationManager.class);
    private static VisualConfigurationManager INSTANCE = new VisualConfigurationManager();


    private String configurationFileName;
    private String dataSource;
    private String dataSourceUser;
    private String dataSourcePass;
    private String dataSourceURL;
    private String themeFolder;
    
    
    private VisualConfigurationManager()  {
	Properties portletConfig = PortletProps.getProperties();
	//FacesContext context = FacesContext.getCurrentInstance();
	//String serverName = context.getExternalContext().getRequestServerName();
	Properties config = new Properties();
	//configurationFileName = portletConfig.getProperty(serverName + CONFIGURATION_SUFFIX);
	//if(configurationFileName == null) {
	    configurationFileName = portletConfig.getProperty("default" + CONFIGURATION_SUFFIX);
	    themeFolder = portletConfig.getProperty(THEME_FODLER + CONFIGURATION_SUFFIX);
	 //   log.warn("No configfile path found for host " + " @TODO "+", using default "+configurationFileName);
	//}
	try {
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
    
    public String getThemeFolder(){
    	return themeFolder;
    }
}

