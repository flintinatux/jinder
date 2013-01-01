package com.madhackerdesigns.jinder;

import java.io.IOException;
import java.util.List;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;
import com.madhackerdesigns.jinder.models.Message;
import com.madhackerdesigns.jinder.models.MessageDetails;
import com.madhackerdesigns.jinder.models.RoomDetails;
import com.madhackerdesigns.jinder.models.User;

public class Room extends GenericJson {
  
  // Campfire API data model: Room
  
  @Key public long id;
  @Key public String name;
  @Key private String topic;
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
  
  public HttpResponse lock() throws IOException {
    return post("lock", null);
  }
  
  public HttpResponse rename(String name) throws IOException {
    return setName(name);
  }
  
  public HttpResponse paste(String message) throws IOException {
    return sendMessage(message, "PasteMessage");
  }
  
  public HttpResponse play(String sound) throws IOException {
    return sendMessage(sound, "SoundMessage");
  }
  
  public void setConnection(Connection connection) {
    this.connection = connection;
  }
  
  public HttpResponse setName(String name) throws IOException {
    return update(name, this.topic);
  }
  
  public HttpResponse setTopic(String topic) throws IOException {
    return update(this.name, topic);
  }
  
  public HttpResponse speak(String message) throws IOException {
    return sendMessage(message, "TextMessage");
  }
  
  public String topic() throws IOException {
    reload();
    return topic;
  }
  
  public HttpResponse tweet(String url) throws IOException {
    return sendMessage(url, "TweetMessage");
  }
  
  public HttpResponse unlock() throws IOException {
    return post("unlock", null);
  }
  
  public List<User> users() throws IOException {
    reload();
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
  
  private HttpResponse sendMessage(String message, String type) throws IOException {
    MessageDetails newMessage = new MessageDetails();
    newMessage.message = new Message(message, type);
    return connection.post(roomUrlFor("speak"), newMessage);
  }
  
  private HttpResponse update(String name, String topic) throws IOException {
    RoomDetails updatedRoom = new RoomDetails();
    updatedRoom.room = new Room(name, topic);
    return connection.put(roomPath(), updatedRoom);
  }

}
