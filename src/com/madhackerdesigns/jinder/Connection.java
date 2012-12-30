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
import com.google.api.client.util.Key;

public class Connection {

  private static final String HOST = "campfirenow.com";
  private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
  
  private static HttpRequestFactory connection;
  
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
  
  public String token() throws IOException {
    if (token == null) {
      token = requestToken();
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
  
  private HttpExecuteInterceptor basicAuthentication() {
    if (token == null) {
      return new BasicAuthentication(options.username, options.password);
    } else {
      return new BasicAuthentication(token, "X");
    }
  }
  
  private HttpRequestFactory buildConnection() { return connection(); }
  
  private HttpRequestFactory connection() {
    if (connection == null) {
      connection = HTTP_TRANSPORT.createRequestFactory(setConnectionOptions());
    }
    return connection;
  }
  
  private GenericUrl urlFor(String path) {
    return new GenericUrl(uri + path);
  }

  private String requestToken() throws IOException {
    HttpRequest request = connection().buildGetRequest(urlFor("/users/me.json"));
    Self self = request.execute().parseAs(Self.class);
    return self.user.api_auth_token;
  }
  
  private HttpRequestInitializer setConnectionOptions() {
    return new HttpRequestInitializer() {
      
      @Override
      public void initialize(HttpRequest request) throws IOException {
        request.setBackOffPolicy(new ExponentialBackOffPolicy());
        request.setInterceptor(basicAuthentication());
        request.setParser(new JsonObjectParser(options.jsonFactory));
      }
    };
  }
  
  private void setupConnection(String subdomain, ConnectionOptions options) {
    this.subdomain = subdomain;
    this.options = options;
    this.uri = (options.ssl ? "https" : "http") + "://" + subdomain + "." + HOST;
    this.token = options.token;
    buildConnection();
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
  
  public class Self {
    @Key User user;
  }
  
  public class User {
    @Key Integer id;
    @Key String name;
    @Key String email_address;
    @Key boolean admin;
    @Key String created_at;
    @Key String type;
    @Key String avatar_url;
    @Key String api_auth_token;
  }
}
