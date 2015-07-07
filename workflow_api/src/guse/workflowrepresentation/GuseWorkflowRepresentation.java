package guse.workflowrepresentation;

import guse.workflowrepresentation.InputPort.Type;

import java.io.File;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.NotImplementedException;

import submitter.Node;
import submitter.parameters.FileListParameter;
import submitter.parameters.FileParameter;
import submitter.parameters.InputList;
import submitter.parameters.Parameter;
import submitter.parameters.ParameterSet;
import de.uni_tuebingen.qbic.guseWorkflowBeans.Workflow;

public class GuseWorkflowRepresentation extends submitter.Workflow {

  // no need to be seen in Workflow? What about the directory?
  // guse xml
  Workflow workflowXml;
  // which directory is it located.
  File directory;

  Map<String, GuseNode> nodes;

  public Workflow getWorkflowXml() {
    return workflowXml;
  }

  public void setWorkflowXml(Workflow workflowXml) {
    this.workflowXml = workflowXml;
  }

  public File getDirectory() {
    return directory;
  }

  public void setDirectory(File directory) {
    this.directory = directory;
  }

  public GuseWorkflowRepresentation(String id, String name, String description, String version,
      InputList data, ParameterSet params, String experimentType, String sampleType) {
    super(id, name, description, version, data, params, experimentType, sampleType);

  }


  public void setNodes(Map<String, GuseNode> nodes) {
    this.nodes = nodes;
  }

  public Map<String, GuseNode> getNodesNew() {
    return this.nodes;
  }


  public List<GuseNode> getGuseNodes() {

    return new ArrayList<GuseNode>(nodes.values());
  }

  @Override
  public List<Node> getNodes() {

    return new ArrayList<Node>(nodes.values());
  }

  @Override
  public ParameterSet getParameters() {
    Map<String, Parameter> workflowParams = new HashMap<String, Parameter>();

    for (Map.Entry<String, GuseNode> nodeEntry : getNodesNew().entrySet()) {
      GuseNode guseNode = nodeEntry.getValue();
      for (Entry<String, InputPort> portEntry : guseNode.getInputPorts().entrySet()) {
        InputPort inputPort = portEntry.getValue();
        if (inputPort.type == Type.CTD || inputPort.type == Type.CTD_ZIP) {
          workflowParams.putAll(inputPort.getParams());
        }
      }
    }
    return new ParameterSet(getName(), getDescription(), workflowParams);
  }

  @Override
  public InputList getData() {
    Map<String, Parameter> workflowInput = new HashMap<String, Parameter>();

    for (Map.Entry<String, GuseNode> nodeEntry : getNodesNew().entrySet()) {
      GuseNode guseNode = nodeEntry.getValue();
      for (Entry<String, InputPort> portEntry : guseNode.getInputPorts().entrySet()) {
        InputPort inputPort = portEntry.getValue();
        if (inputPort.type == Type.FILESTOSTAGE) {
          workflowInput.putAll(inputPort.getParams());
        }
      }
    }


    return new InputList(getName(), getDescription(), workflowInput);

  }

  @Override
  public void setData(InputList data) {
    // TODO Auto-generated method stub
    throw new NotImplementedException();
  }

  @Override
  public Map<String, List<SimpleEntry<String, String>>> getParameterToNodesMapping() {
    // TODO Auto-generated method stub
    throw new NotImplementedException();
  }

  @Override
  public void setParameterToNodesMapping(
      Map<String, List<SimpleEntry<String, String>>> parameterToNodesMapping) {
    throw new NotImplementedException();
  }

  @Override
  public void setPathOfInput(String fullPath) {
    // TODO Auto-generated method stub
    throw new NotImplementedException();
  }

  @Override
  public List<String> getFileTypes() {
    List<String> fileTypes = new ArrayList<String>();

    for (Map.Entry<String, GuseNode> nodeEntry : getNodesNew().entrySet()) {
      GuseNode guseNode = nodeEntry.getValue();
      for (Entry<String, InputPort> portEntry : guseNode.getInputPorts().entrySet()) {
        InputPort inputPort = portEntry.getValue();
        if (inputPort.type == Type.FILESTOSTAGE) {
          for (Entry<String, Parameter> entry : inputPort.getParams().entrySet()) {
            if (!entry.getValue().getTitle().endsWith(".db")) {
              List<String> range = null;
              if (entry.getValue() instanceof FileParameter) {
                FileParameter fileParam = (FileParameter) entry.getValue();
                range = fileParam.getRange();
              } else {
                FileListParameter fileListParam = (FileListParameter) entry.getValue();
                range = fileListParam.getRange();
              }
              List<String> rangeUpperCase = new ArrayList<String>();
              for (String r : range) {
                rangeUpperCase.add(r.toUpperCase());
              }
              fileTypes.addAll(rangeUpperCase);
            }
          }
        }
      }
    }
    return fileTypes;
  }

  public GuseNode getNode(String nodeName) {
    return this.nodes.get(nodeName);
  }
}
