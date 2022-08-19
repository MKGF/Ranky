package com.desierto.Ranky.domain.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class Ranking {

  private long id;

  private List<Account> accounts = new ArrayList<>();

  public void sortByRank() {
    accounts = accounts.stream().sorted(Account::compareTo).collect(Collectors.toList());
  }

  public boolean addAccount(Account account) {
    return this.accounts.add(account);
  }
}
