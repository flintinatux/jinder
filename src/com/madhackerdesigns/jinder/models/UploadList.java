package com.madhackerdesigns.jinder.models;

import java.util.ArrayList;
import java.util.List;

import com.google.api.client.util.Key;

public class UploadList {
  
  // Campfire API data model: Upload
  
  @Key private List<SingleUpload> uploads;
  
  // public methods
  
  public List<Upload> uploads() {
    List<Upload> mapped = new ArrayList<Upload>();
    for (SingleUpload single : this.uploads){
      mapped.add(single.upload());
    }
    return mapped;
  }

}
