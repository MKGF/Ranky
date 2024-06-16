package com.desierto.Ranky.infrastructure.utils;

public enum DiscordButtons {
  PAGE("public_page"),
  FINAL_PAGE("final_page");

  final String id;

  DiscordButtons(String s) {
    this.id = s;
  }

  public String getId() {
    return id;
  }
}
