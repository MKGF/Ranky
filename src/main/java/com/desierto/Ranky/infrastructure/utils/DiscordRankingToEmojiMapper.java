package com.desierto.Ranky.infrastructure.utils;

import com.desierto.Ranky.domain.valueobject.Rank.Tier;

public class DiscordRankingToEmojiMapper {

  public static String emojiFromTier(Tier tier) {
    for (TierWithEmoji tierWithEmoji :
        TierWithEmoji.values()) {
      if (tierWithEmoji.value.equalsIgnoreCase(tier.getValue())) {
        return tierWithEmoji.emoji;
      }
    }
    return TierWithEmoji.UNRANKED.emoji;
  }

  private enum TierWithEmoji {

    UNRANKED("UNRANKED", "<:Unranked:1248786000533262419>"),
    IRON("IRON", "<:Iron:1248780238553612351>"),
    BRONZE("BRONZE", "<:Bronze:1248780338289971240>"),
    SILVER("SILVER", "<:Silver:1248780200271941642>"),
    GOLD("GOLD", "<:Gold:1248780142856241193>"),
    PLATINUM("PLATINUM", "<:Platinum:1248780113013903440>"),
    EMERALD("EMERALD", "<:Emerald:1248781094980161703>"),
    DIAMOND("DIAMOND", "<:Diamond:1248780302873133117>"),
    MASTER("MASTER", "<:Master:1248780273718399038>"),
    GRANDMASTER("GRANDMASTER", "<:Grandmaster:1248780022421131347>"),
    CHALLENGER("CHALLENGER", "<:Challenger:1248780069564977183>");

    private final String value;

    private final String emoji;

    TierWithEmoji(String value, String emoji) {
      this.value = value;
      this.emoji = emoji;
    }
  }
}
