package com.madhackerdesigns.jinder.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.madhackerdesigns.jinder.Campfire;
import com.madhackerdesigns.jinder.Room;
import com.madhackerdesigns.jinder.models.User;
import com.madhackerdesigns.jinder.test.helpers.MockTransport;

public class RoomTest extends JinderTest {
  
  private static Campfire campfire;

  @BeforeClass
  public static void loadNewCampfire() {
    campfire = new Campfire("test", "mytoken");
  }

  @Test
  public final void returnsListOfUsers() throws IOException {
    MockTransport mockTransport = new MockTransport();
    mockTransport.addResponse("/rooms.json", 200, fixture("rooms.json"));
    mockTransport.addResponse("/room/80749.json", 200, fixture("room_80749.json"));
    campfire.connection().setHttpTransport(mockTransport);
    Room room = campfire.findRoomById(80749);
    List<User> users = room.users();
    assertEquals("Jane Doe", users.get(0).name());
  }

}
