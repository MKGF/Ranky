package com.desierto.Ranky.domain.exception;

public class CommandsChannelNotFoundException extends RuntimeException {

  public CommandsChannelNotFoundException() {
    super(
        "Ranky commands channel wasn't found in the server. Please create **#commands-channel** and give me access to it.");
  }
}
