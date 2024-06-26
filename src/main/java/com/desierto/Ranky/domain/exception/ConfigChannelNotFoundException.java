package com.desierto.Ranky.domain.exception;

public class ConfigChannelNotFoundException extends RuntimeException {

  public ConfigChannelNotFoundException() {
    super(
        "Ranky configuration channel wasn't found in the server. Please create **#config-channel** and give me access to it.");
  }
}
