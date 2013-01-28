package com.madhackerdesigns.jinder.models;

import com.google.api.client.util.Key;

public class Upload {
  
  // Campfire API data model: Upload
  
  @Key public long id;
  @Key public long byte_size;
  @Key public String content_type;
  @Key public String created_at;
  @Key public String name;
  @Key public long room_id;
  @Key public long user_id;
  @Key public String full_url;

}
