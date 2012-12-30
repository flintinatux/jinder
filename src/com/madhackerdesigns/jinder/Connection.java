package com.madhackerdesigns.jinder;

import java.io.IOException;

import com.google.api.client.http.BasicAuthentication;
import com.google.api.client.http.ExponentialBackOffPolicy;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpExecuteInterceptor;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.madhackerdesigns.jinder.models.Self;

public class Connection {

  private static final String HOST = "campfirenow.com";
  
  private static HttpTransport httpTransport = new NetHttpTransport();
  private static HttpRequestFactory connection;
  private static JsonFactory jsonFactory;
  
  private String subdomain;
  private String uri;
  private ConnectionOptions options;
  private String token;
  
  public Connection(String subdomain) {
    setupConnection(subdomain, new ConnectionOptions());
  }

  public Connection(String subdomain, ConnectionOptions options) {
    setupConnection(subdomain, options);
  }
  
  public Connection(String subdomain, ConnectionOptions options, HttpTransport transport) {
    httpTransport = transport;
    setupConnection(subdomain, options);
  }
  
  public String token() throws IOException {
    if (token == null) {
      Self self = get("/users/me.json").parseAs(Self.class);
      token = self.user.api_auth_token;
    }
    return token;
  }
  
  public HttpResponse get(String path) throws IOException {
    return connection().buildGetRequest(urlFor(path)).execute();
  }
  
  public HttpResponse post(String path, HttpContent content) throws IOException {
    return connection().buildPostRequest(urlFor(path), content).execute();
  }
  
  public HttpResponse put(String path, HttpContent content) throws IOException {
    return connection().buildPutRequest(urlFor(path), content).execute();
  }

  public boolean usingSSL() {
	  return options.ssl;
  }
  
  // private methods
  
  private HttpExecuteInterceptor basicAuthentication() throws IOException {
    if (token == null) {
      return new BasicAuthentication(options.username, options.password);
    } else {
      return new BasicAuthentication(token(), "X");
    }
  }
  
  private HttpRequestFactory buildConnection() {
    return connection();
  }
  
  private HttpRequestFactory connection() {
    if (connection == null) {
      connection = httpTransport.createRequestFactory(setConnectionOptions());
    }
    return connection;
  }
  
  private HttpRequestInitializer setConnectionOptions() {
    return new HttpRequestInitializer() {
      @Override
      public void initialize(HttpRequest request) throws IOException {
        request.setBackOffPolicy(new ExponentialBackOffPolicy());
        request.setInterceptor(basicAuthentication());
        request.setParser(new JsonObjectParser(Connection.jsonFactory));
      }
    };
  }
  
  private void setupConnection(String subdomain, ConnectionOptions options) {
    this.subdomain = subdomain;
    this.options = options;
    this.uri = (options.ssl ? "https" : "http") + "://" + subdomain + "." + HOST;
    this.token = options.token;
    Connection.jsonFactory = options.jsonFactory;
    buildConnection();
  }
  
  private GenericUrl urlFor(String path) {
    return new GenericUrl(uri + path);
  }
}
