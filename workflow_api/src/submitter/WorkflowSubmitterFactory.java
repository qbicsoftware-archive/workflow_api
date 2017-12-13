package submitter;

import java.io.File;

import guse.impl.GuseSubmitter;
import guse.impl.GuseWorkflowFileSystem;
import guse.remoteapi.GuseRemoteApi;
import life.qbic.portal.liferayandvaadinhelpers.main.ConfigurationManager;

public class WorkflowSubmitterFactory {

  public static enum Type {
    guseSubmitter, snakemakeSubmitter
  }

  public static Submitter getSubmitter(Type submitter, ConfigurationManager manager)
      throws Exception {
    switch (submitter) {
      case guseSubmitter:
        GuseRemoteApi gra = new GuseRemoteApi();
        gra.setHost(manager.getGuseRemoteApiUrl());
        gra.setPASSWORD(manager.getGuseRemoteApiPass());

        GuseWorkflowFileSystem gwfs = new GuseWorkflowFileSystem(
            new File(manager.getPathToGuseWorkflows()),
            new File(manager.getPathToGuseCertificate()), new File(manager.getPathToDropboxes()));
        return new GuseSubmitter(gra, gwfs, new File(manager.getPathToWFConfig()));
      case snakemakeSubmitter:
        return null;
      default:
        assert false;
        return null;
    }
  };
}
