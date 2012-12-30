package com.madhackerdesigns.jinder.models;

import com.google.api.client.util.Key;

public class User {
  @Key
  public Integer id;
  @Key
  public String name;
  @Key
  public String email_address;
  @Key
  public boolean admin;
  @Key
  public String created_at;
  @Key
  public String type;
  @Key
  public String avatar_url;
  @Key
  public String api_auth_token;
}
