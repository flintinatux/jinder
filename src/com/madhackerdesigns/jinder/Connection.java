package com.madhackerdesigns.jinder;

import java.io.IOException;

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
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.gson.GsonFactory;
import com.madhackerdesigns.jinder.models.Self;

public class Connection {

  private static final String HOST = "campfirenow.com";
  
  private static HttpRequestFactory connection;
  private static HttpTransport httpTransport;
  private static JsonFactory jsonFactory;
  
  private String subdomain;
  private String token;
  private String username;
  private String password;
  private Boolean ssl;
  
  // constructors
  
  public Connection(String subdomain, String token) {
    this.subdomain = subdomain;
    this.token = token;
  }

  public Connection(String subdomain, String username, String password) {
    this.subdomain = subdomain;
    this.username = username;
    this.password = password;
  }
  
  // public methods
  
  public static void clearConnection() {
    connection = null;
  }
  
  public void disableSSL() {
    ssl = false;
  }
  
  public void enableSSL() {
    ssl = true;
  }
  
  public HttpResponse get(String path) throws IOException {
    return connection().buildGetRequest(urlFor(path)).execute();
  }
  
  public HttpResponse post(String path, Object object) throws IOException {
    return connection().buildPostRequest(urlFor(path), jsonContentFor(object)).execute();
  }
  
  public HttpResponse put(String path, Object object) throws IOException {
    return connection().buildPutRequest(urlFor(path), jsonContentFor(object)).execute();
  }
  
  public void setHttpTransport(HttpTransport httpTransport) {
    Connection.httpTransport = httpTransport;
    clearConnection();
  }
  
  public void setJsonFactory(JsonFactory jsonFactory) {
    Connection.jsonFactory = jsonFactory;
  }

  public Boolean ssl() {
    if (ssl == null) {
      ssl = true;
    }
    return ssl;
  }

  public String token() throws IOException {
    if (token == null) {
      Self self = get("/users/me.json").parseAs(Self.class);
      token = self.user.api_auth_token;
    }
    return token;
  }
  
  // protected methods
  
  protected GenericUrl urlFor(String path) {
    return new GenericUrl(uri() + path);
  }
  
  // private methods
  
  private HttpExecuteInterceptor basicAuthentication() throws IOException {
    if (token == null) {
      return new BasicAuthentication(username, password);
    } else {
      return new BasicAuthentication(token(), "X");
    }
  }
  
  private HttpRequestFactory connection() {
    if (connection == null) {
      connection = httpTransport().createRequestFactory(setConnectionOptions());
    }
    return connection;
  }
  
  private HttpTransport httpTransport() {
    if (httpTransport == null) {
      httpTransport = new NetHttpTransport();
    }
    return httpTransport;
  }
  
  private JsonHttpContent jsonContentFor(Object object) {
    if (object == null) { return null; }
    return new JsonHttpContent(jsonFactory(), object);
  }
  
  private JsonFactory jsonFactory() {
    if (jsonFactory == null) {
      jsonFactory = new GsonFactory();
    }
    return jsonFactory;
  }

  private HttpRequestInitializer setConnectionOptions() {
    return new HttpRequestInitializer() {
      @Override
      public void initialize(HttpRequest request) throws IOException {
        request.setBackOffPolicy(new ExponentialBackOffPolicy());
        request.setInterceptor(basicAuthentication());
        request.setParser(new JsonObjectParser(jsonFactory()));
      }
    };
  }

  private String uri() {
    return (ssl() ? "https" : "http") + "://" + subdomain + "." + HOST;
  }
  
}
