package com.madhackerdesigns.jinder;

import java.io.IOException;
import java.util.List;

import com.madhackerdesigns.jinder.models.RoomList;


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
  
  public List<Room> rooms() throws IOException {
    List<Room> rooms = connection().get("/rooms.json").parseAs(RoomList.class).rooms;
    for (Room room : rooms) {
      room.setConnection(connection);
    }
    return rooms;
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

}
