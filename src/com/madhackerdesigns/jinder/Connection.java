package com.madhackerdesigns.jinder;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.http.BasicAuthentication;
import com.google.api.client.http.ExponentialBackOffPolicy;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpExecuteInterceptor;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.Key;
import com.madhackerdesigns.jinder.RawPost.Response;
import com.madhackerdesigns.jinder.models.SingleUser;

/**
 * The {@link Connection} class contains all of the protected helper methods for communicating
 * with the public Campfire API. {@link Connection}'s are not intended to be instantiated
 * directly, but will be constructed by a {@link Campfire} instance when needed.
 * 
 * @author flintinatux
 * @see Campfire
 */
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
  
  /**
   * @param subdomain - the Campfire subdomain to connect to.
   * @param token - the user's API authentication token.
   */
  protected Connection(String subdomain, String token) {
    this.subdomain = subdomain;
    this.token = token;
  }

  /**
   * This version of the constructor does not permanently store the username
   * and password, but instead uses them to obtain the API authentication token.
   * 
   * @param subdomain - the Campfire subdomain to connect to.
   * @param username - the user's username.
   * @param password - the user's password.
   */
  protected Connection(String subdomain, String username, String password) {
    this.subdomain = subdomain;
    this.username = username;
    this.password = password;
  }
  
  // protected methods
  
  /**
   * Clears the current connection so that new settings can be reloaded on next use.
   */
  protected static void clearConnection() {
    connection = null;
  }
  
  /**
   * Disables logging.
   * 
   * @see Logger
   */
  protected void disableLogging() {
    logging = false;
  }
  
  /**
   * Enables logging.
   * 
   * @see Logger
   */
  protected void enableLogging() {
    logging = true;
  }
  
  /**
   * Disables the use of SSL on this connection.
   * 
   * @see https://github.com/37signals/campfire-api#ssl-usage
   */
  protected void disableSSL() {
    ssl = false;
  }
  
  /**
   * Enables the use of SSL on this connection.
   * 
   * @see https://github.com/37signals/campfire-api#ssl-usage
   */
  protected void enableSSL() {
    ssl = true;
  }
  
  /**
   * Builds and executes a GET request for the given path.
   * 
   * @param path - the relative path of the GET request. (ex: "/room/1234/transcript.json")
   * @return the {@link HttpResponse} of the GET request.
   * @throws IOException
   */
  protected HttpResponse get(String path) throws IOException {
    return connection().buildGetRequest(urlFor(path)).execute();
  }
  
  /**
   * Opens a streaming connection to the desired room to listen to new messages.
   * 
   * @param roomId - the id of the room to listen to.
   * @return the {@link HttpResponse} containing the streaming message data.
   * @throws IOException
   */
  protected HttpResponse getStreamForRoom(long roomId) throws IOException {
    return connection().buildGetRequest(streamUrlFor(roomId)).execute();
  }
  
  /**
   * If the desired JsonFactory is not currently set, the connection defaults
   * to using a {@link GsonFactory}.
   * 
   * @return the current JsonFactory to use for encoding/decoding data.
   * @see GsonFactory
   */
  protected JsonFactory jsonFactory() {
    if (jsonFactory == null) {
      jsonFactory = new GsonFactory();
    }
    return jsonFactory;
  }
  
  /**
   * Log a message with the current logging settings.
   * 
   * @param level - the log {@link Level} of the message.
   * @param message - the log message.
   */
  protected void log(Level level, String message) {
    if (logging) { logger().log(level, message); }
  }
  
  /**
   * Encodes the given {@link Object} into json using the provided {@link JsonFactory},
   * and then builds and executes a POST request to the given path with that json data
   * as the body of the request. The object must use {@link Key} annotation to specify 
   * which member fields to encode.
   * 
   * @param path - the relative path of the POST request. (ex: "/room/1234/speak.json")
   * @param object - the {@link Object} to encode into json for posting
   * @return the {@link HttpResponse} of the POST request.
   * @throws IOException
   */
  protected HttpResponse post(String path, Object object) throws IOException {
    return connection().buildPostRequest(urlFor(path), jsonContentFor(object)).execute();
  }
  
  /**
   * Encodes the given {@link Object} into json using the provided {@link JsonFactory},
   * and then builds and executes a PUT request to the given path with that json data
   * as the body of the request. The object must use {@link Key} annotation to specify 
   * which member fields to encode.
   * 
   * @param path - the relative path of the PUT request. (ex: "/room/1234/speak.json")
   * @param object - the {@link Object} to encode into json for posting
   * @return the {@link HttpResponse} of the PUT request.
   * @throws IOException
   */
  protected HttpResponse put(String path, Object object) throws IOException {
    return connection().buildPutRequest(urlFor(path), jsonContentFor(object)).execute();
  }
  
  /**
   * Builds and executes a {@link RawPost} request to upload the given {@link File} as the
   * multipart/form-data request body using the upload format specified by the Campfire API.
   * 
   * @param path - the relative path of the raw POST request. (ex: "/room/1234/uploads.json")
   * @param file - the {@link File} to upload as the multipart/form-data request body. 
   * @return the {@link Response} of the raw POST request.
   * @throws IOException
   */
  protected RawPost.Response rawPost(String path, File file) throws IOException {
    return new RawPost(this, urlFor(path).build(), file).execute();
  }
  
  /**
   * Sets the pluggable {@link HttpTransport} to use for this connection. If not explicitly set,
   * the default is {@link NetHttpTransport}.
   * 
   * @param httpTransport - the {@link HttpTransport} to use for this connection.
   * @see NetHttpTransport
   * @see ApacheHttpTransport
   * @see UrlFetchTransport
   */
  protected void setHttpTransport(HttpTransport httpTransport) {
    Connection.httpTransport = httpTransport;
    clearConnection();
  }
  
  /**
   * Sets the pluggable {@link JsonFactory} to use for this connection. If not explicitly set,
   * the default is {@link GsonFactory}.
   *  
   * @param jsonFactory - the {@link JsonFactory} to use for this connection.
   * @see AndroidJsonFactory
   * @see GsonFactory
   * @see JacksonFactory
   */
  protected void setJsonFactory(JsonFactory jsonFactory) {
    Connection.jsonFactory = jsonFactory;
  }

  /**
   * The current status of using SSL for this connection. Defaults to true if not currently set.
   * 
   * @return true if SSL is enabled. 
   */
  protected Boolean ssl() {
    if (ssl == null) {
      ssl = true;
    }
    return ssl;
  }

  /**
   * If this {@link Connection} was constructed with the user's username and password, then the
   * corresponding API authentication token is requested from the Campfire server and cached for use.
   * 
   * @return the user's API authentication token for Campfire.
   * @throws IOException
   */
  protected String token() throws IOException {
    if (token == null) {
      SingleUser self = get("/users/me.json").parseAs(SingleUser.class);
      token = self.user.api_auth_token;
    }
    return token;
  }
  
  /**
   * Builds a full valid {@link GenericUrl} for the given path and the Campfire subdomain.
   * 
   * @param path - the relative path to build a {@link GenericUrl} with. (ex: "/room/1234/speak.json")
   * @return the {@link GenericUrl} for the given path.
   * @throws IOException
   */
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

  /**
   * Builds a base URI for the connection using the subdomain and current SSL status.
   * 
   * @return the base URI of the connection.
   */
  private String baseUri() {
    return (ssl() ? "https" : "http") + "://" + subdomain + "." + HOST;
  }
  
  /**
   * If the API authentication token is not set, it returns a {@link BasicAuthentication} using
   * the user's username and password, so that a token may be requested.
   * 
   * @return the {@link BasicAuthentication} needed for the connection.
   * @throws IOException
   */
  private HttpExecuteInterceptor basicAuthentication() throws IOException {
    if (token == null) {
      return new BasicAuthentication(username, password);
    } else {
      return new BasicAuthentication(token(), "X");
    }
  }
  
  /**
   * Builds a new {@link HttpRequestFactory} for the connection, and ensures the API authentication
   * token is available for future requests.
   * 
   * @return the {@link HttpRequestFactory} to use for the connection.
   * @throws IOException
   */
  private HttpRequestFactory connection() throws IOException {
    if (connection == null) {
      connection = httpTransport().createRequestFactory(setConnectionOptions());
      token();
    }
    return connection;
  }
  
  /**
   * If the desired {@link HttpTransport} is not currently set, the connection defaults to
   * using the {@link NetHttpTransport}.
   * 
   * @return the {@link HttpTransport} to use for this connection.
   */
  private HttpTransport httpTransport() {
    if (httpTransport == null) {
      httpTransport = new NetHttpTransport();
    }
    return httpTransport;
  }
  
  /**
   * Encodes the given {@link Object} as json data to use in the request body. The object
   * must use {@link Key} annotation to specify which member fields to encode.
   * 
   * @param object - the {@link Object} to encode for the request body.
   * @return
   */
  private JsonHttpContent jsonContentFor(Object object) {
    if (object == null) { return null; }
    return new JsonHttpContent(jsonFactory(), object);
  }
  
  /**
   * @return - the {@link Logger} for this package.
   */
  private Logger logger() {
    if (logger == null) {
      logger = Logger.getLogger("com.madhackerdesigns.jinder");
    }
    return logger;
  }

  /**
   * Sets up the new connection with an {@link ExponentialBackOffPolicy}, the correct 
   * {@link BasicAuthentication}, and a {@link JsonObjectParser} that uses the specified
   * {@link JsonFactory}.
   * 
   * @return the {@link HttpRequestInitializer} to use for this connection.
   */
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
  
  /**
   * @param roomId - the id of the room for which to build a streaming url.
   * @return the {@link GenericUrl} for the streaming connection.
   */
  private GenericUrl streamUrlFor(long roomId) {
    return new GenericUrl("https://streaming.campfirenow.com/room/" + roomId + "/live.json");
  }
  
}
