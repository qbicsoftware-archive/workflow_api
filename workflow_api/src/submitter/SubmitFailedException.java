package submitter;

public class SubmitFailedException extends Exception {
  private static final long serialVersionUID = -1664420836866215381L;

  public SubmitFailedException(String message) {
    super(message);
  }

  public SubmitFailedException() {
    super();
  }
}
