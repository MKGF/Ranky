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
}
