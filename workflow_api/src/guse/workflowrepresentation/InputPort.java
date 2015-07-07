package guse.workflowrepresentation;

import java.util.Map;

import submitter.parameters.Parameter;



public class InputPort {


  public InputPort(String name, String description, Integer portNumber,
      Map<String, Parameter> params, Type type) {
    super();
    this.name = name;
    this.description = description;
    this.portNumber = portNumber;
    this.params = params;
    this.type = type;
  }

  public InputPort() {

  }

  public enum Type {
    FILESTOSTAGE, JOBNAME, CTD_ZIP, CTD, REGISTERNAME, USER, DROPBOX
  };

  String name;
  String description;

  Integer portNumber;

  Map<String, Parameter> params;
  Type type;

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
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

  public Integer getPortNumber() {
    return portNumber;
  }

  public void setPortNumber(Integer portNumber) {
    this.portNumber = portNumber;
  }

  public Map<String, Parameter> getParams() {
    return params;
  }

  public void setParams(Map<String, Parameter> params) {
    this.params = params;
  }

  /**
   * creates a clone of this port with empty params
   * 
   * @return
   */
  public InputPort emptyClone() {
    return new InputPort(name, description, portNumber, null, type);
  }
}
