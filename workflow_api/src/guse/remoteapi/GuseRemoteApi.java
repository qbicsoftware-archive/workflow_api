package guse.remoteapi;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import logging.Log4j2Logger;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

public class GuseRemoteApi {

  private logging.Logger LOGGER = new Log4j2Logger(GuseRemoteApi.class);

  private String host = null;
  private String PASSWORD = null;

  public String getPASSWORD() {
    return PASSWORD;
  }

  public void setPASSWORD(String pASSWORD) {
    PASSWORD = pASSWORD;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  /**
   * checks whether the host is available. returns true if a connection can be established == the
   * response code is 200 and guse returns something non-empty
   */
  public boolean checkHostAvailable() {
    HttpURLConnection connection = null;
    URL myurl;
    try {
      myurl = new URL(host);
      connection = (HttpURLConnection) myurl.openConnection();
      // Set request to header to reduce load as Subirkumarsao said.
      connection.setRequestMethod("HEAD");
      int code = connection.getResponseCode();
      if (code == 200 && connection.getContentLength() > 0)
        return true;
      LOGGER.debug("" + code + " content length " + connection.getContentLength());
    } catch (MalformedURLException e) {
      LOGGER.error("MalformedURLException: " + host, e.getStackTrace());
    } catch (IOException e) {
      LOGGER.error("IOExcpetion: Check url " + host + ", is the server available?",
          e.getStackTrace());
    }
    return false;
  }

  /**
   * submit a workflow to the remote api for the given host. Note: portmapping has the following
   * format: inputfilename=WFname/Jobname=PortNumber exename=WFname/Jobname
   * 
   * @param workflowDescription standrad gUSE workflow description XML file
   * @param inputFile Zip file taht contains input files and executables
   * @param portmappingAndExecutableMapping Text file which contains key - value pairs separated
   *        with new line
   * @param certificates Zip file that contains files/certificates to authenticate to the cluster
   * @return FALSE if the submission failed, or ID of the workflow instance.
   */
  public String submit(File workflowDescription, File inputFile,
      File portmappingAndExecutableMapping, File certificates) {
    HashMap<String, ContentBody> parameters = new HashMap<String, ContentBody>();
    FileBody wfdesc = new FileBody(workflowDescription);
    FileBody inputzip = new FileBody(inputFile);
    FileBody portmapping = new FileBody(portmappingAndExecutableMapping);
    FileBody certs = new FileBody(certificates);

    StringBody pass = new StringBody(PASSWORD, ContentType.TEXT_PLAIN);
    StringBody m = new StringBody("submit", ContentType.TEXT_PLAIN);
    parameters.put("wfdesc", wfdesc);
    parameters.put("inputzip", inputzip);
    parameters.put("portmapping", portmapping);
    parameters.put("certs", certs);

    parameters.put("pass", pass);
    parameters.put("m", m);

    return executePost(host, parameters).trim();
  }

  public String submit(File zipfile, File certificates) {
    HashMap<String, ContentBody> parameters = new HashMap<String, ContentBody>();
    FileBody guseWorkflow = new FileBody(zipfile);
    FileBody certs = new FileBody(certificates);

    StringBody pass = new StringBody(PASSWORD, ContentType.TEXT_PLAIN);
    StringBody m = new StringBody("submit", ContentType.TEXT_PLAIN);
    parameters.put("gusewf", guseWorkflow);
    parameters.put("certs", certs);

    parameters.put("pass", pass);
    parameters.put("m", m);

    return executePost(host, parameters).trim();
  }



  /**
   * Returns the workflow status for a given id, or FALSE if id is wrong.
   * 
   * @param Id
   * @return
   */
  public String info(String Id) {
    HashMap<String, ContentBody> parameters = new HashMap<String, ContentBody>();
    StringBody pass = new StringBody(PASSWORD, ContentType.TEXT_PLAIN);
    StringBody m = new StringBody("info", ContentType.TEXT_PLAIN);
    StringBody ID = new StringBody(Id, ContentType.TEXT_PLAIN);

    parameters.put("pass", pass);
    parameters.put("m", m);
    parameters.put("ID", ID);
    return executePost(host, parameters).trim();
  }

  /**
   * Returns the workflow status and job status information for a given id, or FALSE if id is wrong
   * or a guse error occured. the format of the returned String is in the following format:
   * WFstatus;Job1name:status;Job2name:status; e.g.
   * running;Job1:init0:running=1:finished=1:error=0;Job2:init=0:running=1:finished=0:error=0; Note:
   * WFstatus is the same as the output of {@link info}
   * 
   * @param Id
   * @return
   */
  public String detailedInfo(String Id) {
    HashMap<String, ContentBody> parameters = new HashMap<String, ContentBody>();
    StringBody pass = new StringBody(PASSWORD, ContentType.TEXT_PLAIN);
    StringBody m = new StringBody("detailsinfo", ContentType.TEXT_PLAIN);
    StringBody ID = new StringBody(Id, ContentType.TEXT_PLAIN);

    parameters.put("pass", pass);
    parameters.put("m", m);
    parameters.put("ID", ID);

    return executePost(host, parameters).trim();
  }



  // Thats the one that is working!
  public String executePost(String targetURL, Map<String, ContentBody> parameters) {
    URL url;
    String connectionResponse = null;
    try {
      url = new URL(targetURL);

      HttpURLConnection connection;
      connection = (HttpURLConnection) url.openConnection();
      connection.setDoOutput(true);
      connection.setRequestMethod("POST");

      MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.STRICT);

      for (Entry<String, ContentBody> e : parameters.entrySet()) {
        multipartEntity.addPart(e.getKey(), e.getValue());
      }

      connection.setRequestProperty("Content-Type", multipartEntity.getContentType().getValue());
      OutputStream out = connection.getOutputStream();
      try {
        multipartEntity.writeTo(out);
      } finally {
        out.close();
      }
      // int status = connection.getResponseCode();
      // Get Response
      InputStream is = connection.getInputStream();
      BufferedReader rd = new BufferedReader(new InputStreamReader(is));
      String line;
      StringBuffer response = new StringBuffer();
      while ((line = rd.readLine()) != null) {
        response.append(line);
        response.append('\r');
      }
      rd.close();
      connectionResponse = response.toString();
    } catch (MalformedURLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return connectionResponse;
  }

}
