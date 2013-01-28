package com.madhackerdesigns.jinder;

import java.io.IOException;
import java.util.List;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.madhackerdesigns.jinder.models.Account;
import com.madhackerdesigns.jinder.models.RoomList;
import com.madhackerdesigns.jinder.models.SingleAccount;
import com.madhackerdesigns.jinder.models.SingleUser;
import com.madhackerdesigns.jinder.models.User;


/**
 * The {@link Campfire} class provides the public methods for accessing a 37signals Campfire
 * account. A new Campfire instance will build its own {@link Connection} for communicating
 * with the public Campfire API. The main purpose of the Campfire class is to fetch information
 * about available {@link Room}s and {@link User}s, as well as the current {@link Account} in use.
 * 
 * @author flintinatux
 * @see Room
 * @see User
 * @see Account
 */
public class Campfire {
  
  private Connection connection;
  
  // constructors
  
  public Campfire(String subdomain, String token) {
    this.connection = new Connection(subdomain, token);
  }
  
  public Campfire(String subdomain, String username, String password) {
    this.connection = new Connection(subdomain, username, password);
  }
  
  // public methods
  
  public Account account() throws IOException {
    return connection.get("/account.json").parseAs(SingleAccount.class).account;
  }
  
  public void disableLogging() {
    connection.disableLogging();
  }
  
  public void enableLogging() {
    connection.enableLogging();
  }
  
  public Room findRoomById(long id) throws IOException {
    for (Room room : rooms()) {
      if (id == room.id) { return room; }
    }
    return null;
  }
  
  public Room findRoomByName(String name) throws IOException {
    for (Room room : rooms()) {
      if (name.equals(room.name)) { return room; }
    }
    return null;
  }
  
  public Room findRoomByGuestHash(String hash) throws IOException {
    for (Room room : rooms()) {
      if (hash.equals(room.active_token_value)) { return room; }
    }
    return null;
  }
  
  public User me() throws IOException {
    return connection.get("/users/me.json").parseAs(SingleUser.class).user;
  }
  
  public List<Room> rooms() throws IOException {
    List<Room> rooms = connection.get("/rooms.json").parseAs(RoomList.class).rooms;
    for (Room room : rooms) {
      room.setConnection(connection);
    }
    return rooms;
  }
  
  public void setHttpTransport(HttpTransport httpTransport) {
    connection.setHttpTransport(httpTransport);
  }
  
  public void setJsonFactory(JsonFactory jsonFactory) {
    connection.setJsonFactory(jsonFactory);
  }
  
  public SortedSet<User> users() throws IOException {
    SortedSet<User> users = new ConcurrentSkipListSet<User>();
    for (Room room : rooms()) {
      users.addAll(room.users());
    }
    return users;
  }
  
}
