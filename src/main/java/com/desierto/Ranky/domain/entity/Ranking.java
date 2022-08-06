package com.desierto.Ranky.domain.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@Entity
public class Ranking {

  @Id
  @GeneratedValue
  private long id;

  @ElementCollection
  @CollectionTable(
      name = "ranking_accounts",
      joinColumns = @JoinColumn(name = "id")
  )
  private List<Account> accounts = new ArrayList<>();

  public void sortByRank() {
    accounts = accounts.stream().sorted(Account::compareTo).collect(Collectors.toList());
  }

  public boolean addAccount(Account account) {
    return this.accounts.add(account);
  }
}
