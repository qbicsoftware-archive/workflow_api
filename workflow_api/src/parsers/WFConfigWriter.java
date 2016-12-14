package parsers;

import guse.workflowrepresentation.GuseNode;
import guse.workflowrepresentation.GuseWorkflowRepresentation;
import guse.workflowrepresentation.InputPort;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import submitter.parameters.BooleanParameter;
import submitter.parameters.FileListParameter;
import submitter.parameters.FileParameter;
import submitter.parameters.FloatParameter;
import submitter.parameters.IntParameter;
import submitter.parameters.Parameter;
import submitter.parameters.StringParameter;

public class WFConfigWriter {

  public static void main(String[] args) throws Exception {
    WFConfigReader c = new WFConfigReader();
    GuseWorkflowRepresentation gwfr = c.read(new File("/PATH/TO/new_testworkflow.config"));

    WFConfigWriter w = new WFConfigWriter();
    w.write("/PATH/TO/new_testworkflow.json", gwfr);
  }

  public void write(String pathToConfig, GuseWorkflowRepresentation guseWorkflow)
      throws JSONException, IOException {

    JSONObject root = new JSONObject();
    JSONObject workflow = new JSONObject();

    workflow.put("name", guseWorkflow.getName());
    workflow.put("version", guseWorkflow.getVersion());
    workflow.put("description", guseWorkflow.getDescription());
    workflow.put("experimenttype", guseWorkflow.getExperimentType());
    workflow.put("sampletype", guseWorkflow.getSampleType());
    workflow.put("filedirectory", guseWorkflow.getDirectory());
    workflow.put("id", guseWorkflow.getID());

    // iterate over nodes of workflow
    // TODO function to getNodes ?
    Map<String, GuseNode> workflowNodes = guseWorkflow.getNodesNew();
    JSONArray jsonWorkflowNodes = new JSONArray();

    for (Map.Entry<String, GuseNode> entryNode : workflowNodes.entrySet()) {

      GuseNode guseNode = entryNode.getValue();
      JSONObject jsonGuseNode = new JSONObject();

      jsonGuseNode.put("name", guseNode.getTitle());
      jsonGuseNode.put("description", guseNode.getDescription());
      jsonGuseNode.put("grid", guseNode.getGrid());
      jsonGuseNode.put("gridtype", guseNode.getGridType());
      jsonGuseNode.put("resource", guseNode.getResource());

      jsonGuseNode.put("cmdparams", new JSONArray(guseNode.getCmdParams()));
      jsonGuseNode.put("moabkeydirectives", new JSONArray(guseNode.getMoabKeydirectives()));

      // iterate over input ports of node
      Map<String, InputPort> nodeInputPorts = guseNode.getInputPorts();
      JSONArray jsonNodeInputPorts = new JSONArray();

      for (Map.Entry<String, InputPort> entryInputPort : nodeInputPorts.entrySet()) {

        InputPort inputPort = entryInputPort.getValue();
        JSONObject jsonInputPort = new JSONObject();

        jsonInputPort.put("name", inputPort.getName());
        jsonInputPort.put("description", inputPort.getDescription());
        jsonInputPort.put("portnumber", inputPort.getPortNumber());
        jsonInputPort.put("type", inputPort.getType());

        // iterate over parameters of input port
        Map<String, Parameter> inputPortParameters = inputPort.getParams();
        JSONArray jsonInputPortParameters = new JSONArray();

        for (Map.Entry<String, Parameter> entryParameter : inputPortParameters.entrySet()) {

          Parameter inputPortParameter = entryParameter.getValue();
          JSONObject jsonParameter = new JSONObject();

          jsonParameter.put("name", inputPortParameter.getTitle());
          jsonParameter.put("description", inputPortParameter.getDescription());


          if (inputPortParameter instanceof StringParameter) {
            StringParameter stringParameter = (StringParameter) inputPortParameter;
            jsonParameter.put("type", "String");
            jsonParameter.put("range", new JSONArray(stringParameter.getRange()));
            jsonParameter.put("default", inputPortParameter.getValue());
            jsonParameter.put("required", stringParameter.isRequired());

          } else if (inputPortParameter instanceof IntParameter) {
            IntParameter integerParameter = (IntParameter) inputPortParameter;
            jsonParameter.put("type", "Integer");
            jsonParameter.put("default", inputPortParameter.getValue());

            JSONArray jsonParameterRange = new JSONArray();
            jsonParameterRange.put(integerParameter.getMinimum());
            jsonParameterRange.put(integerParameter.getMaximum());
            jsonParameter.put("range", jsonParameterRange);
            jsonParameter.put("required", integerParameter.isRequired());

          } else if (inputPortParameter instanceof FloatParameter) {
            FloatParameter floatParameter = (FloatParameter) inputPortParameter;
            jsonParameter.put("type", "Float");
            jsonParameter.put("default", inputPortParameter.getValue());

            JSONArray jsonParameterRange = new JSONArray();
            jsonParameterRange.put(floatParameter.getMinimum());
            jsonParameterRange.put(floatParameter.getMaximum());
            jsonParameter.put("range", jsonParameterRange);
            jsonParameter.put("required", floatParameter.isRequired());


          } else if (inputPortParameter instanceof FileParameter) {
            FileParameter fileParameter = (FileParameter) inputPortParameter;
            jsonParameter.put("type", "File");
            jsonParameter.put("default", inputPortParameter.getValue());
            jsonParameter.put("range", new JSONArray(fileParameter.getRange()));
            jsonParameter.put("required", fileParameter.isRequired());
          }

          else if (inputPortParameter instanceof FileListParameter) {
            FileListParameter fileListParameter = (FileListParameter) inputPortParameter;
            jsonParameter.put("type", "FileList");
            jsonParameter.put("default",
                new JSONArray((List<String>) inputPortParameter.getValue()));
            jsonParameter.put("range", new JSONArray(fileListParameter.getRange()));
            jsonParameter.put("required", fileListParameter.isRequired());
          }

          else if (inputPortParameter instanceof BooleanParameter) {
            BooleanParameter boolParameter = (BooleanParameter) inputPortParameter;
            jsonParameter.put("type", "Bool");
            jsonParameter.put("default", inputPortParameter.getValue());
            jsonParameter.put("range", new JSONArray(new ArrayList<String>()));
            jsonParameter.put("required", boolParameter.isRequired());
          }

          jsonInputPortParameters.put(jsonParameter);
        }

        jsonInputPort.put("parameters", jsonInputPortParameters);
        jsonNodeInputPorts.put(jsonInputPort);
      }

      jsonGuseNode.put("inputports", jsonNodeInputPorts);
      jsonWorkflowNodes.put(jsonGuseNode);
    }

    workflow.put("nodes", jsonWorkflowNodes);
    root.put("workflow", workflow);

    FileWriter newConfigFile = new FileWriter(pathToConfig);
    // WATCH OUT! Somehow if indentFactor=2, information gets lost
    newConfigFile.write(root.toString(3));
    newConfigFile.flush();
    newConfigFile.close();
  }

  /*
   * public void write(String path, Workflow wf) throws JSONException, IOException { JSONObject root
   * = new JSONObject(); JSONObject workflow = new JSONObject(); workflow.put("name", wf.getName());
   * workflow.put("version", wf.getVersion()); workflow.put("description", wf.getDescription());
   * workflow.put("experimenttype", wf.getExperimentType()); workflow.put("datasettype",
   * wf.getDatasetType()); ParameterSet params = wf.getParameters(); Map<String,
   * List<SimpleEntry<String, String>>> paramtonodemaps = wf.getParameterToNodesMapping(); JSONArray
   * jParams = new JSONArray(); for (String pName : params.getParamNames()) { Parameter p =
   * params.getParam(pName); JSONObject jParam = new JSONObject(); jParam.put("name", p.getTitle());
   * jParam.put("description", p.getDescription()); JSONArray namesInNodes = new JSONArray();
   * JSONArray nodeNames = new JSONArray(); if(paramtonodemaps.containsKey(p.getTitle())){
   * List<SimpleEntry<String, String>> paramMaps = paramtonodemaps.get(p.getTitle());
   * for(SimpleEntry<String, String> entry : paramMaps){ namesInNodes.put(entry.getKey());
   * nodeNames.put(entry.getValue()); } } jParam.put("namesInNodes", namesInNodes);
   * jParam.put("nodeNames", nodeNames);
   * 
   * jParam.put("default", p.getValue()); JSONArray range = new JSONArray(); if (p instanceof
   * StringParameter) { StringParameter sp = (StringParameter) p; jParam.put("type", "String"); for
   * (String s : sp.getRange()) range.put(s); } else if (p instanceof IntParameter) { IntParameter
   * ip = (IntParameter) p; jParam.put("type", "Integer"); range.put(ip.getMinimum());
   * range.put(ip.getMaximum()); } else if (p instanceof FloatParameter) { FloatParameter fp =
   * (FloatParameter) p; jParam.put("type", "Float"); range.put(fp.getMinimum());
   * range.put(fp.getMaximum());
   * 
   * } jParam.put("range", range); jParams.put(jParam); } for (Map.Entry<String, Parameter> entry :
   * wf.getData().getData().entrySet()) { FileParameter fp = (FileParameter) entry.getValue();
   * JSONObject jParam = new JSONObject(); jParam.put("name", fp.getTitle());
   * jParam.put("description", fp.getTitle()); jParam.put("type", "File"); JSONArray namesInNodes =
   * new JSONArray(); JSONArray nodeNames = new JSONArray();
   * if(paramtonodemaps.containsKey(fp.getTitle())){ List<SimpleEntry<String, String>> paramMaps =
   * paramtonodemaps.get(fp.getTitle()); for(SimpleEntry<String, String> e : paramMaps){
   * namesInNodes.put(e.getKey()); nodeNames.put(e.getValue()); } } jParam.put("namesInNodes",
   * namesInNodes); jParam.put("nodeNames", nodeNames);
   * 
   * jParam.put("default", fp.getDefaultValue()); jParam.put("range", new JSONArray());
   * jParams.put(jParam); } workflow.put("params", jParams); root.put("workflow", workflow);
   * FileWriter file = new FileWriter(path); file.write(root.toString(2)); file.flush();
   * file.close(); }
   */
}
