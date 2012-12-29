package com.madhackerdesigns.jinder;

import com.madhackerdesigns.jinder.Connection.ConnectionOptions;

public class Campfire {
  
  private Connection connection;
  
  public Campfire(String subdomain) {
    this.connection = new Connection(subdomain);
  }
  
  public Campfire(String subdomain, ConnectionOptions options) {
    this.connection = new Connection(subdomain, options);
  }

}
