package com.madhackerdesigns.jinder;

import java.io.IOException;
import java.util.List;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;
import com.madhackerdesigns.jinder.models.RoomDetails;
import com.madhackerdesigns.jinder.models.User;

public class Room extends GenericJson {
  
  // Campfire API data model: Room
  
  @Key public long id;
  @Key public String name;
  @Key public String topic;
  @Key public long membership_limit;
  @Key public Boolean full;
  @Key public Boolean open_to_guests;
  @Key public String active_token_value;
  @Key public String updated_at;
  @Key public String created_at;
  @Key public Boolean locked;
  @Key private List<User> users;
  
  // class and instance fields
  
  private Connection connection;
  private boolean loaded;
  
  // constructors
  
  public Room() { }
  
  public Room(String name, String topic) {
    this.name = name;
    this.topic = topic;
  }
  
  // public methods
  
  public boolean guestAccessEnabled() throws IOException {
    load();
    return open_to_guests;
  }
  
  public String guestInviteCode() throws IOException {
    load();
    return active_token_value;
  }
  
  public GenericUrl guestUrl() throws IOException {
    if (guestAccessEnabled()) {
      return connection.urlFor("/" + active_token_value);
    }
    return null;
  }
  
  public HttpResponse join() throws IOException {
    return post("join", null);
  }
  
  public HttpResponse leave() throws IOException {
    return post("leave", null);
  }
  
  public HttpResponse rename(String name) throws IOException {
    this.name = name;
    return update(name, this.topic);
  }
  
  public void setConnection(Connection connection) {
    this.connection = connection;
  }
  
  public List<User> users() throws IOException {
    reload();
    if (users == null) { throw new IOException(); }
    return users;
  }
  
  // private methods
  
  private void load() throws IOException {
    if (!loaded) { reload(); }
  }
  
  private void reload() throws IOException {
    Room room = connection.get(roomPath()).parseAs(RoomDetails.class).room;
    this.putAll(room);
    loaded = true;
  }
  
  private String roomPath() {
    return "/room/" + id + ".json";
  }
  
  private String roomUrlFor(String action) {
    return "/room/" + id + "/" + action + ".json";
  }
  
  private HttpResponse post(String action, Object object) throws IOException {
    return connection.post(roomUrlFor(action), object);
  }
  
  private HttpResponse update(String name, String topic) throws IOException {
    RoomDetails updatedRoom = new RoomDetails();
    updatedRoom.room = new Room(name, topic);
    return connection.put(roomPath(), updatedRoom);
  }

}
