package com.qbic.openbismodel;

import java.util.Date;
import java.util.List;

public class OpenBisProject {
	private List<OpenBisExperiment> experiments;
	private String QBiCUser;
	private String Id;
	private String Description;
	private String QBiCScientist;
	private Date RegistrationDate;
	private boolean project_finished;
	
	
	public List<OpenBisExperiment> getExperiments() {
		return experiments;
	}

	public void setExperiments(List<OpenBisExperiment> experiments) {
		this.experiments = experiments;
	}

	public String getQBiCUser() {
		return QBiCUser;
	}

	public void setQBiCUser(String qBiCUser) {
		QBiCUser = qBiCUser;
	}

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}

	public String getQBiCScientist() {
		return QBiCScientist;
	}

	public void setQBiCScientist(String qBiCScientist) {
		QBiCScientist = qBiCScientist;
	}

	public Date getRegistrationDate() {
		return RegistrationDate;
	}

	public void setRegistrationDate(Date registrationDate) {
		RegistrationDate = registrationDate;
	}

	public boolean isProject_finished() {
		return project_finished;
	}

	public void setProject_finished(boolean project_finished) {
		this.project_finished = project_finished;
	}
}
