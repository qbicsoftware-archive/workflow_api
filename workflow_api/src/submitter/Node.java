package submitter;

import java.util.Map;

import submitter.parameters.Parameter;
import submitter.parameters.ParameterSet;

public class Node extends ParameterSet {
  public Node(String title, String description, Map<String, Parameter> params) {
    super(title, description, params);
  }
}
