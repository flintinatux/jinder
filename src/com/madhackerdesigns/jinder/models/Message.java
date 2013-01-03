package com.madhackerdesigns.jinder.models;

import com.google.api.client.util.Key;
import com.madhackerdesigns.jinder.Room;

public class Message {

  // Campfire API data model: Message
  
  @Key public long id;
  @Key public String body;
  @Key public long room_id;
  @Key public long user_id;
  @Key public String created_at;
  @Key public String type;
  @Key public boolean starred;
  
  // static and instance fields
  
  private Room room;
  
  // constructors
  
  public Message() { }
  
  public Message(String body, String type) {
    this.body = body;
    this.type = type;
  }
  
  // public methods
  
  public void setRoom(Room room) {
    this.room = room;
  }
  
}
