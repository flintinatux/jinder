package com.madhackerdesigns.jinder.test.helpers;

import java.io.File;
import java.io.IOException;

import com.google.common.base.Charsets;
import com.google.common.io.Files;


public class Fixture {
  
  private File fixturePath = new File("../fixtures");

  private String filename;
  
  public Fixture(String filename) {
    this.filename = filename;
  }
  
  public String read() throws IOException {
    return Files.toString(new File(fixturePath, filename), Charsets.UTF_8);
  }

  @Override
  public String toString() {
    return filename;
  }

}
