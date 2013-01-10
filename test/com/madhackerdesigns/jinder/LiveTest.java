package com.madhackerdesigns.jinder;

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
      room.speak("Test message from Jinder.");
      room.play("nyan");
      room.lock();
      room.unlock();
      room.speak("End of test.");
      room.leave();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
