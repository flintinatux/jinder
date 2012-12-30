package com.madhackerdesigns.jinder;

import java.io.IOException;
import java.net.URI;

import com.google.api.client.http.BasicAuthentication;
import com.google.api.client.http.ExponentialBackOffPolicy;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpExecuteInterceptor;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.sun.jndi.toolkit.url.Uri;

public class Connection {

  private static final String HOST = "campfirenow.com";
  private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
  
  private static HttpRequestFactory connection;
  
  private String subdomain;
  private String uri;
  private ConnectionOptions options;
  private String token;
  
  public Connection(String subdomain) {
    buildConnection(subdomain, new ConnectionOptions());
  }

  public Connection(String subdomain, ConnectionOptions options) {
    buildConnection(subdomain, options);
  }
  
  public String token() throws IOException {
    if (token == null) {
      GenericUrl meUrl = new GenericUrl(uri + "/users/me.json");
      HttpRequest request = connection().buildGetRequest(meUrl).setInterceptor(tokenAuth());
      HttpResponse response = request.execute();
      // TODO: continue here
    }
    return token;
  }

  public boolean usingSSL() {
	  return options.ssl;
  }
  
  // private methods
  
  private HttpRequestFactory connection() {
    if (connection == null) {
      connection = HTTP_TRANSPORT.createRequestFactory(setConnectionOptions());
    }
    return connection;
  }
  
  private void buildConnection(String subdomain, ConnectionOptions options) {
    this.subdomain = subdomain;
    this.options = options;
    this.uri = (options.ssl ? "https" : "http") + "://" + subdomain + "." + HOST;
    this.token = options.token;
    
    connection();
  }
  
  private HttpRequestInitializer setConnectionOptions() {
    return new HttpRequestInitializer() {
      
      @Override
      public void initialize(HttpRequest request) throws IOException {
        request.setBackOffPolicy(new ExponentialBackOffPolicy());
        request.setParser(new JsonObjectParser(options.jsonFactory));
      }
    };
  }
  
  private HttpExecuteInterceptor tokenAuth() {
    return new BasicAuthentication(options.username, options.password);
  }
  
  // embedded classes

  public class ConnectionOptions {
    JsonFactory jsonFactory;
    String proxy;
    boolean ssl = true;
    boolean sslVerify = true;
    String token;
    String username;
    String password;
  }
}
