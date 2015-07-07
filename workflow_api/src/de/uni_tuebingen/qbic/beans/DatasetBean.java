package de.uni_tuebingen.qbic.beans;

import java.io.Serializable;

public class DatasetBean implements Serializable {

  private static final long serialVersionUID = 5009508340296905542L;
  
  private String fileName;
  private String fileType;
  private String openbisCode;
  private String fullPath;
  private String sampleIdentifier;
  public DatasetBean(String fileName, String fileType, String openbisCode, String fullPath,
      String sampleIdentifier) {
    super();
    this.fileName = fileName;
    this.fileType = fileType;
    this.openbisCode = openbisCode;
    this.setSampleIdentifier(sampleIdentifier);
    this.setFullPath(fullPath);
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public String getFileType() {
    return fileType;
  }

  public void setFileType(String fileType) {
    this.fileType = fileType;
  }

  public String getOpenbisCode() {
    return openbisCode;
  }

  public void setOpenbisCode(String openbisCode) {
    this.openbisCode = openbisCode;
  }

  public String getFullPath() {
    return fullPath;
  }

  public void setFullPath(String fullPath) {
    this.fullPath = fullPath;
  }

  public String getSampleIdentifier() {
    return sampleIdentifier;
  }

  public void setSampleIdentifier(String sampleIdentifier) {
    this.sampleIdentifier = sampleIdentifier;
  }

}
