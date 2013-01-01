package com.madhackerdesigns.jinder;

import java.io.IOException;
import java.util.List;

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
  
  // public methods
  
  public void setConnection(Connection connection) {
    this.connection = connection;
  }
  
  public List<User> users() throws IOException {
    reload();
    if (users == null) { throw new IOException(); }
    return users;
  }
  
  // private methods
  
  private void reload() throws IOException {
    Room room = connection.get(roomPath()).parseAs(RoomDetails.class).room;
    this.putAll(room);
  }
  
  private String roomPath() {
    return "/room/" + id + ".json";
  }

}
