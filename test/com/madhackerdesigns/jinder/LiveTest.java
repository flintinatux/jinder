package com.madhackerdesigns.jinder;

import java.io.IOException;

public class LiveTest {

  private static final String SUBDOMAIN = System.getenv("CAMPFIRE_SUBDOMAIN");
//  private static final String TOKEN = System.getenv("CAMPFIRE_TOKEN");
  private static final String USERNAME = System.getenv("CAMPFIRE_USERNAME");
  private static final String PASSWORD = System.getenv("CAMPFIRE_PASSWORD");
  
  private static Campfire campfire;

  public static void main(String[] args) {
    try {
//      campfire = new Campfire(SUBDOMAIN, TOKEN);
      campfire = new Campfire(SUBDOMAIN, USERNAME, PASSWORD);
      campfire.enableLogging();
      Room room = campfire.rooms().get(0);
      room.join();
      room.speak("Testing connection with Jinder.");
//      room.play("nyan");
//      room.lock();
//      room.unlock();
//      File file = new File("C:/Users/flintinatux/uploaded_file.txt");
//      room.upload(file);
      room.speak("End of test.");
//      room.leave();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
