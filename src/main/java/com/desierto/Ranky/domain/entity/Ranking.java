package com.desierto.Ranky.domain.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class Ranking {

  private String id;

  private List<Account> accounts;

  public Ranking(String id) {
    this.id = id;
    this.accounts = new ArrayList<>();
  }

  public Ranking(String id, List<Account> accounts) {
    this.id = id;
    this.accounts = new ArrayList<>(accounts);
  }

  public void sortByRank() {
    accounts = accounts.stream().sorted(Account::compareTo).collect(Collectors.toList());
  }

  public boolean addAccount(Account account) {
    return this.accounts.add(account);
  }

  public boolean removeAccount(Account account) {
    return this.accounts.remove(account);
  }
}
