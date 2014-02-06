package de.uni_tuebingen.qbic.openbismodel;

public class OpenBisConnection{
        
    	private final String openBISPassword;
	private final String openBISUserName;
	private final String openBISUrl;
	
	public OpenBisConnection(String openBISUserName, String openBISPassword, String openBISUrl) {
	    this.openBISUserName = openBISUserName;
	    this.openBISPassword = openBISPassword;
	    this.openBISUrl = openBISUrl;
	}
	
	public String getPassword(){
		return openBISPassword;
	}
	public String getUserName(){
		return openBISUserName;
	}
	public String getUrl(){
		return openBISUrl;
	}
}