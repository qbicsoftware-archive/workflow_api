package de.uni_tuebingen.qbic.beans;

import java.io.Serializable;

public class WorkflowDescriptionBean implements Serializable {
  private String name;
  private String description;
  private String version;


  public WorkflowDescriptionBean(String name, String description, String version) {
    super();
    this.name = name;
    this.description = description;
    this.version = version;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }
}
