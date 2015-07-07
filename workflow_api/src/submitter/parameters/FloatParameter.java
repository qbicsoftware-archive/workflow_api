package submitter.parameters;

public class FloatParameter extends Parameter {

  private float minimum, maximum;
  private float value;
  private boolean isSet = false;

  public FloatParameter(String title, String description, boolean advanced, boolean required,
      float minimum, float maximum) {
    super(title, description, advanced, required);
    this.minimum = minimum;
    this.maximum = maximum;
  }

  @Override
  public boolean isValid(Object value) {

    return (minimum <= (float) value) && ((float) value <= maximum);
  }

  @Override
  public void setValue(Object value) throws IllegalArgumentException {
    float val = 0;

    if (value instanceof String) {
      try {
        val = Float.parseFloat((String) value);
      } catch (NumberFormatException e) {

      }
    } else {
      val = (float) value;
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
  public void validate() throws IllegalArgumentException {
    if (this.isRequired() && !isSet) {
      throw new IllegalArgumentException();
    }
  }

  @Override
  public Object getValue() {
    return value;
  }

  public float getMinimum() {
    return minimum;
  }

  public void setMinimum(float minimum) {
    this.minimum = minimum;
  }

  public float getMaximum() {
    return maximum;
  }

  public void setMaximum(float maximum) {
    this.maximum = maximum;
  }


}
