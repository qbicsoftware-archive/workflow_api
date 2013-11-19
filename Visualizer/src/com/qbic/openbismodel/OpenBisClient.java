package com.qbic.openbismodel;

import java.util.ArrayList;
import java.util.List;

import ch.systemsx.cisd.common.exceptions.InvalidSessionException;
import ch.systemsx.cisd.common.exceptions.InvalidAuthenticationException;
import ch.systemsx.cisd.openbis.dss.client.api.v1.IOpenbisServiceFacade;
import ch.systemsx.cisd.openbis.dss.client.api.v1.OpenbisServiceFacadeFactory;
import ch.systemsx.cisd.openbis.generic.shared.api.v1.dto.Experiment;
import ch.systemsx.cisd.openbis.generic.shared.api.v1.dto.Project;


/*public class OpenBisClient {
	//TODO Es soll sich selbst um initialization mit Openbis kümmern
	//einmal anmelden und in der session bleiben
	//bis logout eingeleitet wird
	//Donnerstag 15.30 - 16.20
	IOpenbisServiceFacade facade;
	/**
	 * TODO
	 * @return
	 *
	public List<Project> listProjects() {
		IOpenbisServiceFacade facade = OpenbisServiceFacadeFactory.tryCreate("wojnar", "opk49x3z", "https://qbis.informatik.uni-tuebingen.de:8443/openbis", 60000); 
		List<Project> projects = facade.listProjects();
		facade.logout();
		return projects;
	}
	/**
	 * todo
	 * @param project
	 * @return
	 *
	public List<Experiment> listExperimentsForProject(
			Project project) {
		IOpenbisServiceFacade facade = OpenbisServiceFacadeFactory.tryCreate("wojnar", "opk49x3z", "https://qbis.informatik.uni-tuebingen.de:8443/openbis", 60000); 
		List<String> temp = new ArrayList<String>();
		temp.add(project.getIdentifier());
		List<Experiment> experiments = facade.listExperimentsForProjects(temp);
		facade.logout();
		return experiments;	
	}
	private boolean login(){
		return false;
		
	}
	public boolean logout(){
		return true;
	}
	
//	public boolean loggedin(){
	//	return this.facade;
//	}
}*/

/**
 * dss client, a proxy to the generic openbis api
 * This client is based on DSS Client for openBIS written by Emanuel Schmid and the OpenbisConnector written by Bela Hullar
 * @author wojnar
 *
 */
public class OpenBisClient{
	int timeout = 120; // 2 minutes
	int tolimit = 600;
	IOpenbisServiceFacade facade;
	String userId;
	String password;
	String serverURL;
	boolean verbose;
	
	
	public OpenBisClient(String loginid, String password, String serverURL, boolean verbose)
	{
		this.userId = loginid;
		this.password = password;
		this.serverURL = serverURL;
		this.verbose = verbose;
		this.facade = null;
		this.login();
	}
	public boolean loggedin(){
		if (this.facade == null)
			return false;
		try{
			this.facade.checkSession();
		}
		catch(InvalidSessionException e){
			return false;
		}
		return true;
	}
	/**
	 * logs out of the OpenBIS server
	 */
	public void logout(){
		try{
			this.facade.logout();
		}
		catch (Exception e){
			//Nothing todo here
		} finally{
			this.facade = null;
		}
	}
	/**
	 *  logs in to the OpenBIS server with the system userid
        after calling this function, the user has to provide the password
	 */
	public void login(){
		if(this.loggedin())
			this.logout();
		
		int trialno = 3;
		int notrial = 0;
		int timeoutStep = this.timeout;
		while(true){
			if(this.verbose){
				System.out.println("trialno: " + trialno + ", notrial: " + notrial + ", timeout " +  this.timeout);
			}
			try{
				facade = OpenbisServiceFacadeFactory.tryCreate(this.userId, this.password, this.serverURL, this.timeout*1000);
				break;
			}
			catch (Exception e){
				if(e.getMessage().contains("Read timed out")){
					if (this.timeout >= this.tolimit)
						throw new InvalidAuthenticationException("login failed");
					this.timeout += timeoutStep;
					
				}
				else{
					notrial++;
					if(notrial >= trialno) 
						throw new InvalidAuthenticationException("login failed");
					try {
						Thread.sleep(10);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					if(facade == null){
						return;//throw new UnexpectedException("OpenBis facade is not available");
					}
				}
			}
		}
	}
	
	/**
	 * TODO
	 * @return
	 */
	public List<Project> listProjects() {
		if(!this.loggedin()){
			this.login();
		}
		List<Project> projects = facade.listProjects();
		return projects;
	}
	
	/**
	 * todo
	 * @param project
	 * @return
	 */
	public List<Experiment> listExperimentsForProject(
			Project project) {
		if(!this.loggedin()){
			this.login();
		}
		List<String> temp = new ArrayList<String>();
		temp.add(project.getIdentifier());
		List<Experiment> experiments = facade.listExperimentsForProjects(temp);
		return experiments;	
	}
	
	public IOpenbisServiceFacade getFacade(){
		if(!this.loggedin()){
			this.login();
		}
		return facade;
	}
	
	@Override
	protected void finalize() throws Throwable {
		this.logout();
		super.finalize();
	}
	
	
}
