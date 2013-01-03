package com.madhackerdesigns.jinder;

import java.io.IOException;
import java.util.List;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;

import com.madhackerdesigns.jinder.models.RoomList;
import com.madhackerdesigns.jinder.models.SingleUser;
import com.madhackerdesigns.jinder.models.User;


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
  
  public Connection connection() {
    return connection;
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
  
  public SortedSet<User> users() throws IOException {
    SortedSet<User> users = new ConcurrentSkipListSet<User>();
    for (Room room : rooms()) {
      users.addAll(room.users());
    }
    return users;
  }

}
