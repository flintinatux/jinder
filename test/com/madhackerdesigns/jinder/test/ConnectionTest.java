package com.madhackerdesigns.jinder.test;

import java.io.IOException;

import org.junit.Test;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.LowLevelHttpRequest;
import com.google.api.client.http.LowLevelHttpResponse;
import com.google.api.client.testing.http.MockHttpTransport;
import com.google.api.client.testing.http.MockLowLevelHttpRequest;
import com.google.api.client.testing.http.MockLowLevelHttpResponse;
import com.madhackerdesigns.jinder.Connection;
import com.madhackerdesigns.jinder.ConnectionOptions;

public class ConnectionTest {

  @Test
  public void raisesExceptionWithBadCredentials() throws IOException {
    ConnectionOptions options = new ConnectionOptions();
    options.token = "token";
    Connection connection = new Connection("test", options, unauthorizedTransport());
    connection.get("/rooms.json");
  }

  private HttpTransport unauthorizedTransport() {
    return new MockHttpTransport() {

      @Override
      protected LowLevelHttpRequest buildRequest(String method, String url) throws IOException {
        return new MockLowLevelHttpRequest(url) {

          @Override
          public LowLevelHttpResponse execute() throws IOException {
            MockLowLevelHttpResponse response = new MockLowLevelHttpResponse();
            response.setStatusCode(401);
            response.setContent("Unauthorized");
            return response;
          }
          
        };
      }
      
    };
  }
  
}
