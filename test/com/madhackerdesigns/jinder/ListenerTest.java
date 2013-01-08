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
  public void listensToStreamingMessagesFromRoom() throws IOException {
    campfire.setHttpTransport(streamingTransport());
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
    campfire.setHttpTransport(streamingTransport());
    Listener listener = new Listener() {

      @Override
      public void handleNewMessage(Message message) {
        listenCount++;
        if (listenCount == 6) { room.stopListening(); }
      }
    };
    room.setListener(listener);
    listener.connectAndListenToMessages();
    assertEquals(6, listenCount);
  }
  
  @Test
  public void setsUserForEachMessageWhenListening() throws IOException {
    campfire.setHttpTransport(streamingTransport());
    Listener listener = new Listener() {
      
      @Override
      public void handleNewMessage(Message message) {
        listenCount++;
        if (listenCount == 5) { assertEquals("Jimmy Doe", message.user().name()); }
      }
    };
    room.setListener(listener);
    listener.connectAndListenToMessages();
  }
  
  // private methods
  
  private MockTransport streamingTransport() throws IOException {
    MockTransport mockTransport = new MockTransport();
    mockTransport.addResponse("GET", "/room/80749.json", 200, fixture("room_80749.json"));
    mockTransport.addResponse("POST", "/room/80749/join.json", 200, "");
    mockTransport.addResponse("GET", "/room/80749/live.json", 200, fixture("streaming.json"));
    mockTransport.addResponse("GET", "/users/3.json", 200, fixture("user_3.json"));
    return mockTransport;
  }

}
