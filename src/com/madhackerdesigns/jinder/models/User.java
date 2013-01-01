package com.madhackerdesigns.jinder.models;

import com.google.api.client.util.Key;

public class User implements Comparable<User> {
  
  @Key private long id;
  @Key private String name;
  @Key public String email_address;
  @Key public boolean admin;
  @Key public String created_at;
  @Key public String type;
  @Key public String avatar_url;
  @Key public String api_auth_token;

  @Override
  public int compareTo(User other) {
    return this.name().compareTo(other.name());
  }
  
  @Override
  public boolean equals(Object other) {
    return other instanceof User && ((User) other).id() == this.id();
  }
  
  public long id() {
    return id;
  }
  
  public String name() {
    return name;
  }
  
}
