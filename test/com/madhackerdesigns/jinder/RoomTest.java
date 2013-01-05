package com.madhackerdesigns.jinder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.api.client.http.GenericUrl;
import com.madhackerdesigns.jinder.Campfire;
import com.madhackerdesigns.jinder.Room;
import com.madhackerdesigns.jinder.helpers.MockTransport;
import com.madhackerdesigns.jinder.models.Message;
import com.madhackerdesigns.jinder.models.User;

public class RoomTest extends JinderTest {
  
  private static Campfire campfire;
  private static Room room;

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
  public void postsToJoinUrl() throws IOException {
    campfire.setHttpTransport(new MockTransport("POST", "/room/80749/join.json", 200, ""));
    room.join();
  }
  
  @Test
  public void postsToLeaveUrl() throws IOException {
    campfire.setHttpTransport(new MockTransport("POST", "/room/80749/leave.json", 200, ""));
    room.leave();
  }
  
  @Test
  public void returnsGuestUrlIfActive() throws IOException {
    GenericUrl guestUrl = room.guestUrl();
    assertEquals("https://test.campfirenow.com/90cf7", guestUrl.build());
  }
  
  @Test
  public void returnsNullIfGuestAccessDisabled() throws IOException {
    room.guestUrl();
    room.open_to_guests = false;
    assertNull(room.guestUrl());
  }
  
  @Test
  public void setsGuestInviteCode() throws IOException {
    assertEquals("90cf7", room.guestInviteCode());
  }
  
  @Test
  public void setsGuestAccessEnabled() throws IOException {
    assertTrue(room.guestAccessEnabled());
  }
  
  @Test
  public void putsToUpdateTheRoomName() throws IOException {
    campfire.setHttpTransport(new MockTransport("PUT", "/room/80749.json", 200, ""));
    room.setName("Foo");
    room.rename("Bar");
  }
  
  @Test
  public void putsToUpdateTheRoomTopic() throws IOException {
    campfire.setHttpTransport(new MockTransport("PUT", "/room/80749.json", 200, ""));
    room.setTopic("New topic");
  }
  
  @Test
  public void getsTheCurrentTopic() throws IOException {
    assertEquals("Testing", room.topic());
  }
  
  @Test
  public void getsTopicEvenIfItsChanged() throws IOException {
    assertEquals("Testing", room.topic());
    campfire.setHttpTransport(new MockTransport("GET", "/room/80749.json", 200, fixture("room_80751.json")));
    assertEquals("Testing 2", room.topic());
  }
  
  @Test
  public void postsToLockTheRoom() throws IOException {
    campfire.setHttpTransport(new MockTransport("POST", "/room/80749/lock.json", 200, ""));
    room.lock();
  }
  
  @Test
  public void postsToUnlockTheRoom() throws IOException {
    campfire.setHttpTransport(new MockTransport("POST", "/room/80749/unlock.json", 200, ""));
    room.unlock();
  }
  
  @Test
  public void postsToSpeakMessage() throws IOException {
    campfire.setHttpTransport(new MockTransport("POST", "/room/80749/speak.json", 201, fixture("speak.json")));
    room.speak("Hello");
  }
  
  @Test
  public void postsToPasteMessage() throws IOException {
    campfire.setHttpTransport(new MockTransport("POST", "/room/80749/speak.json", 201, fixture("speak.json")));
    room.paste("Hello\nWorld!");
  }
  
  @Test
  public void postsToPlaySound() throws IOException {
    campfire.setHttpTransport(new MockTransport("POST", "/room/80749/speak.json", 201, fixture("speak.json")));
    room.play("trombone");
  }
  
  @Test
  public void postsToTweetTheUrl() throws IOException {
    campfire.setHttpTransport(new MockTransport("POST", "/room/80749/speak.json", 201, fixture("speak.json")));
    room.tweet("http://madhackerdesigns.com");
  }
  
  @Test
  public void findsUserByIdInRoomCache() throws IOException {
    User user = room.user(2);
    assertEquals("John Doe", user.name());
  }
  
  @Test
  public void findsUserByIdFromApi() throws IOException {
    MockTransport mockTransport = new MockTransport();
    mockTransport.addResponse("GET", "/room/80749.json", 200, fixture("room_80749.json"));
    mockTransport.addResponse("GET", "/users/3.json", 200, fixture("user_3.json"));
    campfire.setHttpTransport(mockTransport);
    User user = room.user(3);
    assertEquals("Jimmy Doe", user.name());
  }
  
  @Test
  public void cachesFoundUserUntilNextReload() throws IOException {
    MockTransport mockTransport = new MockTransport();
    mockTransport.addResponse("GET", "/room/80749.json", 200, fixture("room_80749.json"));
    mockTransport.addResponse("GET", "/users/3.json", 200, fixture("user_3.json"));
    campfire.setHttpTransport(mockTransport);
    User user = room.user(3);
    assertEquals("Jimmy Doe", user.name());
    campfire.setHttpTransport(new MockTransport("GET", "/room/80749.json", 200, fixture("room_80749.json")));
    user = room.user(3);
    List<User> users = room.users();
    assertFalse(users.contains(user));
  }
  
  @Test
  public void getsTranscriptForSpecificDate() throws IOException {
    MockTransport mockTransport = new MockTransport();
    mockTransport.addResponse("GET", "/room/80749.json", 200, fixture("room_80749.json"));
    mockTransport.addResponse("GET", "/room/80749/transcript/2013/1/2.json", 200, fixture("message_list.json"));
    campfire.setHttpTransport(mockTransport);
    Calendar date = Calendar.getInstance();
    date.set(2013, 1, 2);
    List<Message> messages = room.transcript(date);
    assertEquals("Lol", messages.get(1).body);
  }
  
  @Test
  public void searchesForTermWithEncodesString() throws IOException {
    MockTransport mockTransport = new MockTransport();
    mockTransport.addResponse("GET", "/room/80749.json", 200, fixture("room_80749.json"));
    mockTransport.addResponse("GET", "/search?q=kittenz%20rule!&format=json", 200, fixture("message_list.json"));
    campfire.setHttpTransport(mockTransport);
    List<Message> messages = room.search("kittenz rule!");
    assertEquals("Lol", messages.get(1).body);
  }

}
