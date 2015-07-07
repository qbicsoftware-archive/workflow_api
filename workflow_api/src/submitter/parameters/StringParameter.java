package submitter.parameters;

import java.util.List;

public class StringParameter extends Parameter {
  private String value;
  private boolean isSet = false;
  private List<String> range;

  public List<String> getRange() {
    return range;
  }

  public void setRange(List<String> range) {
    this.range = range;
  }

  public StringParameter(String title, String description, boolean advanced, boolean required,
      List<String> options) {
    super(title, description, advanced, required);
    range = options;
  }

  @Override
  public void validate() throws IllegalArgumentException {
    if (this.isRequired() && !isSet) {
      throw new IllegalArgumentException();
    }
  }

  @Override
  public boolean isValid(Object value) {
    String val;
    try {
      val = (String) value;
    } catch (ClassCastException e) { // TODO ?????
      return false;
    }
    return val != null;
  }

  @Override
  public void setValue(Object value) throws IllegalArgumentException {
    if (!isValid(value)) {
      isSet = false;
      throw new IllegalArgumentException();
    }

    this.value = (String) value;
    isSet = true;

  }

  @Override
  public void addToXML() {
    assert isSet;
    // TODO Auto-generated method stub

  }

  @Override
  public void addToJSON(String name, String json) {
    assert isSet;
    // TODO Auto-generated method stub

  }

  @Override
  public Object getValue() {
    return value;
  }

}
