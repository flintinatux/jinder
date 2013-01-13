package com.madhackerdesigns.jinder;

import java.io.File;
import java.io.IOException;

public class LiveTest {

  private static final String TOKEN = System.getenv("CAMPFIRE_TOKEN");
  private static final String SUBDOMAIN = System.getenv("CAMPFIRE_SUBDOMAIN");
  
  private static Campfire campfire;

  public static void main(String[] args) {
    try {
      campfire = new Campfire(SUBDOMAIN, TOKEN);
      campfire.enableLogging();
      Room room = campfire.rooms().get(0);
      room.join();
      room.speak("Testing upload with Jinder.");
      room.play("nyan");
      room.lock();
      room.unlock();
      File file = new File("C:/Users/flintinatux/uploaded_file.txt");
      room.upload(file);
      room.speak("End of test.");
      room.leave();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
