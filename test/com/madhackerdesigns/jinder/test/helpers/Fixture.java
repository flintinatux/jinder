package com.madhackerdesigns.jinder.test.helpers;

import java.io.File;
import java.io.IOException;

import com.google.common.base.Charsets;
import com.google.common.io.Files;


public class Fixture {
  
  private static final String fixturePath = "test/com/madhackerdesigns/jinder/test/fixtures";
  
  public static String fixture(String filename) throws IOException {
    return Files.toString(new File(fixturePath, filename), Charsets.UTF_8);
  }

}
