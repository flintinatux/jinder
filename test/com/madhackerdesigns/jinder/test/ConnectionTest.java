package com.madhackerdesigns.jinder.test;

import static com.madhackerdesigns.jinder.test.helpers.Fixture.fixture;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.After;
import org.junit.Test;

import com.google.api.client.http.HttpResponseException;
import com.madhackerdesigns.jinder.Connection;
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
    connection.setHttpTransport(new MockTransport(200, fixture("me.json"), "/users/me.json"));
    assertEquals("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx", connection.token());
  }
  
  @Test
  public void usesBasicAuthForCredentials() throws IOException {
    Connection connection = new Connection("test", "mytoken");
    connection.setHttpTransport(new MockTransport(200, fixture("rooms.json"), "/rooms.json"));
    connection.get("/rooms.json");
  }
  
  @Test
  public void turnsOnSslByDefault() throws IOException {
    Connection connection = new Connection("test", "user", "pass");
    connection.setHttpTransport(new MockTransport(200, fixture("me.json")));
    assertTrue(connection.ssl());
  }
  
}