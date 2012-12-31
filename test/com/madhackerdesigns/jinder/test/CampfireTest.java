package com.madhackerdesigns.jinder.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.madhackerdesigns.jinder.Campfire;
import com.madhackerdesigns.jinder.Room;
import com.madhackerdesigns.jinder.test.helpers.MockTransport;

public class CampfireTest extends JinderTest {
  
  private static Campfire campfire;

  @BeforeClass
  public static void loadNewCampfire() {
    campfire = new Campfire("test", "mytoken");
  }
  
  @Test
  public void returnsTheAvailableRooms() throws IOException {
    setRoomsFixture();
    assertEquals(2, campfire.rooms().size());
  }
  
  @Test
  public void setsTheRoomNameAndId() throws IOException {
    setRoomsFixture();
    Room room = campfire.rooms().get(0);
    assertEquals("Room 1", room.name);
    assertEquals(80749, room.id);
  }
  
  @Test
  public void returnsRoomWhenMatchFoundById() throws IOException {
    setRoomsFixture();
    Room room = campfire.findRoomById(80749);
    assertEquals("Room 1", room.name);
  }
  
  @Test
  public void returnsNullWhenMatchNotFoundById() throws IOException {
    setRoomsFixture();
    Room room = campfire.findRoomById(123);
    assertNull(room);
  }
  
  @Test
  public void returnsRoomWhenMatchFoundByName() throws IOException {
    setRoomsFixture();
    Room room = campfire.findRoomByName("Room 1");
    assertEquals(80749, room.id);
  }
  
  @Test
  public void returnsNullWhenMatchNotFoundByName() throws IOException {
    setRoomsFixture();
    Room room = campfire.findRoomByName("asdf");
    assertNull(room);
  }
  
  @Test
  public void returnsRoomWhenMatchFoundByGuestHash() throws IOException {
    setRoomsFixture();
    Room room = campfire.findRoomByGuestHash("4c8fb");
    assertEquals("Room 2", room.name);
  }
  
  @Test
  public void returnsNullWhenMatchNotFoundByGuestHash() throws IOException {
    setRoomsFixture();
    Room room = campfire.findRoomByGuestHash("b349e");
    assertNull(room);
  }
  
  // private helpers
  
  private void setRoomsFixture() throws IOException {
    campfire.connection().setHttpTransport(new MockTransport("/rooms.json", 200, fixture("rooms.json")));
  }

}
