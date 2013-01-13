package com.madhackerdesigns.jinder;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.api.client.http.BasicAuthentication;
import com.google.api.client.http.ExponentialBackOffPolicy;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpExecuteInterceptor;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.Base64;
import com.google.api.client.util.StringUtils;
import com.madhackerdesigns.jinder.models.SingleUser;

public class Connection {

  private static final String HOST = "campfirenow.com";
  
  private static HttpRequestFactory connection;
  private static HttpTransport httpTransport;
  private static JsonFactory jsonFactory;
  private static Logger logger;
  private static boolean logging = false;
  
  private String subdomain;
  private String token;
  private String username;
  private String password;
  private Boolean ssl;
  
  // constructors
  
  protected Connection(String subdomain, String token) {
    this.subdomain = subdomain;
    this.token = token;
  }

  protected Connection(String subdomain, String username, String password) {
    this.subdomain = subdomain;
    this.username = username;
    this.password = password;
  }
  
  // protected methods
  
  protected static void clearConnection() {
    connection = null;
  }
  
  protected void disableLogging() {
    logging = false;
  }
  
  protected void enableLogging() {
    logging = true;
  }
  
  protected void disableSSL() {
    ssl = false;
  }
  
  protected void enableSSL() {
    ssl = true;
  }
  
  protected JsonFactory jsonFactory() {
    if (jsonFactory == null) {
      jsonFactory = new GsonFactory();
    }
    return jsonFactory;
  }
  
  protected HttpResponse get(String path) throws IOException {
    return connection().buildGetRequest(urlFor(path)).execute();
  }
  
  protected HttpResponse getStreamForRoom(long roomId) throws IOException {
    return connection().buildGetRequest(streamUrlFor(roomId)).execute();
  }
  
  protected void log(Level level, String message) {
    if (logging) { logger().log(level, message); }
  }
  
  protected HttpResponse post(String path, Object object) throws IOException {
    return connection().buildPostRequest(urlFor(path), jsonContentFor(object)).execute();
  }
  
  protected HttpResponse put(String path, Object object) throws IOException {
    return connection().buildPutRequest(urlFor(path), jsonContentFor(object)).execute();
  }
  
  protected void rawPost(String path, File file) throws IOException {
    // get input stream to the file
    FileInputStream fileStream = new FileInputStream(file);
    String contentType = URLConnection.guessContentTypeFromName(file.getName());
    if (contentType == null) { contentType = "application/octet-stream"; }
    InputStreamContent fileContent = new InputStreamContent(contentType, fileStream);
    
    // build content disposition and type
    String contentDisposition = "Content-Disposition: form-data; name=\"upload\"; filename=\"" + file.getName() + "\"";
    contentType = "Content-Type: " + contentType;
    
    // build request body following the standard format
    String BOUNDARY = "---------------------------XXX";
    String requestStart = String.format("--%s\r\n%s\r\n%s\r\n\r\n", BOUNDARY, contentDisposition, contentType);
    String requestEnd = String.format("\r\n--%s--\r\n", BOUNDARY);
    
    // create http connection
    URL url = new URL(urlFor(path).build());
    logger().log(Level.INFO, "Uploading to URL: " + url.toExternalForm());
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    
    // set basic authentication
    String userPass = token() + ":X";
    String encodedAuth = Base64.encodeBase64String(StringUtils.getBytesUtf8(userPass));
    connection.setRequestProperty("Authorization", "Basic " + encodedAuth);
    
    // setup connection properties
    connection.setDoOutput(true);
    connection.setDoInput(true);
    connection.setUseCaches(false);
    connection.setRequestMethod("POST");
    connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
    
    // set content length property
    long contentLength = requestStart.length() + file.length() + requestEnd.length();
    connection.setRequestProperty("Content-Length", Long.toString(contentLength));
    
    // set streaming mode of the connection
    // see http://developer.android.com/reference/java/net/HttpURLConnection.html
    if (contentLength >= 0 && contentLength <= Integer.MAX_VALUE) {
      connection.setFixedLengthStreamingMode((int) contentLength);
    } else {
      connection.setChunkedStreamingMode(0);
    }
    
    // send the request body
    connection.connect();
    DataOutputStream out = new DataOutputStream(connection.getOutputStream());
    out.writeBytes(requestStart);
    out.flush();
    fileContent.writeTo(out);
    out.flush();
    out.writeBytes(requestEnd);
    out.flush();
    out.close();
    
    // read response from connection
    int code = connection.getResponseCode();
    String message = connection.getResponseMessage();
    logger().log(Level.INFO, String.format("Response: %d %s", code, message));
  }
  
  protected void setHttpTransport(HttpTransport httpTransport) {
    Connection.httpTransport = httpTransport;
    clearConnection();
  }
  
  protected void setJsonFactory(JsonFactory jsonFactory) {
    Connection.jsonFactory = jsonFactory;
  }

  protected Boolean ssl() {
    if (ssl == null) {
      ssl = true;
    }
    return ssl;
  }

  protected String token() throws IOException {
    if (token == null) {
      SingleUser self = get("/users/me.json").parseAs(SingleUser.class);
      token = self.user.api_auth_token;
    }
    return token;
  }
  
  protected GenericUrl urlFor(String path) throws IOException {
    URI uri;
    try {
      URL url = new URL(baseUri() + path);
      uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
    } catch (Exception e) {
      e.printStackTrace();
      throw new IOException("URISyntaxException caught.");
    }
    return new GenericUrl(uri.toString());
  }
  
  // private methods

  private String baseUri() {
    return (ssl() ? "https" : "http") + "://" + subdomain + "." + HOST;
  }
  
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
  
  private Logger logger() {
    if (logger == null) {
      logger = Logger.getLogger("com.madhackerdesigns.jinder");
    }
    return logger;
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
  
  private GenericUrl streamUrlFor(long roomId) {
    return new GenericUrl("https://streaming.campfirenow.com/room/" + roomId + "/live.json");
  }
  
}
