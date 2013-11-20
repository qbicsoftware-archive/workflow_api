package com.qbic.openbismodel;

import java.util.List;
//import com.qbic.util.DashboardUtil;


public class OpenBisModel {
	List<OpenBisProject> openbisProjects;
	OpenBisClient openbisClient;
	
	public OpenBisModel(){
		//openbisClient = new OpenBisClient("wojnar", "opk49x3z", "https://qbis.informatik.uni-tuebingen.de:8443/openbis", false);
		openbisProjects = null;
	}
	
	/**
	 * TODO
	 * @return
	 */
/*	public List<OpenBisProject> getProjects(){
		if(openbisProjects == null){
			openbisProjects = new ArrayList<OpenBisProject>();
			List<Project> projects =  openbisClient.listProjects(); 
			for(Project project: projects){
				OpenBisProject op = new OpenBisProject();
				op.setDescription("Description currently not available");
				op.setId(project.getCode());
				op.setRegistrationDate(project.getRegistrationDetails().getRegistrationDate());
				op.setProject_finished(false);
				op.setQBiCUser("QBiC User/client currently not available");
				op.setQBiCScientist(project.getRegistrationDetails().getUserId());
				List<Experiment> experiments = openbisClient.listExperimentsForProject(project);
				ArrayList<OpenBisExperiment> openBisExperiments = new ArrayList<OpenBisExperiment>();
				for(Experiment experiment: experiments){
					Map<String,String> prop = experiment.getProperties();
					System.out.println("getCode " + experiment.getCode());
					System.out.println("getExperimentTypeCode " + experiment.getExperimentTypeCode());
					System.out.println("getIdentifier " + experiment.getIdentifier());
					System.out.println("toString " + experiment.toString());
					System.out.println("getProperties: ");
					for (Map.Entry<String, String> entry : prop.entrySet())
					{
					    System.out.println(entry.getKey() + "/" + entry.getValue());
					}
					System.out.println("getPermId " + experiment.getPermId());
					EntityRegistrationDetails erd = experiment.getRegistrationDetails();
					System.out.println("erd.getModifierFirstName() " + erd.getModifierFirstName());
					System.out.println("erd.getModifierLastName() " + erd.getModifierLastName());
					System.out.println("erd.getModifierUserId() " + erd.getModifierUserId());
					System.out.println("erd.getUserLastName() " + erd.getUserLastName());
					System.out.println("erd.toString() " + erd.toString());
					System.out.println("erd.getUserId() " + erd.getUserId());
					System.out.println("erd.getRegistrationDate().toString() " + erd.getRegistrationDate().toString());
					System.out.println("/*****************************************");
					OpenBisExperiment tmp = new OpenBisExperiment();
					IOpenbisServiceFacade facade = this.openbisClient.getFacade();
					List<DataSet> datasets = facade.listDataSetsForExperiment(experiment.getIdentifier());
					//ArrayList<String>  = new ArrayList<String>();
					//System.out.println(facade.listDataSetsForExperiment(experiment.getIdentifier()).size());
					//System.out.println(facade.listDataSetsForExperiment(experiment.getCode()).size());
					ArrayList<String> eee = new ArrayList<String>();
					eee.add(experiment.getIdentifier());
					//System.out.println(facade.listSamplesForExperiments(eee).size());
					System.out.println(facade.listDataSetsForExperiments(eee).size());
					datasets = facade.listDataSetsForExperiments(eee);
				/*	for(Sample sample : facade.listSamplesForExperiments(eee)){
						ArrayList<String> sss = new ArrayList<String>();
						sss.add(sample.getIdentifier());
						System.out.println(facade.listDataSetsForSamples(sss).size());
						System.out.println(facade.listDataSetsForSample(sample.getIdentifier()).size());
					}*
					
					
					long filesize = 0;
					for(DataSet dataset : datasets){
						IDataSetDss datasetDSS = dataset.getDataSetDss();
						FileInfoDssDTO[] fileinfo = datasetDSS.listFiles("", true);
						
						for(FileInfoDssDTO file : fileinfo){
							if(!file.isDirectory())
								filesize += file.getFileSize();
						}
					}
					String sizeOfFile = String.valueOf(filesize);//DashboardUtil.humanReadableByteCount(filesize, false);
					tmp.setCurrent_volume(sizeOfFile);
					tmp.setData_analysed(false);
					tmp.setData_generated(false);
					tmp.setDescription("Description currently not available");
					tmp.setEstimated_volume(-42);
					tmp.setExp_designed(false);
					tmp.setId(experiment.getCode());
					tmp.setInvoice_sent(false);
					tmp.setOffer_accepted(false);
					openBisExperiments.add(tmp);
				}
				op.setExperiments(openBisExperiments);
				openbisProjects.add(op);
			}
		}
		return openbisProjects;
	}
/*
	/**
	 * TODO not implemented
	 * @return
	 */
	public List<OpenBisExperiment> listExperiments(){
		throw new UnsupportedOperationException("listExperiments in OpenBisModel not implemented");
	}
	/**
	 * TODO not implemented
	 * @return
	 */
	public List<OpenBisExperiment> listExperimentsFormProject(OpenBisProject project){
		throw new UnsupportedOperationException("listExperimentsFormProject in OpenBisModel not implemented");
	}
	
}
