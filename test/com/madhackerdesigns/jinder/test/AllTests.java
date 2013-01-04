package com.madhackerdesigns.jinder.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.madhackerdesigns.jinder.CampfireTest;
import com.madhackerdesigns.jinder.RoomTest;

@RunWith(Suite.class)
@SuiteClasses({ConnectionTest.class, CampfireTest.class, RoomTest.class})
public class AllTests {

}
