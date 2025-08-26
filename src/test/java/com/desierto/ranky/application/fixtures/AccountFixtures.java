package com.desierto.ranky.application.fixtures;

import com.desierto.ranky.domain.entity.Account;
import com.desierto.ranky.domain.valueobject.Division;
import com.desierto.ranky.domain.valueobject.Rank;
import com.desierto.ranky.domain.valueobject.Rank.Tier;
import com.desierto.ranky.domain.valueobject.Winrate;

public class AccountFixtures {

  public static Account anAccount() {
    Account account = new Account("id", "name", "tagLine");
    account.updateRank(
        new Rank(Tier.EMERALD, Division.II, 13, Winrate.builder().wins(15).losses(11).build()));
    return account;
  }

  public static Account anotherAccount() {
    Account account = new Account("anotherId", "anotherName", "anotherTagLine");
    account.updateRank(
        new Rank(Tier.DIAMOND, Division.I, 1, Winrate.builder().wins(176).losses(173).build()));
    return account;
  }

  public static Account getDifferentWithSameName(Account account) {
    Account different = new Account(account.getId() + "different", account.getName(),
        account.getTagLine() + "different");
    different.updateRank(
        new Rank(Tier.DIAMOND, Division.I, 1, Winrate.builder().wins(176).losses(173).build()));
    return different;
  }
}
