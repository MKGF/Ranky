package com.desierto.Ranky.infrastructure.utils;

public class DiscordProgressBar {

  private static final char INCOMPLETE = '░';

  private static final char COMPLETE = '█';

  public static String getProgress(int percent) {
    return "|" + repeat(COMPLETE, percent / 2) + repeat(INCOMPLETE, (100 - percent) / 2) + "| "
        + percent
        + "%";
  }

  private static String repeat(char character, int times) {
    return new String(new char[times]).replace('\u0000', character);
  }
}
