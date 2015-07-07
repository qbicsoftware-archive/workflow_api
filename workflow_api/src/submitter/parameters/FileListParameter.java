package submitter.parameters;

import java.util.List;

public class FileListParameter extends Parameter {

  private List<String> value;
  private List<String> range;

  public FileListParameter(String title, String description, boolean advanced, boolean required,
      List<String> value, List<String> range) {
    super(title, description, advanced, required);
    this.value = value;
    this.range = range;
  }

  @Override
  public void validate() throws IllegalArgumentException {
    // TODO Auto-generated method stub

  }

  @Override
  public Object getValue() {
    return this.value;
  }

  @Override
  public boolean isValid(Object value) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void setValue(Object value) throws IllegalArgumentException {
    this.value = (List<String>) value;
  }

  public List<String> getRange() {
    return range;
  }

  public void setRange(List<String> range) {
    this.range = range;
  }

  @Override
  public void addToXML() {
    // TODO Auto-generated method stub

  }

  @Override
  public void addToJSON(String name, String json) {
    // TODO Auto-generated method stub

  }

}
