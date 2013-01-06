package com.madhackerdesigns.jinder;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.madhackerdesigns.jinder.helpers.MockTransport;
import com.madhackerdesigns.jinder.models.Message;

public class ListenerTest extends JinderTest {
  
  private static Campfire campfire;
  
  private int listenCount = 0;
  private Room room;

  @BeforeClass
  public static void loadNewCampfire() throws IOException {
    campfire = new Campfire("test", "mytoken");
  }
  
  @Before
  public void loadNewRoom() throws IOException {
    MockTransport mockTransport = new MockTransport();
    mockTransport.addResponse("GET", "/rooms.json", 200, fixture("rooms.json"));
    mockTransport.addResponse("GET", "/room/80749.json", 200, fixture("room_80749.json"));
    campfire.setHttpTransport(mockTransport);
    room = campfire.findRoomById(80749);
  }
  
  @Test
  public void listensToStreamingMessagesFromRoom() throws IOException, InterruptedException {
    campfire.setHttpTransport(new MockTransport("GET", "/room/80749/live.json", 200, fixture("streaming.json")));
    Listener listener = new Listener() {

      @Override
      public void handleNewMessage(Message message) {
        assertEquals(false, message.starred);
        listenCount++;
      }
      
    };
    room.setListener(listener);
    listener.connectAndListenToMessages();
    assertEquals(8, listenCount);
  }
  
  @Test
  public void stopsListeningToRoom() throws IOException {
    campfire.setHttpTransport(new MockTransport("GET", "/room/80749/live.json", 200, fixture("streaming.json")));
    Listener listener = new Listener() {

      @Override
      public void handleNewMessage(Message message) {
        listenCount++;
        if (listenCount == 6) { stop(); }
      }
      
    };
    room.setListener(listener);
    listener.connectAndListenToMessages();
    assertEquals(6, listenCount);
  }

}
