package com.madhackerdesigns.jinder;

import com.google.api.client.util.Key;

public class Room {
  
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
//  @Key private List<User> users;
  
  // class and instance fields
  
  private Connection connection;
  
  // public methods
  
  public void setConnection(Connection connection) {
    this.connection = connection;
  }

}
