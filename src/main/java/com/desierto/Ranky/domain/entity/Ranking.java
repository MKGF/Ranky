package com.desierto.Ranky.domain.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class Ranking {

  private String id;

  private List<Account> accounts;

  public Ranking(String id) {
    this.id = id;
    this.accounts = new ArrayList<>();
  }

  public void sortByRank() {
    accounts = accounts.stream().sorted(Account::compareTo).collect(Collectors.toList());
  }

  public boolean addAccount(Account account) {
    return this.accounts.add(account);
  }
}
