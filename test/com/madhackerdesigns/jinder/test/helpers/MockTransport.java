package com.madhackerdesigns.jinder.test.helpers;

import java.io.IOException;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.LowLevelHttpRequest;
import com.google.api.client.http.LowLevelHttpResponse;
import com.google.api.client.testing.http.MockHttpTransport;
import com.google.api.client.testing.http.MockLowLevelHttpRequest;
import com.google.api.client.testing.http.MockLowLevelHttpResponse;

public class MockTransport extends MockHttpTransport {

  private int statusCode;
  private String content;
  private String expectedPath;
  
  public MockTransport(int statusCode, String content) {
    super();
    this.statusCode = statusCode;
    this.content = content;
  }
  
  public MockTransport(int statusCode, String content, String expectedPath) {
    super();
    this.statusCode = statusCode;
    this.content = content;
    this.expectedPath = expectedPath;
  }
  
  @Override
  protected LowLevelHttpRequest buildRequest(String method, String url) throws IOException {
    return new MockLowLevelHttpRequest(url) {

      @Override
      public LowLevelHttpResponse execute() throws IOException {
        MockLowLevelHttpResponse response = new MockLowLevelHttpResponse();
        response.setStatusCode(statusCode);
        response.setContent(content());
        return response;
      }
      
      private String content() {
        String givenPath = new GenericUrl(getUrl()).getRawPath();
        return (expectedPath == null || expectedPath.equals(givenPath)) ? content : "";
      }
      
    };
  }

}
