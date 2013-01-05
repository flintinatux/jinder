package com.madhackerdesigns.jinder;

import com.madhackerdesigns.jinder.models.Message;

public interface Listener {
  
  public void handleNewMessage(Message message);

}
