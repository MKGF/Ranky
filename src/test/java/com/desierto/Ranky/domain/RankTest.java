package com.desierto.Ranky.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.desierto.Ranky.domain.valueobject.Division;
import com.desierto.Ranky.domain.valueobject.Rank;
import com.desierto.Ranky.domain.valueobject.Rank.Tier;
import com.desierto.Ranky.domain.valueobject.Winrate;
import org.junit.jupiter.api.Test;

public class RankTest extends BaseTest {

  @Test
  public void challenger_is_higher_than_grand_master() {
    assertThat(rankFromTier(Tier.CHALLENGER).compareTo(rankFromTier(Tier.GRANDMASTER)) > 0);
  }

  @Test
  public void grand_master_is_higher_than_master() {
    assertThat(rankFromTier(Tier.GRANDMASTER).compareTo(rankFromTier(Tier.MASTER)) > 0);
  }

  @Test
  public void master_is_higher_than_diamond() {
    assertThat(rankFromTier(Tier.MASTER).compareTo(rankFromTier(Tier.DIAMOND)) > 0);
  }

  @Test
  public void diamond_is_higher_than_emerald() {
    assertThat(rankFromTier(Tier.DIAMOND).compareTo(rankFromTier(Tier.EMERALD)) > 0);
  }

  @Test
  public void emerald_is_higher_than_platinum() {
    assertThat(rankFromTier(Tier.EMERALD).compareTo(rankFromTier(Tier.PLATINUM)) > 0);
  }

  @Test
  public void platinum_is_higher_than_gold() {
    assertThat(rankFromTier(Tier.PLATINUM).compareTo(rankFromTier(Tier.GOLD)) > 0);
  }

  @Test
  public void gold_is_higher_than_silver() {
    assertThat(rankFromTier(Tier.GOLD).compareTo(rankFromTier(Tier.SILVER)) > 0);
  }

  @Test
  public void silver_is_higher_than_bronze() {
    assertThat(rankFromTier(Tier.SILVER).compareTo(rankFromTier(Tier.BRONZE)) > 0);
  }

  @Test
  public void bronze_is_higher_than_iron() {
    assertThat(rankFromTier(Tier.BRONZE).compareTo(rankFromTier(Tier.IRON)) > 0);
  }

  @Test
  public void iron_is_higher_than_unranked() {
    assertThat(rankFromTier(Tier.IRON).compareTo(rankFromTier(Tier.UNRANKED)) > 0);
  }

  @Test
  public void unranked_is_lower_than_challenger() {
    assertThat(rankFromTier(Tier.UNRANKED).compareTo(rankFromTier(Tier.CHALLENGER)) < 0);
  }

  @Test
  public void ranks_with_same_tier_and_division_are_compared_by_league_points() {
    Rank goldThreeZeroLeaguePoints = new Rank(Tier.GOLD, Division.III, 0, defaultWinrate());
    Rank goldThreeFiftyLeaguePoints = new Rank(Tier.GOLD, Division.III, 50, defaultWinrate());
    assertThat(goldThreeFiftyLeaguePoints.compareTo(goldThreeZeroLeaguePoints) > 0);
  }

  @Test
  public void ranks_with_same_tier_and_division_and_league_points_are_compared_by_winrate() {
    Rank lowerWinrate = new Rank(Tier.GOLD, Division.III, 0, new Winrate(1, 1));
    Rank higherWinrate = new Rank(Tier.GOLD, Division.III, 0, new Winrate(2, 1));
    assertThat(higherWinrate.compareTo(lowerWinrate) > 0);
  }

  @Test
  public void same_ranks_are_equally_high() {
    Rank rank = new Rank(Tier.GOLD, Division.III, 0, defaultWinrate());
    Rank anotherRank = new Rank(Tier.GOLD, Division.III, 0, defaultWinrate());
    assertThat(rank.compareTo(anotherRank) == 0);
  }

  private Rank rankFromTier(Tier tier) {
    return new Rank(tier, Division.I, 0, defaultWinrate());
  }

  private Winrate defaultWinrate() {
    return new Winrate(0, 0);
  }
}
