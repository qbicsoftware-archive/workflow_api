package submitter.parameters;

public class IntParameter extends Parameter {
  private int minimum, maximum;
  private int value;
  private boolean isSet = false;

  public IntParameter(String title, String description, boolean advanced, boolean required,
      int minimum, int maximum) {
    super(title, description, advanced, required);
    this.minimum = minimum;
    this.maximum = maximum;
  }

  public int getMinimum() {
    return minimum;
  }

  public void setMinimum(int minimum) {
    this.minimum = minimum;
  }

  public int getMaximum() {
    return maximum;
  }

  public void setMaximum(int maximum) {
    this.maximum = maximum;
  }

  @Override
  public void validate() throws IllegalArgumentException {
    if (this.isRequired() && !isSet) {
      throw new IllegalArgumentException();
    }
  }

  @Override
  public boolean isValid(Object value) {

    return (minimum <= (int) value) && ((int) value <= maximum);
  }

  @Override
  public void setValue(Object value) throws IllegalArgumentException {
    int val = 0;

    if (value instanceof String) {
      try {
        val = Integer.parseInt((String) value);
      } catch (NumberFormatException e) {

      }
    } else {
      val = (int) value;
    }
    if (!isValid(val)) {
      isSet = false;
      throw new IllegalArgumentException();
    }
    this.value = val;
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
