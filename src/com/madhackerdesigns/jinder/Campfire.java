package com.madhackerdesigns.jinder;

import java.io.IOException;
import java.util.List;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;

import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.madhackerdesigns.jinder.models.RoomList;
import com.madhackerdesigns.jinder.models.SingleUser;
import com.madhackerdesigns.jinder.models.User;


/**
 * The {@link Campfire} class provides the public methods for accessing a 37signals Campfire
 * account. A new Campfire instance will build its own {@link Connection} for communicating
 * with the public Campfire API. The main purpose of the Campfire class is to fetch information
 * about available {@link Room}s and {@link User}s.
 * 
 * @author flintinatux
 * @see Room
 * @see User
 */
public class Campfire {
  
  private Connection connection;
  
  // constructors
  
  /**
   * @param subdomain
   * @param token
   */
  public Campfire(String subdomain, String token) {
    this.connection = new Connection(subdomain, token);
  }
  
  /**
   * This version of the constructor does not permanently store the username
   * and password, but instead uses them to obtain the API authentication token.
   * 
   * @param subdomain
   * @param username
   * @param password
   */
  public Campfire(String subdomain, String username, String password) {
    this.connection = new Connection(subdomain, username, password);
  }
  
  // public methods
  
//  public 
  
  /**
   * Disables logging.
   */
  public void disableLogging() {
    connection.disableLogging();
  }
  
  /**
   * Enables logging.
   */
  public void enableLogging() {
    connection.enableLogging();
  }
  
  /**
   * Finds the {@link Room} that matches the given id.
   * 
   * @param id - the id of the desired {@link Room}
   * @return the {@link Room} that matches this id, or null if no match exists
   * @throws IOException
   */
  public Room findRoomById(long id) throws IOException {
    for (Room room : rooms()) {
      if (id == room.id) { return room; }
    }
    return null;
  }
  
  /**
   * Finds the {@link Room} that matches a given name.
   * 
   * @param name - the name of the desired {@link Room}
   * @return the {@link Room} that matches this name, or null if no match exists
   * @throws IOException
   */
  public Room findRoomByName(String name) throws IOException {
    for (Room room : rooms()) {
      if (name.equals(room.name)) { return room; }
    }
    return null;
  }
  
  /**
   * Finds the {@link Room} that matches a given guest hash.
   * 
   * @param hash - the guest hash or code for the desired {@link Room}
   * @return the room for this guest hash, or null if no match exists
   * @throws IOException
   */
  public Room findRoomByGuestHash(String hash) throws IOException {
    for (Room room : rooms()) {
      if (hash.equals(room.active_token_value)) { return room; }
    }
    return null;
  }
  
  /**
   * @return the currently logged in {@link User}
   * @throws IOException
   */
  public User me() throws IOException {
    return connection.get("/users/me.json").parseAs(SingleUser.class).user;
  }
  
  /**
   * @return a list of available {@link Room}s for this account
   * @throws IOException
   */
  public List<Room> rooms() throws IOException {
    List<Room> rooms = connection.get("/rooms.json").parseAs(RoomList.class).rooms();
    for (Room room : rooms) {
      room.setConnection(connection);
    }
    return rooms;
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
  public void setHttpTransport(HttpTransport httpTransport) {
    connection.setHttpTransport(httpTransport);
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
  public void setJsonFactory(JsonFactory jsonFactory) {
    connection.setJsonFactory(jsonFactory);
  }
  
  /**
   * @return the list of all {@link User}s currently in all {@link Room}s of this Campfire account.
   * @throws IOException
   */
  public SortedSet<User> users() throws IOException {
    SortedSet<User> users = new ConcurrentSkipListSet<User>();
    for (Room room : rooms()) {
      users.addAll(room.users());
    }
    return users;
  }
  
}
