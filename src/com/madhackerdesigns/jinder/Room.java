package com.madhackerdesigns.jinder;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.json.GenericJson;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.Key;
import com.madhackerdesigns.jinder.models.Message;
import com.madhackerdesigns.jinder.models.MessageList;
import com.madhackerdesigns.jinder.models.SingleMessage;
import com.madhackerdesigns.jinder.models.SingleRoom;
import com.madhackerdesigns.jinder.models.SingleUpload;
import com.madhackerdesigns.jinder.models.SingleUser;
import com.madhackerdesigns.jinder.models.Upload;
import com.madhackerdesigns.jinder.models.UploadList;
import com.madhackerdesigns.jinder.models.User;

/**
 * The {@link Room} class represents an actual room in the Campfire lobby. A user can
 * join a room, send messages, upload files, and even start listening to new messages
 * through a streaming interface by attaching a new {@link Listener}. Do not instantiate
 * a Room directly, but instead request the list of available Rooms from a {@link Campfire}
 * instance.
 * 
 * @author flintinatux
 * @see Campfire
 * @see Listener
 * @see User
 */
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
  
  // instance fields
  
  private Connection connection;
  private Listener listener;
  private boolean loaded;
  
  // constructors
  
  /**
   * Default constructor.
   */
  public Room() { }
  
  /**
   * Special constructor used to update the name or topic of this {@link Room}.
   * 
   * @param name
   * @param topic
   */
  protected Room(String name, String topic) {
    this.name = name;
    this.topic = topic;
  }
  
  // public methods
  
  /**
   * @return a list of the five most recent {@link Upload}s to this {@link Room}
   * @throws IOException
   */
  public List<Upload> files() throws IOException {
    return get("uploads").parseAs(UploadList.class).uploads();
  }
  
  /**
   * @return true if guest access is enabled on this {@link Room}
   * @throws IOException
   */
  public boolean guestAccessEnabled() throws IOException {
    load();
    return open_to_guests;
  }
  
  /**
   * @return the guest invite code or hash for this {@link Room}
   * @throws IOException
   */
  public String guestInviteCode() throws IOException {
    load();
    return active_token_value;
  }
  
  /**
   * Builds a {@link GenericUrl} using the guest invite code or hash.
   * 
   * @return the url for a guest to visit this {@link Room}
   * @throws IOException
   */
  public GenericUrl guestUrl() throws IOException {
    if (guestAccessEnabled()) {
      return connection.urlFor("/" + active_token_value);
    }
    return null;
  }
  
  /**
   * Joins this {@link Room} as the current {@link User}. A user may not interact
   * with a room by speaking, listening, etc. without first joining it. Other users
   * in the room will receive a join message for this user.
   * 
   * @return the {@link HttpResponse} of the join request
   * @throws IOException
   */
  public HttpResponse join() throws IOException {
    log(Level.INFO, "Joining room: " + name);
    return post("join", null);
  }
  
  /**
   * Tells the Campfire server that the user is leaving this {@link Room}. The user
   * must join the room again before interacting with it further. Other users in the room
   * will receive a leave message for this user.
   * 
   * @return the {@link HttpResponse} of the leave request
   * @throws IOException
   */
  public HttpResponse leave() throws IOException {
    stopListening();
    log(Level.INFO, "Leaving room: " + name);
    return post("leave", null);
  }
  
  /**
   * Starts listening to this {@link Room} and processing new streaming messages using the
   * supplied {@link Listener}. Starts the {@link Listener} in a separate thread, so as not
   * to block the main one.
   * 
   * @param listener - the {@link Listener} with which to process incoming messages
   * @throws IOException
   * @see Listener
   */
  public void listen(Listener listener) throws IOException {
    setListener(listener);
    new Thread(listener).start();
  }
  
  /**
   * @return true if a {@link Listener} is listening to this {@link Room}
   */
  public boolean isListening() {
    return listener != null;
  }
  
  /**
   * Locks the {@link Room} so no other users may join.
   * 
   * @return the {@link HttpResponse} of the lock request
   * @throws IOException
   */
  public HttpResponse lock() throws IOException {
    return post("lock", null);
  }
  
  /**
   * Sends a new message to the {@link Room} of type PasteMessage.
   * 
   * @param message - the PasteMessage to send
   * @return the new {@link Message} created by the paste request
   * @throws IOException
   */
  public Message paste(String message) throws IOException {
    return sendMessage(message, "PasteMessage").parseAs(SingleMessage.class).message;
  }
  
  /**
   * Send the name of the specified sound to the {@link Room} as type SoundMessage.
   * 
   * @param sound - the name of the sound to play in the {@link Room}
   * @return the new {@link Message} created by the play request
   * @throws IOException
   */
  public Message play(String sound) throws IOException {
    return sendMessage(sound, "SoundMessage").parseAs(SingleMessage.class).message;
  }
  
  /**
   * @return a {@link List} of up to 100 of the most recent {@link Message}s for this {@link Room}
   * @throws IOException
   */
  public List<Message> recent() throws IOException {
    return get("recent").parseAs(MessageList.class).messages;
  }
  
  /**
   * @param sinceMessageId - the {@link Message} id to get recent messages since
   * @return a {@link List} of up to 100 of the most recent {@link Message}s for this {@link Room} since
   * the specified message
   * @throws IOException
   */
  public List<Message> recent(long sinceMessageId) throws IOException {
    String recentUrl = roomUrlFor("recent") + "?since_message_id=" + sinceMessageId;
    return connection.get(recentUrl).parseAs(MessageList.class).messages;
  }
  
  /**
   * @param name - the desired new name for the {@link Room}
   * @return the {@link HttpResponse} of the update request
   * @throws IOException
   */
  public HttpResponse rename(String name) throws IOException {
    return setName(name);
  }
  
  /**
   * Searches through available transcripts on this account for the provided search term.
   * 
   * @param term - the term to search for
   * @return a {@link List} of {@link Message}s matching this search term
   * @throws IOException
   */
  public List<Message> search(String term) throws IOException {
    return connection.get("/search?q=" + term + "&format=json").parseAs(MessageList.class).messages;
  }
  
  /**
   * @param name - the desired new name for the {@link Room}
   * @return the {@link HttpResponse} of the update request
   * @throws IOException
   */
  public HttpResponse setName(String name) throws IOException {
    return update(name, this.topic);
  }
  
  /**
   * @param topic - the desired new topic for the {@link Room}
   * @return the {@link HttpResponse} of the update request
   * @throws IOException
   */
  public HttpResponse setTopic(String topic) throws IOException {
    return update(this.name, topic);
  }
  
  /**
   * Hopefully the most used method in this library! Sends a message to the {@link Room} of
   * type TextMessage.
   * 
   * @param message - the message to send to the {@link Room}
   * @return the new {@link Message} created by the speak request
   * @throws IOException
   */
  public Message speak(String message) throws IOException {
    return sendMessage(message, "TextMessage").parseAs(SingleMessage.class).message;
  }
  
  /**
   * Stops listening to this {@link Room}, and detaches the current {@link Listener}.
   * 
   * @see Listener
   */
  public void stopListening() {
    if (isListening()) {
      log(Level.INFO, "Stopped listening to room: " + name);
      listener.stop();
      listener = null;
    }
  }
  
  /**
   * @return the current topic of the {@link Room}
   * @throws IOException
   */
  public String topic() throws IOException {
    reload();
    return topic;
  }
  
  /**
   * @param date - the {@link Calendar} date of the transcript to retrieve
   * @return a {@link List} of {@link Message}s comprising the transcript of that date
   * @throws IOException
   */
  public List<Message> transcript(Calendar date) throws IOException {
    return get(transcriptPath(date)).parseAs(MessageList.class).messages;
  }
  
  /**
   * Sends a url message to this {@link Room} of type TweetMessage.
   * 
   * @param url - the Twitter url of the tweet message to send
   * @return the new {@link Message} created by the tweet request
   * @throws IOException
   */
  public Message tweet(String url) throws IOException {
    return sendMessage(url, "TweetMessage").parseAs(SingleMessage.class).message;
  }
  
  /**
   * Unlocks the {@link Room} so that other users may join.
   * 
   * @return the {@link HttpResponse} of the unlock request
   * @throws IOException
   */
  public HttpResponse unlock() throws IOException {
    return post("unlock", null);
  }
  
  /**
   * Uploads a {@link File} to the {@link Room} as a multipart/form-data request.
   * 
   * @param file - the {@link File} to upload to the {@link Room}
   * @return the new {@link Upload} created by the upload request
   * @throws IOException
   */
  public Upload upload(File file) throws IOException {
    RawPost.Response response = connection.rawPost(roomUrlFor("uploads"), file);
    return connection.jsonFactory().fromInputStream(response.getContent(), SingleUpload.class).upload;
  }

  /**
   * @param id - the id of the desired {@link User}
   * @return the {@link User} that matches the provided id
   * @throws IOException
   */
  public User user(long id) throws IOException {
    load();
    User found = null;
    for (User user : users) {
      if(user.id() == id) { found = user; }
    }
    if (found == null) {
      found = connection.get(userPath(id)).parseAs(SingleUser.class).user;
      users.add(found);
    }
    return found;
  }
  
  /**
   * @return the {@link List} of {@link User}s currently in the {@link Room}
   * @throws IOException
   */
  public List<User> users() throws IOException {
    reload();
    return users;
  }
  
  // protected methods

  /**
   * @param connection - the {@link Connection} to attach to this {@link Room}
   */
  protected void setConnection(Connection connection) {
    this.connection = connection;
  }
  
  /**
   * @param listener - the {@link Listener} to attach to this {@link Room}
   */
  protected void setListener(Listener listener) {
    listener.setConnection(connection);
    listener.setRoom(this);
    this.listener = listener;
  }
  
  // private methods
  
  /**
   * Builds an executes a GET request for an action on this {@link Room}.
   * 
   * @param action - the action to take on this {@link Room}
   * @return the {@link HttpResponse} of this GET request
   * @throws IOException
   */
  private HttpResponse get(String action) throws IOException {
    return connection.get(roomUrlFor(action));
  }
  
  /**
   * Reloads this {@link Room} if not already loaded.
   * 
   * @throws IOException
   */
  private void load() throws IOException {
    if (!loaded) { reload(); }
  }
  
  /**
   * Shortcut to log a message with the Campfire-wide {@link Logger}.
   * 
   * @param level - the Log {@link Level} of the message
   * @param message - the message to log
   */
  private void log(Level level, String message) {
    connection.log(level, message);
  }
  
  /**
   * Reloads this {@link Room}'s attributes from the Campfire server.
   * 
   * @throws IOException
   */
  private void reload() throws IOException {
    Room room = connection.get(roomPath()).parseAs(SingleRoom.class).room;
    this.putAll(room);
    loaded = true;
  }
  
  /**
   * @return the path to the endpoint for this {@link Room}
   */
  private String roomPath() {
    return "/room/" + id + ".json";
  }
  
  /**
   * Builds a relative url for the provided action on this {@link Room}
   * 
   * @param action - the action to build a url for
   * @return the new url for this action
   */
  private String roomUrlFor(String action) {
    return "/room/" + id + "/" + action + ".json";
  }
  
  /**
   * Encodes the given {@link Object} into json using the provided {@link JsonFactory},
   * and then builds and executes a POST request to the given action with that json data
   * as the body of the request. The object must use {@link Key} annotation to specify 
   * which member fields to encode.
   * 
   * @param action - the action on this {@link Room} to post the given {@link Object} to
   * @param object - the {@link Object} to send
   * @return the {@link HttpResponse} of this post request
   * @throws IOException
   */
  private HttpResponse post(String action, Object object) throws IOException {
    return connection.post(roomUrlFor(action), object);
  }
  
  /**
   * @param message - the message to send to the {@link Room}
   * @param type - the type of the message (ex: "TextMessage")
   * @return the {@link HttpResponse} of the send request
   * @throws IOException
   */
  private HttpResponse sendMessage(String message, String type) throws IOException {
    SingleMessage newMessage = new SingleMessage();
    newMessage.message = new Message(message, type);
    return connection.post(roomUrlFor("speak"), newMessage);
  }
  
  /**
   * @param date - the {@link Calendar} date to build a transcript path for
   * @return the path for the transcript of messages on that date in this {@link Room}
   */
  private String transcriptPath(Calendar date) {
    return "transcript/" + date.get(Calendar.YEAR) + "/" + date.get(Calendar.MONTH) + "/" + date.get(Calendar.DAY_OF_MONTH);
  }
  
  /**
   * @param name - the new name for this {@link Room}
   * @param topic - the new topic for this {@link Room}
   * @return the {@link HttpResponse} for the update request
   * @throws IOException
   */
  private HttpResponse update(String name, String topic) throws IOException {
    SingleRoom updatedRoom = new SingleRoom();
    updatedRoom.room = new Room(name, topic);
    return connection.put(roomPath(), updatedRoom);
  }
  
  /**
   * @param id - the id of the desired {@link User}
   * @return the path of the endpoint for the desired {@link User}
   */
  private String userPath(long id) {
    return "/users/" + id + ".json";
  }

}
