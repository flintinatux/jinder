package com.madhackerdesigns.jinder.helpers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.api.client.http.LowLevelHttpRequest;
import com.google.api.client.http.LowLevelHttpResponse;
import com.google.api.client.testing.http.MockHttpTransport;
import com.google.api.client.testing.http.MockLowLevelHttpRequest;
import com.google.api.client.testing.http.MockLowLevelHttpResponse;

public class MockTransport extends MockHttpTransport {
  
  private static Logger logger = Logger.getAnonymousLogger();

  private HashMap<String, Response> getResponses = new HashMap<String, Response>();
  private HashMap<String, Response> postResponses = new HashMap<String, Response>();
  private HashMap<String, Response> putResponses = new HashMap<String, Response>();
  private HashMap<String, Response> deleteResponses = new HashMap<String, Response>();
  private String requestMethod;
  private String requestPath;
  private boolean logging = false;
  
  // constructors
  
  public MockTransport() { }

  public MockTransport(String method, String expectedPath, int statusCode, String content) {
    super();
    addResponse(method, expectedPath, statusCode, content);
  }
  
  // public methods
  
  public void addResponse(String method, String expectedPath, int statusCode, String content) {
    responsesFor(method).put(expectedPath, new Response(statusCode, content));
  }
  
  public void disableLogging() { 
    logging = false;
  }
  
  public void enableLogging() {
    logging = true;
  }
  
  // protected methods
  
  @Override
  protected LowLevelHttpRequest buildRequest(String method, String url) throws IOException {
    requestMethod = method;
    requestPath = pathFor(url); 
    if (logging) { logger.log(Level.INFO, "Requested path: " + requestPath); }
    
    return new MockLowLevelHttpRequest(url) {

      @Override
      public LowLevelHttpResponse execute() throws IOException {
        MockLowLevelHttpResponse response = new MockLowLevelHttpResponse();
        response.setStatusCode(statusCode());
        response.setContent(content());
        if (logging) { logger.log(Level.INFO, "Response: " + statusCode() + ", " + content()); }
        return response;
      }
      
      private int statusCode() {
        return generateResponse().statusCode;
      }

      private String content() {
        return generateResponse().content;
      }
      
    };
  }
  
  // private methods
  
  private Response defaultResponse() {
    return new Response(400, "");
  }
  
  private Response generateResponse() {
    if (pathAvailableForMethod()) {
      return responseOfPathForMethod();
    }
    return defaultResponse();
  }
  
  private boolean pathAvailableForMethod() {
    return responsesFor(requestMethod).containsKey(requestPath);
  }

  private String pathFor(String url) throws MalformedURLException {
    URL parsedUrl = new URL(url);
    URI uri = null;
    try {
      uri = new URI(parsedUrl.getProtocol(), parsedUrl.getUserInfo(), parsedUrl.getHost(), parsedUrl.getPort(), parsedUrl.getPath(), parsedUrl.getQuery(), parsedUrl.getRef());
    } catch (URISyntaxException e) {
      e.printStackTrace();
      return null;
    }
    String path = uri.getPath(); 
    if (uri.getQuery() != null) {
      path += "?" + uri.getQuery();
    }
    if (uri.getFragment() != null) {
      path += "#" + uri.getFragment();
    }
    return path;
  }
  
  private Response responseOfPathForMethod() {
    return responsesFor(requestMethod).get(requestPath);
  }
  
  private HashMap<String, Response> responsesFor(String method) {
    if ("GET".equals(method))    { return getResponses; }
    if ("POST".equals(method))   { return postResponses; }
    if ("PUT".equals(method))    { return putResponses; }
    if ("DELETE".equals(method)) { return deleteResponses; }
    return null;
  }
  
  // internal classes
  
  private class Response {
    int statusCode;
    String content;
    
    Response(int statusCode, String content) {
      this.statusCode = statusCode;
      this.content = content;
    }
  }

}
