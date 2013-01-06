package com.madhackerdesigns.jinder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonParser;
import com.madhackerdesigns.jinder.models.Message;

public abstract class Listener implements Runnable {
  
  private Connection connection;
  private boolean listening;
  private Logger logger;
  private BufferedReader reader;
  private long roomId;
  
  // public methods
  
  public abstract void handleNewMessage(Message message);

  @Override
  public void run() {
    try {
      JsonParser parser;
      String nextLine = reader().readLine().trim();
      while (listening && notEmpty(nextLine)) {
        logger().log(Level.INFO, nextLine);
        parser = jsonFactory().createJsonParser(nextLine);
        handleNewMessage(parser.parseAndClose(Message.class, null));
        nextLine = reader().readLine().trim();
      }
    } catch (IOException e) {
      logger().log(Level.INFO, "Exitted somehow...");
    }
  }
  
  // protected methods
  
  protected void setConnection(Connection connection) {
    this.connection = connection;
  }
  
  protected void setRoomId(long roomId) {
    this.roomId = roomId;
  }
  
  // private methods
  
  private JsonFactory jsonFactory() {
    return connection.jsonFactory();
  }
  
  private Logger logger() {
    if (logger == null) {
      logger = Logger.getLogger("com.madhackerdesigns.jinder");
    }
    return logger;
  }
  
  private boolean notEmpty(String string) {
    return string != null && ! string.equals("");
  }
  
  private BufferedReader reader() throws IOException {
    if (reader == null) {
      InputStream stream = connection.getStreamForRoom(roomId).getContent();
      reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
    }
    return reader;
  }

}
