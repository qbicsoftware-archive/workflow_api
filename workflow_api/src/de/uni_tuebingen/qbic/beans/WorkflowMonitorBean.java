package de.uni_tuebingen.qbic.beans;

import java.io.Serializable;


public class WorkflowMonitorBean implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 8968460328493605874L;
  private String workflow;
  private String status;
  private double progress;
  private String version;
  private String executedBy;
  private String startedAt;
  private String experiment;

  public WorkflowMonitorBean(String workflow, String status, double progress, String version,
      String executedBy, String startedAt, String experimentCode) {
    super();
    this.workflow = workflow;
    this.status = status;
    this.progress = progress;
    this.setVersion(version);
    this.setExecutedBy(executedBy);
    this.setStartedAt(startedAt);
    this.experiment = experimentCode;
  }

  public String getWorkflow() {
    return workflow;
  }

  public void setWorkflow(String workflow) {
    this.workflow = workflow;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public double getProgress() {
    return progress;
  }

  public void setProgress(double progress) {
    this.progress = progress;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getExecutedBy() {
    return executedBy;
  }

  public void setExecutedBy(String executedBy) {
    this.executedBy = executedBy;
  }

  public String getStartedAt() {
    return startedAt;
  }

  public void setStartedAt(String startedAt) {
    this.startedAt = startedAt;
  }

  public String getExperiment() {
    return experiment;
  }

  public void setExperiment(String experiment) {
    this.experiment = experiment;
  }

}
