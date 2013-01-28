package com.madhackerdesigns.jinder.models;

import com.google.api.client.util.Key;

public class Account {
  
  // Campfire API data model: Account
    
  @Key public Long id;
  @Key public String name;
  @Key public String subdomain;
  @Key public String plan;
  @Key public Long owner_id;
  @Key public String time_zone;
  @Key public Long storage;
  @Key public String created_at;
  @Key public String updated_at;

}
