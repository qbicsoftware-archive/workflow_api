package submitter.parameters;

public class BooleanParameter extends Parameter {
  private boolean value;
  private boolean isSet = false;

  public BooleanParameter(String title, String description, boolean advanced, boolean required) {
    super(title, description, advanced, required);
    // TODO Auto-generated constructor stub
  }

  @Override
  public void validate() throws IllegalArgumentException {
    if (this.isRequired() && !isSet) {
      throw new IllegalArgumentException();
    }
  }

  @Override
  public Object getValue() {
    return value;
  }

  @Override
  public boolean isValid(Object value) {
    try {
      boolean val;
      if (value instanceof String) {
        val = Boolean.valueOf((String) value);
      } else {
        val = (boolean) value;
      }
    } catch (ClassCastException e) {
      return false;
    }
    return true;
  }

  @Override
  public void setValue(Object value) throws IllegalArgumentException {
    if (!isValid(value)) {
      isSet = false;
      throw new IllegalArgumentException();
    }

    if (value instanceof String) {
      this.value = Boolean.valueOf((String) value);
    } else {
      this.value = (boolean) value;
    }
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

}
