package qsnake;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import submitter.Workflow;
import submitter.parameters.InputList;
import submitter.parameters.Parameter;
import submitter.parameters.ParameterSet;

public class QSnakeWorkflowFactory {

  public Workflow fromJSON(JSONObject data) throws JSONException {
    if (!data.getString("title").equals("qtest")) {
      throw new RuntimeException();
    }

    // InputList now has a Map instead of a List
    // InputList inputData = new InputList("data", "Input files", new ArrayList<Parameter>());
    InputList inputData = new InputList("data", "Input files", new HashMap<String, Parameter>());
    ParameterSet params =
        new ParameterSet("parameters", "parameters", new HashMap<String, Parameter>());
    return new Workflow(null, null, null, null, inputData, params, null, null);
    // new Workflow(data.getString("_id"), data.getString("title"),
    // data.getString("description"), data.getString("version"), inputData, params, null);
  }

}
