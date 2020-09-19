package com.desierto.LoLRankingMaker.domain.enumerates;

public enum QueueType {
  SOLOQ("RANKED_SOLO_5x5"), FLEXQ("RANKED_FLEX_SR");

  private String value;

  QueueType(String queueType) {
    this.value = queueType;
  }

  public String getValue() {
    return value;
  }
}
