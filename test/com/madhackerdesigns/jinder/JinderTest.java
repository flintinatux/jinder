package com.madhackerdesigns.jinder;

import java.io.File;
import java.io.IOException;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class JinderTest {

  private static final String fixturePath = "test/com/madhackerdesigns/jinder/fixtures";
  
  public static String fixture(String filename) throws IOException {
    return Files.toString(new File(fixturePath, filename), Charsets.UTF_8);
  }

}
