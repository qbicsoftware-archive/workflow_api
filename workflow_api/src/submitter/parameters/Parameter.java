package submitter.parameters;

public abstract class Parameter { // TODO subclasses for int, float, file...
  private String title;
  private String description;
  private boolean advanced;
  private boolean required;

  public Parameter(String title, String description, boolean advanced, boolean required) {
    this.title = title;
    this.description = description;
    this.advanced = advanced;
    this.required = required;
  }

  public abstract void validate() throws IllegalArgumentException;

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public boolean isAdvanced() {
    return advanced;
  }

  public boolean isRequired() {
    return required;
  }

  public abstract Object getValue();

  public abstract boolean isValid(Object value);

  public abstract void setValue(Object value) throws IllegalArgumentException;

  public abstract void addToXML();

  public abstract void addToJSON(String name, String json); // TODO json
  // should be a
  // writer
  // instance?
}
