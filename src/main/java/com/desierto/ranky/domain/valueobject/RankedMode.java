package com.desierto.ranky.domain.valueobject;

public enum RankedMode {
  RANKED_SOLO_5x5("SoloQ"), RANKED_FLEX_SR("Flex");

  private String rankedMode;

  RankedMode(String rankedMode) {
    this.rankedMode = rankedMode;
  }

  public String getRankedMode() {
    return rankedMode;
  }

  public static RankedMode fromQueueType(String queueType) {
    return RANKED_FLEX_SR.rankedMode.equals(queueType) ? RANKED_FLEX_SR : RANKED_SOLO_5x5;
  }

  public String formatForDiscordButton() {
    return "|" + rankedMode;
  }
}
