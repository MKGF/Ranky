package com.desierto.ranky.domain.entity;

import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class Ranking {

  private String id;

  private List<Account> accounts;

  private Boolean isPublic;

  public Ranking(String id) {
    this.id = id;
    this.accounts = new ArrayList<>();
    this.isPublic = false;
  }

  public Ranking(String id, List<Account> accounts) {
    this.id = id;
    this.accounts = new ArrayList<>(accounts);
    this.isPublic = false;
  }

  public Ranking(String id, List<Account> accounts, Boolean isPublic) {
    this.id = id;
    this.accounts = new ArrayList<>(accounts);
    this.isPublic = isPublic;
  }

  public void addAccount(Account account) {
    if (accounts.stream().map(Account::getId)
        .noneMatch(id -> id.equalsIgnoreCase(account.getId()))) {
      this.accounts.add(account);
    }
  }

  public void removeAccount(Account accountToRemove) {
    this.accounts.removeIf(account -> account.getId().equals(accountToRemove.getId()));
  }

  public void makePublic() {
    this.isPublic = true;
  }
}
