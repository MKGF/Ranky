package com.desierto.Ranky.infrastructure.utils;

public enum DiscordMessages {
  EXECUTE_COMMAND_FROM_SERVER("You need to execute the command inside a server."),

  COMMAND_NOT_ALLOWED("You are not allowed to execute this command.");

  final String message;

  DiscordMessages(String s) {
    this.message = s;
  }

  public String getMessage() {
    return message;
  }
}
