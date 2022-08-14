package com.desierto.Ranky.domain;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import com.desierto.Ranky.domain.builder.RankBuilder;
import com.desierto.Ranky.domain.entity.Account;
import com.desierto.Ranky.domain.entity.Ranking;
import com.desierto.Ranky.domain.valueobject.AccountInformation;
import com.desierto.Ranky.domain.valueobject.Rank;
import com.desierto.Ranky.domain.valueobject.Rank.Tier;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

public class RankingTest {

  @Test
  public void ranking_with_gold1_silver3_and_silver4_is_sorted_by_rank() {
    RankBuilder rankBuilder = new RankBuilder();
    Rank gold1 = rankBuilder.tier(Tier.GOLD).division(1).build();
    Rank silver3 = rankBuilder.tier(Tier.SILVER).division(3).build();
    Rank silver4 = rankBuilder.tier(Tier.SILVER).division(4).build();
    Account gold1Account = Account.builder().id(1).name("MAIKY").accountInformation(
        AccountInformation.builder().rank(gold1).build())
        .build();
    Account silver3Account = Account.builder().id(2).name("naza30sec").accountInformation(
        AccountInformation.builder().rank(silver3).build())
        .build();
    Account silver4Account = Account.builder().id(3).name("AficionadoAlFallGuys")
        .accountInformation(
            AccountInformation.builder().rank(silver4).build())
        .build();
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

  @Test
  public void ranking_with_gold1_0lp_and_gold1_20lp_is_sorted_by_rank() {
    RankBuilder rankBuilder = new RankBuilder();
    Rank gold1 = rankBuilder.tier(Tier.GOLD).division(1).build();
    Account gold10lpAccount = Account.builder().id(1).name("MAIKY").accountInformation(
        AccountInformation.builder().rank(gold1).leaguePoints(0).build())
        .build();
    Account gold120lpAccount = Account.builder().id(2).name("naza30sec").accountInformation(
        AccountInformation.builder().rank(gold1).leaguePoints(20).build())
        .build();
    Ranking ranking = new Ranking();
    ranking.addAccount(gold10lpAccount);
    ranking.addAccount(gold120lpAccount);
    ArrayList<Account> notSortedAccounts = new ArrayList<>();
    notSortedAccounts.add(gold10lpAccount);
    notSortedAccounts.add(gold120lpAccount);
    ArrayList<Account> sortedAccounts = new ArrayList<>();
    sortedAccounts.add(gold120lpAccount);
    sortedAccounts.add(gold10lpAccount);
    gold10lpAccount.compareTo(gold120lpAccount);

    assertArrayEquals(notSortedAccounts.toArray(new Account[0]),
        ranking.getAccounts().toArray(new Account[0]));
    ranking.sortByRank();
    assertArrayEquals(sortedAccounts.toArray(new Account[0]),
        ranking.getAccounts().toArray(new Account[0]));
  }

}
