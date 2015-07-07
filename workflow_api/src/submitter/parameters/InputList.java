package submitter.parameters;

import java.util.Map;

import org.json.JSONArray;

import submitter.ParameterSetFactory;
import submitter.Submitter;

public class InputList { // TODO extends Parameter?
  private String title;
  private String description;
  private Map<String, Parameter> data; // TODO OrderedMap?

  /**
   * Parameter sets should be created by the implementations of {@link Submitter}. These can use the
   * {@link ParameterSetFactory}.
   * 
   * @param title The title of the parameter set. A view might show this as label.
   * @param description A longer description of the parameter set. A view might show this as
   *        tooltip.
   * @param params {@code params} describes which parameters are expected.
   */
  public InputList(String title, String description, Map<String, Parameter> params) {
    this.title = title;
    this.description = description;
    this.data = params;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public String asXML() {
    throw new RuntimeException();
  }

  public JSONArray asJSON() {
    JSONArray arr = new JSONArray();
    for (Map.Entry<String, Parameter> entry : getData().entrySet()) {
      arr.put(entry.getValue().getValue());
    }
    return arr;

  }

  public Parameter getParam(Object key) {
    return data.get(key);
  }

  public Map<String, Parameter> getData() {
    return data;
  }

  public void setData(Map<String, Parameter> data) {
    this.data = data;
  }

  public void validate() throws IllegalArgumentException {
    for (Map.Entry<String, Parameter> entry : getData().entrySet()) {
      entry.getValue().validate();
    }
  }

  public int size() {
    return data.size();
  }
}
