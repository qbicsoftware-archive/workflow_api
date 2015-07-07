package submitter;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

import submitter.parameters.InputList;
import submitter.parameters.Parameter;
import submitter.parameters.ParameterSet;

public class Workflow {
  protected ParameterSet params;
  protected InputList data;
  protected List<Node> nodes;

  private String name;
  private String description;
  private String version;
  private String id;
  private String experimentType;
  private String sampleType;
  private Map<String, List<AbstractMap.SimpleEntry<String, String>>> parameterToNodesMapping;

  public Workflow(String id, String name, String description, String version, InputList data,
      ParameterSet params, String experimentType, String sampleType) {
    this.name = name;
    this.description = description;
    this.version = version;
    this.id = id;
    this.data = data;
    this.params = params;
    this.experimentType = experimentType;
    this.sampleType = sampleType;
  }

  /**
   * Workflow-global parameters. Those could be parameters that are used in several nodes but should
   * be set only once.
   * 
   * @return
   */
  public ParameterSet getParameters() {
    return params;
  }

  /**
   * Check if all required parameters are set and throw an error if not.
   * 
   * @throws IllegalArgumentException
   */
  public void validate() throws IllegalArgumentException {
    params.validate();
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public String getVersion() {
    return version;
  }

  public List<Node> getNodes() {
    return nodes;
  }

  public String getID() {
    return id;
  }

  public InputList getData() {
    return data;
  }

  public void setData(InputList data) {
    this.data = data;
  }


  public String getExperimentType() {
    return experimentType;
  }

  public void setExperimentType(String experimentType) {
    this.experimentType = experimentType;
  }

  public String getSampleType() {
    return sampleType;
  }

  public void setSampleType(String datasetType) {
    this.sampleType = datasetType;
  }

  /**
   * the return value is Map<Paramter name, List<entry<namesInNodes, nodeNames >>>
   * 
   * @return
   */
  public Map<String, List<AbstractMap.SimpleEntry<String, String>>> getParameterToNodesMapping() {
    return parameterToNodesMapping;
  }

  public void setParameterToNodesMapping(
      Map<String, List<AbstractMap.SimpleEntry<String, String>>> parameterToNodesMapping) {
    this.parameterToNodesMapping = parameterToNodesMapping;
  }

  public void setPathOfInput(String fullPath) {
    Map<String, Parameter> inpMap = getData().getData();
    Parameter firstInput = (Parameter) inpMap.entrySet().iterator().next().getValue();
    firstInput.setValue(fullPath);
  }

  public List<String> getFileTypes() {
    // TODO Auto-generated method stub
    return null;
  }
}
