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

  private HashMap<String, Response> responses = new HashMap<String, Response>();
  
  // constructors
  
  public MockTransport() { }

  public MockTransport(String expectedPath, int statusCode, String content) {
    super();
    addResponse(expectedPath, statusCode, content);
  }
  
  // public methods
  
  public void addResponse(String expectedPath, int statusCode, String content) {
    responses.put(expectedPath, new Response(statusCode, content));
  }
  
  // protected methods
  
  @Override
  protected LowLevelHttpRequest buildRequest(String method, String url) throws IOException {
    return new MockLowLevelHttpRequest(url) {

      @Override
      public LowLevelHttpResponse execute() throws IOException {
        MockLowLevelHttpResponse response = new MockLowLevelHttpResponse();
        response.setStatusCode(statusCode());
        response.setContent(content());
        return response;
      }
      
      private int statusCode() {
        if (responses.containsKey(path())) {
          return responses.get(path()).statusCode;
        }
        return 400;
      }

      private String content() {
        if (responses.containsKey(path())) {
          return responses.get(path()).content;
        }
        return "";
      }

      private String path() {
        return new GenericUrl(getUrl()).getRawPath();
      }
      
    };
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
