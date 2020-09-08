package domain;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import domain.Rank.Tier;
import domain.builder.RankBuilder;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

public class RankingTest {

  @Test
  public void ranking_with_gold1_silver3_and_silver4_is_sorted_by_rank() {
    RankBuilder rankBuilder = new RankBuilder();
    Rank gold1 = rankBuilder.tier(Tier.GOLD).division(1).build();
    Rank silver3 = rankBuilder.tier(Tier.SILVER).division(3).build();
    Rank silver4 = rankBuilder.tier(Tier.SILVER).division(4).build();
    Account gold1Account = Account.builder().id(1).name("Mikel").leagueOfLegendsId("MAIKY")
        .rank(gold1).build();
    Account silver3Account = Account.builder().id(2).name("Naza").leagueOfLegendsId("naza30sec")
        .rank(silver3).build();
    Account silver4Account = Account.builder().id(3).name("Asier")
        .leagueOfLegendsId("AficionadoAlFallGuys").rank(silver4).build();
    Ranking ranking = new Ranking();
    ranking.addAccount(silver4Account);
    ranking.addAccount(gold1Account);
    ranking.addAccount(silver3Account);
    ArrayList<Account> notSortedAccounts = new ArrayList<>();
    notSortedAccounts.add(silver4Account);
    notSortedAccounts.add(gold1Account);
    notSortedAccounts.add(silver3Account);
    ArrayList<Account> sortedAccounts = new ArrayList<>();
    sortedAccounts.add(gold1Account);
    sortedAccounts.add(silver3Account);
    sortedAccounts.add(silver4Account);

    assertArrayEquals(notSortedAccounts.toArray(new Account[0]),
        ranking.getAccounts().toArray(new Account[0]));
    ranking.sortByRank();
    assertArrayEquals(sortedAccounts.toArray(new Account[0]),
        ranking.getAccounts().toArray(new Account[0]));
  }
}
