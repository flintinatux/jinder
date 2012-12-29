package com.madhackerdesigns.jinder;

import java.net.URI;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;

public class Connection {

  private static final String HOST = "campfirenow.com";
  private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
  
  private JsonFactory jsonFactory;
  private String subdomain;
  private URI uri;
  private ConnectionOptions options;
  private String token;
  
  public Connection(String subdomain) {
    setupConnection(subdomain, new ConnectionOptions());
  }

  public Connection(String subdomain, ConnectionOptions options) {
    setupConnection(subdomain, options);
  }
  
  public void setJsonFactory(JsonFactory jsonFactory) {
    this.jsonFactory = jsonFactory;
  }
  
  private void setupConnection(String subdomain, ConnectionOptions options) {
    this.subdomain = subdomain;
    this.options = options;
    this.uri = URI.create((options.ssl ? "https" : "http") + "://" + subdomain + "." + HOST);
    this.token = options.token;
  }

  public class ConnectionOptions {
    boolean ssl = true;
    boolean sslVerify = true;
    String proxy;
    String token;
  }
}
