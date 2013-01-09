package com.madhackerdesigns.jinder.models;

import com.google.api.client.util.Key;

public class Message {

  // Campfire API data model: Message
  
  @Key public Long id;
  @Key public String body;
  @Key public Long room_id;
  @Key public Long user_id;
  @Key public String created_at;
  @Key public String type;
  @Key public Boolean starred;
  
  // instance fields
  
  public User user;
  
  // constructors
  
  public Message() { }
  
  public Message(String body, String type) {
    this.body = body;
    this.type = type;
  }
  
}
