package com.madhackerdesigns.jinder.test.helpers;

import java.io.IOException;
import java.util.HashMap;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.LowLevelHttpRequest;
import com.google.api.client.http.LowLevelHttpResponse;
import com.google.api.client.testing.http.MockHttpTransport;
import com.google.api.client.testing.http.MockLowLevelHttpRequest;
import com.google.api.client.testing.http.MockLowLevelHttpResponse;

public class MockTransport extends MockHttpTransport {

  private HashMap<String, Response> getResponses = new HashMap<String, Response>();
  private HashMap<String, Response> postResponses = new HashMap<String, Response>();
  private HashMap<String, Response> putResponses = new HashMap<String, Response>();
  private HashMap<String, Response> deleteResponses = new HashMap<String, Response>();
  private String request_method;
  private String request_path;
  
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
  
  // protected methods
  
  @Override
  protected LowLevelHttpRequest buildRequest(String method, String url) throws IOException {
    this.request_method = method;
    this.request_path = new GenericUrl(url).getRawPath();
    
    return new MockLowLevelHttpRequest(url) {

      @Override
      public LowLevelHttpResponse execute() throws IOException {
        MockLowLevelHttpResponse response = new MockLowLevelHttpResponse();
        response.setStatusCode(statusCode());
        response.setContent(content());
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
    return responsesFor(request_method).containsKey(request_path);
  }
  
  private Response responseOfPathForMethod() {
    return responsesFor(request_method).get(request_path);
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
