package com.madhackerdesigns.jinder.test;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.After;
import org.junit.Test;

import com.google.api.client.http.HttpResponseException;
import com.madhackerdesigns.jinder.Connection;
import com.madhackerdesigns.jinder.models.Self;
import com.madhackerdesigns.jinder.test.helpers.Fixture;
import com.madhackerdesigns.jinder.test.helpers.MockTransport;

public class ConnectionTest {
  
  @After
  public void clearConnection() {
    Connection.clearConnection();
  }

  @Test(expected = HttpResponseException.class)
  public void raisesExceptionWithBadCredentials() throws IOException {
    Connection connection = new Connection("test", "foo");
    connection.setHttpTransport(new MockTransport(401, "Unauthorized"));
    connection.get("/rooms.json");
  }
  
  @Test(expected = HttpResponseException.class)
  public void raisesExceptionWhenInvalidSubdomainSpecified() throws IOException {
    Connection connection = new Connection("test", "foo");
    connection.setHttpTransport(new MockTransport(404, "Not found"));
    connection.get("/rooms.json");
  }
  
  @Test
  public void looksUpTokenWhenUsernameAndPasswordProvided() throws IOException {
    Connection connection = new Connection("test", "user", "pass");
    connection.setHttpTransport(new MockTransport(200, new Fixture("me.json").read()));
    String token = connection.get("/users/me.json").parseAs(Self.class).user.api_auth_token;
    assertEquals("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx", token);
  }
  
}
