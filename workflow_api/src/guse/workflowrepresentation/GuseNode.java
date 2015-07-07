package guse.workflowrepresentation;

import guse.workflowrepresentation.InputPort.Type;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.NotImplementedException;
import org.json.JSONObject;

import submitter.parameters.Parameter;

public class GuseNode extends submitter.Node {
  public static String portParamSeperator = ":";
  Map<String, InputPort> inputPorts;
  // submitter. e.g. moab, local.
  String gridType;
  // resource hpc, cfc
  String resource;
  // hpc-bw, qbicheadnode.local
  String grid;


  // String array representation of the command line (is one string in the xml file)
  List<String> cmdParams;

  public List<String> getCmdParams() {
    return cmdParams;
  }

  public void setCmdParams(List<String> cmdParams) {
    this.cmdParams = cmdParams;
  }

  // directives for the moab submitter.
  List<String> moabKeydirectives;

  public GuseNode(String title, String description, Map<String, Parameter> params) {
    super(title, description, params);
    // TODO params have to be set with setParameters or whatever
  }

  public GuseNode() {
    super(null, null, null);
  }

  public Map<String, InputPort> getInputPorts() {
    return inputPorts;
  }

  /**
   * sets the input ports. If inputPorts is null. Internally an empyt map will will be set.
   * 
   * @param inputPorts
   */
  public void setInputPorts(Map<String, InputPort> inputPorts) {
    if (inputPorts == null) {
      inputPorts = new HashMap<String, InputPort>();
    } else {
      this.inputPorts = inputPorts;
    }
  }

  public String getGridType() {
    return gridType;
  }

  public void setGridType(String gridType) {
    this.gridType = gridType;
  }

  public String getResource() {
    return resource;
  }

  public void setResource(String resource) {
    this.resource = resource;
  }

  public String getGrid() {
    return grid;
  }

  public void setGrid(String grid) {
    this.grid = grid;
  }

  public List<String> getMoabKeydirectives() {
    return moabKeydirectives;
  }

  public void setMoabKeydirectives(List<String> moabKeydirectives) {
    this.moabKeydirectives = moabKeydirectives;
  }

  public void setParams(List<String> params) {
    this.cmdParams = params;
  }



  @Override
  public Map<String, Parameter> getParams() {
    // TODO Auto-generated method stub
    throw new NotImplementedException();
  }

  @Override
  public void setParams(Map<String, Parameter> params) {
    // TODO Auto-generated method stub
    throw new NotImplementedException();
  }

  @Override
  public String asXML() {
    // TODO Auto-generated method stub
    throw new NotImplementedException();
  }

  @Override
  public JSONObject asJSON() {
    // TODO Auto-generated method stub
    throw new NotImplementedException();
  }

  @Override
  public Set<String> getParamNames() {
    Set<String> set = new HashSet<String>();
    for (InputPort port : inputPorts.values()) {
      if (port.type == Type.CTD || port.type == Type.CTD_ZIP) {
        for (String p : port.getParams().keySet()) {
          set.add(port.getName() + portParamSeperator + p);
        }
      }

    }
    return set;
  }

  @Override
  public Parameter getParam(String key) {
    String[] params = key.split(portParamSeperator);
    if (params.length == 1) {

      return inputPorts.get(params[0]).getParams().get("");
    }
    return inputPorts.get(params[0]).getParams().get(params[1]);
  }

  public boolean containsPort(String portName) {
    return inputPorts.containsKey(portName);
  }

  /**
   * creates a clone of this node with empty params, ports and directives.
   * 
   * @return
   */
  public GuseNode emptyClone() {
    GuseNode tmp = new GuseNode(this.getTitle(), this.getDescription(), null);
    tmp.setGrid(grid);
    tmp.setGridType(gridType);
    tmp.setResource(resource);
    return tmp;
  }

  public InputPort getPort(String portName) {
    return inputPorts.get(portName);
  }

  public Map<String, InputPort> getPortsByType(Type selectedType) {
    HashMap<String, InputPort> tmp = new HashMap<String, InputPort>();
    for (Iterator<java.util.Map.Entry<String, InputPort>> it = inputPorts.entrySet().iterator(); it
        .hasNext() != false;) {
      java.util.Map.Entry<String, InputPort> entry = it.next();
      if (entry.getValue().type == selectedType) {
        tmp.put(entry.getKey(), entry.getValue());
      }
    }
    return tmp;
  }
}
