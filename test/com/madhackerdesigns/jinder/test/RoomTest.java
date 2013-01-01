package com.madhackerdesigns.jinder.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.api.client.http.GenericUrl;
import com.madhackerdesigns.jinder.Campfire;
import com.madhackerdesigns.jinder.Room;
import com.madhackerdesigns.jinder.test.helpers.MockTransport;

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
    mockTransport.addResponse("/rooms.json", 200, fixture("rooms.json"));
    mockTransport.addResponse("/room/80749.json", 200, fixture("room_80749.json"));
    campfire.connection().setHttpTransport(mockTransport);
    room = campfire.findRoomById(80749);
  }
  
  @Test
  public void postsToJoinUrl() throws IOException {
    campfire.connection().setHttpTransport(new MockTransport("/room/80749/join.json", 200, ""));
    room.join();
  }
  
  @Test
  public void postsToLeaveUrl() throws IOException {
    campfire.connection().setHttpTransport(new MockTransport("/room/80749/leave.json", 200, ""));
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

}
