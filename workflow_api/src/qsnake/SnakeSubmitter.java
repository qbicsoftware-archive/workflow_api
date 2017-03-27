package qsnake;

import java.io.IOException;
import java.net.ConnectException;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.ParseException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import submitter.SubmitFailedException;
import submitter.Submitter;
import submitter.Workflow;

import com.google.common.annotations.VisibleForTesting;
import com.vaadin.data.util.BeanItemContainer;

public class SnakeSubmitter implements Submitter {
  HttpHost qsnakeService;
  CloseableHttpClient httpClient;
  String user;
  String password;

  public SnakeSubmitter(HttpHost qsnakeService, String user, String password) {
    this.qsnakeService = qsnakeService;
    this.user = user;
    this.password = password;

    CredentialsProvider credsProvider = new BasicCredentialsProvider();
    credsProvider.setCredentials(
        new AuthScope(qsnakeService.getHostName(), qsnakeService.getPort()),
        new UsernamePasswordCredentials(user, password));

    httpClient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
  }

  @VisibleForTesting
  JSONObject sendPOST(String query, JSONObject data) throws IOException, RequestFailedException,
      ParseException, JSONException {
    HttpPost request = new HttpPost(this.qsnakeService.toURI() + "/" + query);
    request.addHeader("Accept", "application/json");
    StringEntity entity = new StringEntity(data.toString());
    // entity.setContentType("application/json");
    request.setHeader("Content-Type", "application/json");
    request.setEntity(entity);
    CloseableHttpResponse response = httpClient.execute(request);

    int status = response.getStatusLine().getStatusCode();
    if (status >= 200 & status < 300) {
      HttpEntity responseEntity = response.getEntity();
      return new JSONObject(EntityUtils.toString(responseEntity));
    } else {
      HttpEntity responseEntity = response.getEntity();
      throw new RequestFailedException(request.toString() + "\n"
          + EntityUtils.toString(responseEntity));
    }
  }

  @VisibleForTesting
  JSONObject sendGet(String query) throws IOException, RequestFailedException, ParseException,
      JSONException {
    HttpGet request = new HttpGet(this.qsnakeService.toURI() + "/" + query);
    request.addHeader("Accept", "application/json");
    CloseableHttpResponse response = httpClient.execute(request);

    int status = response.getStatusLine().getStatusCode();
    if (status >= 200 & status < 300) {
      HttpEntity entity = response.getEntity();
      return new JSONObject(EntityUtils.toString(entity));
    } else {
      HttpEntity entity = response.getEntity();
      throw new RequestFailedException(EntityUtils.toString(entity));
    }
  }

  @Override
  public String submit(Workflow workflow, String foreignID, String user)
      throws IllegalArgumentException, SubmitFailedException, ConnectException {
    String workflowID = workflow.getID();
    JSONObject request = new JSONObject();
    try {
      request.put("user", user);
      request.put("barcode", foreignID);
      request.put("workflow", workflowID);
      request.put("data", workflow.getData().asJSON());
      request.put("parameters", workflow.getParameters().asJSON());
      request.put("submit_on_POST", true);
    } catch (JSONException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    JSONObject response;
    try {
      response = sendPOST("/jobs", request);
    } catch (ParseException | IOException | RequestFailedException | JSONException e) {
      e.printStackTrace();
      throw new SubmitFailedException();
    }
    try {
      return response.getString("_id");
    } catch (JSONException e) {
      e.printStackTrace();
      throw new SubmitFailedException();
    }
  }

  @Override
  public Status status(String id) throws NoSuchElementException, ConnectException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void abort(String id) throws NoSuchElementException, ConnectException,
      IllegalStateException {
    throw new NoSuchElementException(); // TODO
  }

  @Override
  public Set<Workflow> available(int page, String sortKey) throws ConnectException,
      IllegalArgumentException {
    JSONObject response;
    try {
      response = sendGet("/workflows?page=" + page);
    } catch (ParseException | IOException | RequestFailedException | JSONException e) {
      e.printStackTrace();
      throw new ConnectException();
    }
    Set<Workflow> workflows = new HashSet<Workflow>();
    QSnakeWorkflowFactory factory = new QSnakeWorkflowFactory();
    JSONArray json_workflows;
    try {
      json_workflows = response.getJSONArray("_items");
      for (int i = 0; i < json_workflows.length(); i++) {
        workflows.add(factory.fromJSON(json_workflows.getJSONObject(i)));
      }
    } catch (JSONException e) {
      e.printStackTrace();
      throw new ConnectException();
    }
    return workflows;
  }

  @Override
  public int numAvailablePages() throws ConnectException {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public BeanItemContainer<Workflow> getAvailableSuitableWorkflows(String fileType)
      throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public BeanItemContainer<Workflow> getAvailableSuitableWorkflows(List<String> fileTypes)
      throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public BeanItemContainer<Workflow> getWorkflowsByExperimentType(String experimentType)
      throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

}
