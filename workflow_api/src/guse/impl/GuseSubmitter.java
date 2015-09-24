package guse.impl;

import guse.remoteapi.GuseRemoteApi;
import guse.workflowrepresentation.GuseWorkflowRepresentation;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import logging.Log4j2Logger;

import org.apache.commons.lang.NotImplementedException;

import parsers.WFConfigReader;
import submitter.SubmitFailedException;
import submitter.Submitter;
import submitter.Workflow;

import com.genericworkflownodes.knime.config.reader.InvalidCTDFileException;
import com.vaadin.data.util.BeanItemContainer;

public class GuseSubmitter implements Submitter {
  private logging.Logger LOGGER = new Log4j2Logger(GuseSubmitter.class);
  private GuseRemoteApi guseRemoteApi;
  private GuseWorkflowFileSystem guseWorkflowFileSystem;
  private File pathToWfConfig;

  public GuseSubmitter(GuseRemoteApi gra, GuseWorkflowFileSystem guseWorkflowFileSystem,
      File pathToWfConfig) {
    this.guseRemoteApi = gra;
    this.guseWorkflowFileSystem = guseWorkflowFileSystem;
    this.pathToWfConfig = pathToWfConfig;
  }

  @Override
  public String submit(Workflow workflow, String foreignID, String user)
      throws IllegalArgumentException, SubmitFailedException, ConnectException {
    if (!(guseRemoteApi.checkHostAvailable() && guseWorkflowFileSystem.isAvailable()))
      throw new ConnectException("is guse connected and initialized (" + guseRemoteApi.getHost()
          + ")? Are guse workflows available on the file system? ("
          + guseWorkflowFileSystem.getPathToMainWorkflowFolder() + ")");

    File tmpguseWorkflow = null;
    try {
      tmpguseWorkflow =
          guseWorkflowFileSystem.GuseWorkflowRepresentationToGuseConverter(
              (GuseWorkflowRepresentation) workflow, foreignID, user);
    } catch (IOException | InvalidCTDFileException e) {
      throw new SubmitFailedException(e.getMessage());
    }
    String runId = guseRemoteApi.submit(tmpguseWorkflow, guseWorkflowFileSystem.getCertificate());
    if (runId.isEmpty() || runId.equals("FALSE")) {
      throw new SubmitFailedException(
          "Submission failed.\nGuse returned: "
              + runId
              + "\nCheck guse logs for more information.\nHint: if no return code avialable, then guse has to be initialized probably.\n tmp guse Folder is: "
              + tmpguseWorkflow.getAbsolutePath());

    }

    if (tmpguseWorkflow != null && tmpguseWorkflow.exists()) {
      guseWorkflowFileSystem.delete(Paths.get(tmpguseWorkflow.getAbsolutePath()));
    }

    return runId;
  }

  @Override
  public Status status(String id) throws NoSuchElementException, ConnectException {
    if (!guseRemoteApi.checkHostAvailable()) {
      throw new ConnectException();
    }

    String status = guseRemoteApi.info(id);

    if (status.equals("FAILED") || status.equals("not valid data"))
      throw new NoSuchElementException("status is:" + status);

    switch (status) {
      case "error":
      case "suspended":
        return Status.FAILED;
      case "finished":
        return Status.FINISHED;
      case "submitted":
        return Status.SUBMITTED;
      case "running":
        return Status.RUNNING;
      default:
        assert false;
        return Status.FAILED;
    }
  }

  @Override
  public void abort(String id) throws NoSuchElementException, ConnectException,
      IllegalStateException {
    // TODO Auto-generated method stub
    throw new NotImplementedException();
  }

  @Override
  public Set<Workflow> available(int page, String sortKey) throws IllegalArgumentException,
      IOException {
    if (!guseWorkflowFileSystem.isAvailable()) {
      throw new ConnectException(
          "guse workflows not available. Check whether certificate is in the right place and guse workflow directory is set correctly.");
    }
    return guseWorkflowFileSystem.getWorkflows();
    //TODO this was never implemented. is it needed at some point?
//    String[] sortedkeys = sortKey.split("/");
//    if (sortedkeys.length > 1 && sortedkeys[1].equals("admin-origin")) {
//      return guseWorkflowFileSystem.getWorkflows(sortedkeys[0]);
//    } else {
//      throw new IllegalArgumentException("unkown sort key: " + sortKey
//          + "possible sortkeys are: admin-origin");
//    }

  }

  @Override
  public int numAvailablePages() throws ConnectException {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public BeanItemContainer<Workflow> getAvailableSuitableWorkflows(String fileType)
      throws Exception {
    BeanItemContainer<Workflow> suitableWorkflows = new BeanItemContainer<Workflow>(Workflow.class);
    // Should that work ?
    File[] directoryListing = pathToWfConfig.listFiles();

    if (directoryListing != null) {
      for (File child : directoryListing) {
        WFConfigReader wfreader = new WFConfigReader();
        Workflow chosenWorkflow;
        chosenWorkflow = wfreader.read(child.getAbsoluteFile());

        if (chosenWorkflow.getSampleType().equals(fileType)) {
          suitableWorkflows.addBean(chosenWorkflow);
        }
      }
    }

    return suitableWorkflows;
  }

  @Override
  public BeanItemContainer<Workflow> getAvailableSuitableWorkflows(List<String> fileTypes)
      throws Exception {
    for(String type: fileTypes){
    }
    BeanItemContainer<Workflow> suitableWorkflows = new BeanItemContainer<Workflow>(Workflow.class);
    // Should that work ?
    File[] directoryListing = pathToWfConfig.listFiles();

    if (directoryListing != null) {
      for (File child : directoryListing) {
        WFConfigReader wfreader = new WFConfigReader();
        Workflow chosenWorkflow = null;
        chosenWorkflow = wfreader.read(child.getAbsoluteFile());
        List<String> workflowFileTypes = chosenWorkflow.getFileTypes();
       
        
        for(String type: workflowFileTypes){
          if(fileTypes.contains(type)){
            suitableWorkflows.addBean(chosenWorkflow);
            break;
          }
        }
//        if (fileTypes.containsAll(workflowFileTypes)) {
//          suitableWorkflows.addBean(chosenWorkflow);
//        }

      }
    }

    return suitableWorkflows;

  }
  
  @Override
  public BeanItemContainer<Workflow> getWorkflowsByExperimentType(String experimentType)
      throws Exception {

    BeanItemContainer<Workflow> suitableWorkflows = new BeanItemContainer<Workflow>(Workflow.class);
    File[] directoryListing = pathToWfConfig.listFiles();

    if (directoryListing != null) {
      for (File child : directoryListing) {
        WFConfigReader wfreader = new WFConfigReader();
        Workflow chosenWorkflow = null;
        chosenWorkflow = wfreader.read(child.getAbsoluteFile());

        String wfExperimentType = chosenWorkflow.getExperimentType();

        if (wfExperimentType.equals(experimentType)) {
          suitableWorkflows.addBean(chosenWorkflow);
        }
      }
    }

    return suitableWorkflows;
  }
  
}
