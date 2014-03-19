package com.qbic.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;

import com.liferay.util.portlet.PortletProps; //util-java.jar

public class ConfigurationManager {

    public static final String MASTER_DIR_KEY = "masterproperties.dir";
    public static final String USER_DEFAULTS_DIR_KEY = "userdefaults.dir";
    public static final String WORKFLOW_DIR_KEY = "workflows.dir";
    public static final String BIODB_UPDATEFILE_KEY = "biodb.updatefile";
    public static final String BIODB_UPDATEFLAG_KEY = "biodb.updateflag";
    public static final String BIODB_BRUTUS_DIR_KEY = "biodb.dir.brutus";
    public static final String BIODB_DB_FILE_EXT_KEY = "biodb.db.ext";
    public static final String BIODB_UPDATEFILE_ENDING = "biodb.updatefileending";
    public static final String CONFIGURATION_SUFFIX = ".configuration";
    public static final String DATASOURCE_KEY = "datasource";
    public static final String DATASOURCE_USER = "datasource.user"; 
    public static final String DATASOURCE_PASS = "datasource.password"; 
    public static final String DATASOURCE_URL = "datasource.url"; 
    public static final String WIKI_ADDR = "wiki.url";
    public static final String WIKI_USER = "wiki.user";
    public static final String WIKI_PASSWD = "wiki.passwd";

    private static Logger log = new Logger(ConfigurationManager.class);
    private static ConfigurationManager INSTANCE = new ConfigurationManager();


    private String mastersDir;
    private String userDefaultsDir;
    private String workflowDir;
    private String bioDBUpdateFilePath;
    private String bioDBUpdateFlagPath;
    private String bioDBUpdateFileEnding;
    private String bioDBBrutusPath;
    private String bioDBDBFileExtension;
    private String configurationFileName;
    private String dataSource;
    private String dataSourceUser;
    private String dataSourcePass;
    private String dataSourceURL;
    private String wikiUser;
    private String wikiPass;
    private String wikiURL;

    private ConfigurationManager()  {
	Properties portletConfig = PortletProps.getProperties();
	//FacesContext context = FacesContext.getCurrentInstance();
	//String serverName = context.getExternalContext().getRequestServerName();
	Properties config = new Properties();
	//configurationFileName = portletConfig.getProperty(serverName + CONFIGURATION_SUFFIX);
	if(configurationFileName == null) {
	    configurationFileName = portletConfig.getProperty("default" + CONFIGURATION_SUFFIX);
	    log.warn("No configfile path found for host " + " @TODO "+", using default "+configurationFileName);
	}
	try {
	    config.load(new FileReader(configurationFileName));
	    StringWriter configDebug = new StringWriter();
	    config.list( new PrintWriter(configDebug));
	    log.debug("Loading configuration: from " + configurationFileName + " [" + configDebug.toString() + "]");
	    mastersDir = config.getProperty(MASTER_DIR_KEY);
	    userDefaultsDir = config.getProperty(USER_DEFAULTS_DIR_KEY);
	    workflowDir = config.getProperty(WORKFLOW_DIR_KEY);
	    bioDBUpdateFilePath =config.getProperty(BIODB_UPDATEFILE_KEY);
	    bioDBUpdateFlagPath = config.getProperty(BIODB_UPDATEFLAG_KEY);
	    bioDBUpdateFileEnding = config.getProperty(BIODB_UPDATEFILE_ENDING);
	    bioDBBrutusPath = config.getProperty(BIODB_BRUTUS_DIR_KEY);
	    bioDBDBFileExtension = config.getProperty(BIODB_DB_FILE_EXT_KEY);
	    dataSource = config.getProperty(DATASOURCE_KEY,"openBIS");
	    dataSourceUser = config.getProperty(DATASOURCE_USER);
	    dataSourcePass = config.getProperty(DATASOURCE_PASS);
	    dataSourceURL = config.getProperty(DATASOURCE_URL);
	    wikiUser = config.getProperty(WIKI_USER);
	    wikiPass = config.getProperty(WIKI_PASSWD);
	    wikiURL = config.getProperty(WIKI_ADDR);
	} catch (IOException e) {
	    log.error("Failed to load configuration: ", e);
	}
    }

    public static ConfigurationManager getInstance() {
	return INSTANCE;
    }

    public String getMastersDir() {
	return (new File(configurationFileName).getParent())+"/"+mastersDir;
    }

    public String getUserDefaultsDir() {
	return (new File(configurationFileName).getParent())+"/"+userDefaultsDir;
    }

    public String getWorkflowDir() {
	return (new File(configurationFileName).getParent())+"/"+workflowDir;
    }

    public String getBioDBUpdateFilePath() {
	return (new File(configurationFileName).getParent())+"/"+bioDBUpdateFilePath;
    }

    public String getBioDBUpdateFlagPath() {
	return (new File(configurationFileName).getParent())+"/"+ bioDBUpdateFlagPath;
    }

    public String getBioDBUpdateFileEnding() {
	return bioDBUpdateFileEnding;
    }

    public String getBioDBBrutusPath() {
	return bioDBBrutusPath;
    }

    public String getBioDBDBFileExtension() {
	return bioDBDBFileExtension;
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

    public String getWikiUser() {
        return wikiUser;
    }

    public String getWikiPass() {
        return wikiPass;
    }

    public String getWikiURL() {
        return wikiURL;
    }

}
