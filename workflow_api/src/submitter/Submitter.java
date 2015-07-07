package submitter;

import java.io.IOException;
import java.net.ConnectException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import com.vaadin.data.util.BeanItemContainer;

/**
 * Submit workflows for execution and query status of workflow runs.
 * 
 * {@code available()} can be used to query for workflows that can be executed. The returned {@see
 * Workflow} instance can then be modified and executed with {@code submit(Workflow)}. This returns
 * an id that can be used to query the status or abort the run.
 * 
 * Example usage:
 * 
 * <pre>
 * {@code
 *     workflows = submitter.available();
 *     if (workflows.length() == 0) {
 *         throw RuntimeError();     
 *     }
 *     workflow = workflows[0];
 *     
 *     // Configure parameters and input files
 *     workflow.parameter().getParam('inputFile').setValue("/hi");
 *     
 *     try {
 *         workflow.validate();
 *     } catch (InvalidArumentException e) {
 *         throw e;
 *     }
 *     
 *     // Optionally register the workflow in another system 
 *     foreignID = register(workflow);
 * 
 *     String id = submitter.submit(workflow, foreignID, "username");
 *     
 *     // query the status at some later time
 *     String status = submitter.status(id);
 * }
 * </pre>
 * 
 */
public interface Submitter {
  public enum Status {
    SUBMITTED, RUNNING, FINISHED, FAILED
  }

  /**
   * Submit a configured workflow to a workflow engine for execution.
   * 
   * 
   * @throws IllegalArgumentException if required parameters of the workflow are not set.
   * @throws SubmitFailedException
   * @throws RuntimeException
   * @param workflow The workflow that should be executed.
   * @param foreignID An optional id for this workflow run from another database. This can be null.
   * @param user Execute the workflow as the specified user. // TODO String??
   * @return An id that uniquely identifies the workflow run.
   */
  public String submit(Workflow workflow, String foreignID, String user)
      throws IllegalArgumentException, SubmitFailedException, ConnectException;

  /**
   * Query the status of a workflow run.
   * 
   * @param id The id of the workflow (not the foreign id, but the return value of submit)
   * @throws NoSuchElementException If the id could not be found.
   * @throws ConnectException If the submitter could not connect to the workflow engine.
   */
  public Status status(String id) throws NoSuchElementException, ConnectException;

  /**
   * Abort a currently running workflow run.
   * 
   * TODO: Add user as argument?
   * 
   * @param id
   * @throws NoSuchElementException
   * @throws ConnectException
   * @throws IllegalStateException If the workflow run is currently not running e.g. if it is
   *         finished or if it failed.
   */
  public void abort(String id) throws NoSuchElementException, ConnectException,
      IllegalStateException;

  /**
   * Return a subset of available workflows.
   * 
   * See {@code numAvailablePages}.
   * 
   * @param Return only a part (a page) of the available workflows. If you want all workflows, set
   *        to -1
   * @param sortKey Return workflows sorted by sortKey.
   * @return
   * @throws ConnectException
   * @throws IllegalArgumentException if {@code page} is larger than the number of available pages
   *         or if the sortKey is unknown.
   * @throws IOException
   * 
   */
  public Set<Workflow> available(int page, String sortKey) throws ConnectException,
      IllegalArgumentException, IOException;

  /**
   * Return the number of pages with available workflows.
   * 
   * See {@link available}.
   * 
   * @return
   * @throws ConnectException
   */
  public int numAvailablePages() throws ConnectException;

  public BeanItemContainer<Workflow> getAvailableSuitableWorkflows(String fileType)
      throws Exception;

  public BeanItemContainer<Workflow> getAvailableSuitableWorkflows(List<String> fileTypes)
      throws Exception;
  
  public BeanItemContainer<Workflow> getWorkflowsByExperimentType(String experimentType)
      throws Exception;

}
