package parsers;

import guse.workflowrepresentation.GuseNode;
import guse.workflowrepresentation.GuseWorkflowRepresentation;
import guse.workflowrepresentation.InputPort;
import guse.workflowrepresentation.InputPort.Type;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import submitter.parameters.FileListParameter;
import submitter.parameters.FileParameter;
import submitter.parameters.FloatParameter;
import submitter.parameters.IntParameter;
import submitter.parameters.Parameter;
import submitter.parameters.StringParameter;

public class WFConfigReader {

  public static void main(String[] args) throws Exception {
    WFConfigReader c = new WFConfigReader();
    GuseWorkflowRepresentation gwfr =
        c.read(new File(
            "/Users/mohr/Documents/PhD/projects/eclipse_workspace/WorkflowMockUp/test/com/example/workflowmockup/wfconfigs/new_testworkflow.config"));
    System.out.println(gwfr);
  }

  public GuseWorkflowRepresentation read(File file) throws Exception {
    InputStream inputStream = new FileInputStream(file);
    byte[] buffer = new byte[inputStream.available()];
    while (inputStream.read(buffer) != -1);

    String jsonText = new String(buffer);
    JSONObject jsonObject = new JSONObject(jsonText);
    inputStream.close();
    JSONObject jsonWorkflow = jsonObject.getJSONObject("workflow");
    
    String workflowName = jsonWorkflow.getString("name");
    String workflowDescription = jsonWorkflow.getString("description");
    String workflowVersion = jsonWorkflow.getString("version");
    String workflowSampleType = jsonWorkflow.getString("sampletype");
    String workflowExperimentType = jsonWorkflow.getString("experimenttype");
    File workflowFileDirectory = new File(jsonWorkflow.getString("filedirectory"));
    String id = jsonWorkflow.getString("id");
    JSONArray jsonWorkflowNodes = jsonWorkflow.getJSONArray("nodes");

    // node map of workflow
    Map<String, GuseNode> workflowNodes = new HashMap<String, GuseNode>();

    // iterate over nodes of workflow
    for (int i = 0; i < jsonWorkflowNodes.length(); i++) {
      JSONObject jsonNode = jsonWorkflowNodes.getJSONObject(i);

      String nodeName = jsonNode.getString("name");
      String nodeDescription = jsonNode.getString("description");
      String nodeGridType = jsonNode.getString("gridtype");
      String nodeResource = jsonNode.getString("resource");
      String nodeGrid = jsonNode.getString("grid");

      JSONArray jsonNodeCMDparams = jsonNode.getJSONArray("cmdparams");
      JSONArray jsonNodeMoabDirectives = jsonNode.getJSONArray("moabkeydirectives");

      List<String> nodeCMDparams = new ArrayList<String>();
      List<String> nodeMoabDirectives = new ArrayList<String>();

      for (int j = 0; j < jsonNodeCMDparams.length(); j++) {
        nodeCMDparams.add(jsonNodeCMDparams.getString(j));
      }
      for (int k = 0; k < jsonNodeMoabDirectives.length(); k++) {
        nodeMoabDirectives.add(jsonNodeMoabDirectives.getString(k));
      }

      JSONArray workflowInputPorts = jsonNode.getJSONArray("inputports");

      // input port map per Node
      HashMap<String, InputPort> inputPortMap = new HashMap<String, InputPort>();

      // iterate over input ports of node
      for (int ii = 0; ii < workflowInputPorts.length(); ii++) {
        JSONObject jsonInputPort = workflowInputPorts.getJSONObject(ii);

        String inputPortName = jsonInputPort.getString("name");
        String inputPortDescription = jsonInputPort.getString("description");
        Integer inputPortNumber = Integer.parseInt(jsonInputPort.getString("portnumber"));
        Type inputPortType = Type.valueOf(jsonInputPort.getString("type"));

        JSONArray jsonInputPortParams = jsonInputPort.getJSONArray("parameters");

        // parameter map per input port
        HashMap<String, Parameter> paramMap = new HashMap<String, Parameter>();

        // iterate over parameters of input ports
        for (int iii = 0; iii < jsonInputPortParams.length(); iii++) {
          JSONObject jsonInputPortParam = jsonInputPortParams.getJSONObject(iii);

          String paramName = jsonInputPortParam.getString("name");
          String paramDescription = jsonInputPortParam.getString("description");
          String paramType = jsonInputPortParam.getString("type");
          JSONArray paramRange = jsonInputPortParam.getJSONArray("range");

          switch (ParameterTypes.valueOf(paramType.toUpperCase())) {
            case STRING:
              List<String> strRange = new ArrayList<String>();
              String paramDefaultString = jsonInputPortParam.getString("default");

              for (int jjj = 0; jjj < paramRange.length(); jjj++)
                strRange.add(paramRange.getString(jjj));

              StringParameter stringParam =
                  new StringParameter(paramName, paramDescription, false, true, strRange);
              stringParam.setValue(paramDefaultString);
              paramMap.put(paramName, stringParam);
              break;

            case FILE:
              List<String> fileRange = new ArrayList<String>();
              String paramDefaultFile = jsonInputPortParam.getString("default");

              for (int jjj = 0; jjj < paramRange.length(); jjj++)
                fileRange.add(paramRange.getString(jjj));

              FileParameter fileParam =
                  new FileParameter(paramName, paramDescription, false, true, null, fileRange);
              fileParam.setValue(paramDefaultFile);
              paramMap.put(paramName, fileParam);
              break;

            case FILELIST:
              List<String> fileListRange = new ArrayList<String>();
              List<String> fileListDefault = new ArrayList<String>();
              JSONArray paramDefaultFileListJSON = jsonInputPortParam.getJSONArray("default");

              for (int kkk = 0; kkk < paramDefaultFileListJSON.length(); kkk++)
                fileListDefault.add(paramDefaultFileListJSON.getString(kkk));

              for (int jjj = 0; jjj < paramRange.length(); jjj++)
                fileListRange.add(paramRange.getString(jjj));

              FileListParameter fileListParam =
                  new FileListParameter(paramName, paramDescription, false, true, null,
                      fileListRange);
              fileListParam.setValue(fileListDefault);
              paramMap.put(paramName, fileListParam);
              break;

            case INTEGER:
              List<Integer> intRange = new ArrayList<Integer>();
              String paramDefaultInt = jsonInputPortParam.getString("default");

              for (int jjj = 0; jjj < paramRange.length(); jjj++)
                intRange.add(paramRange.getInt(jjj));

              IntParameter integerParam =
                  new IntParameter(paramName, paramDescription, false, true, intRange.get(0),
                      intRange.get(1));
              integerParam.setValue(Integer.parseInt(paramDefaultInt));
              paramMap.put(paramName, integerParam);
              break;

            case DOUBLE:
              List<Double> dblRange = new ArrayList<Double>();
              String paramDefaultDouble = jsonInputPortParam.getString("default");


              // TODO
              break;

            case FLOAT:
              List<Float> fltRange = new ArrayList<Float>();
              String paramDefaultFloat = jsonInputPortParam.getString("default");

              for (int jjj = 0; jjj < paramRange.length(); jjj++)
                fltRange.add((float) paramRange.getDouble(jjj));

              FloatParameter floatParam =
                  new FloatParameter(paramName, paramDescription, false, true, fltRange.get(0),
                      fltRange.get(1));
              floatParam.setValue(Float.parseFloat(paramDefaultFloat));
              paramMap.put(paramName, floatParam);
              break;

            case BOOLEAN:
              List<Boolean> boolRange = new ArrayList<Boolean>();
              String paramDefaultBool = jsonInputPortParam.getString("default");

              // TODO
              break;

            default:
              break;
          }
        }
        InputPort newInputPort =
            new InputPort(inputPortName, inputPortDescription, inputPortNumber, paramMap,
                inputPortType);
        inputPortMap.put(inputPortName, newInputPort);
      }
      // TODO fill parameter map here ?
      GuseNode newNode = new GuseNode(nodeName, nodeDescription, null);
      newNode.setGridType(nodeGridType);
      newNode.setResource(nodeResource);
      newNode.setGrid(nodeGrid);
      newNode.setParams(nodeCMDparams);
      newNode.setMoabKeydirectives(nodeMoabDirectives);
      newNode.setInputPorts(inputPortMap);
      workflowNodes.put(nodeName, newNode);
    }
    GuseWorkflowRepresentation guseWorkflow =
        new GuseWorkflowRepresentation(id, workflowName, workflowDescription, workflowVersion, null,
            null, workflowExperimentType, workflowSampleType);
    guseWorkflow.setNodes(workflowNodes);
    guseWorkflow.setDirectory(workflowFileDirectory);
    return guseWorkflow;
  }
}

/*
 * public Workflow read(File file) throws Exception { InputStream is = new FileInputStream(file);
 * byte[] buffer = new byte[is.available()]; while (is.read(buffer) != -1); String jsontext = new
 * String(buffer); JSONObject obj = new JSONObject(jsontext); is.close(); JSONObject wf =
 * obj.getJSONObject("workflow"); String name = wf.getString("name"); String version =
 * wf.getString("version"); String description = wf.getString("description"); String datasetType =
 * wf.getString("datasettype"); String experimentType = wf.getString("experimenttype"); JSONArray
 * jsonParams = wf.getJSONArray("params");
 * 
 * Map<String, Parameter> fileParams = new HashMap<String, Parameter>(); Map<String, Parameter>
 * paramMap = new HashMap<String, Parameter>(); Map<String, List<AbstractMap.SimpleEntry<String,
 * String>>> parameterNodeMapping = new HashMap<String, List<AbstractMap.SimpleEntry<String,
 * String>>>();
 * 
 * for (int i = 0; i < jsonParams.length(); i++) { JSONObject p = jsonParams.getJSONObject(i);
 * 
 * String pName = p.getString("name"); String pDesc = p.getString("description"); JSONArray
 * namesInNodes = p.getJSONArray("namesInNodes"); JSONArray nodeNames = p.getJSONArray("nodeNames");
 * String type = p.getString("type"); JSONArray range = p.getJSONArray("range"); String def =
 * p.getString("default");
 * 
 * List<String> strRange = new ArrayList<String>(); List<Integer> intRange = new
 * ArrayList<Integer>(); List<Float> fltRange = new ArrayList<Float>(); List<Double> dblRange = new
 * ArrayList<Double>();
 * 
 * List<AbstractMap.SimpleEntry<String, String>> entryList = new
 * ArrayList<AbstractMap.SimpleEntry<String, String>>();
 * 
 * for (int j = 0; j != namesInNodes.length(); j++) { entryList.add(new
 * AbstractMap.SimpleEntry<String, String>(nodeNames.get(j).toString(),
 * namesInNodes.get(j).toString())); } parameterNodeMapping.put(pName, entryList);
 * 
 * switch (ParameterTypes.valueOf(type.toUpperCase())) { case STRING: for (int j = 0; j <
 * range.length(); j++) strRange.add(range.getString(j)); StringParameter s = new
 * StringParameter(pName, pDesc, false, true, strRange); s.setValue(def); paramMap.put(pName, s);
 * break;
 * 
 * case FILE: for (int j = 0; j < range.length(); j++) strRange.add(range.getString(j));
 * FileParameter fileParameter = new FileParameter(pName, pDesc, false, true, null, def, strRange);
 * fileParameter.setValue(def); // fileParams.add(fileParameter); fileParams.put(pName,
 * fileParameter); break;
 * 
 * case INTEGER: for (int j = 0; j < range.length(); j++) intRange.add(range.getInt(j)); if
 * (intRange.size() != 2) throw new Exception(intRange.size() +
 * " is wrong size for the Integer range array!"); IntParameter ip = new IntParameter(pName, pDesc,
 * false, true, intRange.get(0), intRange.get(1)); ip.setValue(Integer.parseInt(def));
 * paramMap.put(pName, ip); break;
 * 
 * case DOUBLE:
 * 
 * case FLOAT: for (int j = 0; j < range.length(); j++) fltRange.add((float) range.getDouble(j)); if
 * (fltRange.size() != 2) throw new Exception(fltRange.size() +
 * " is wrong size for the Float range array!");
 * 
 * FloatParameter f = new FloatParameter(pName, pDesc, false, true, fltRange.get(0),
 * fltRange.get(1)); f.setValue(Float.parseFloat(def)); paramMap.put(pName, f); break;
 * 
 * case BOOLEAN:// TODO implement break;
 * 
 * default: break; } } InputList data = new InputList("Files", "Input files", fileParams);
 * ParameterSet params = new ParameterSet("ParameterTypes", "Workflow parameters", paramMap);
 * Workflow newWorkflow = new Workflow("3.14", name, description, version, data, params,
 * experimentType, datasetType); newWorkflow.setParameterToNodesMapping(parameterNodeMapping);
 * return newWorkflow; }
 */

