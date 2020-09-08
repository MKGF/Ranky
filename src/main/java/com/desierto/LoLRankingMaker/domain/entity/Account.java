package com.desierto.LoLRankingMaker.domain.entity;


import com.desierto.LoLRankingMaker.domain.valueobject.Rank;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
@EqualsAndHashCode
@Entity
public class Account implements Comparable<Account> {

  @Id
  @GeneratedValue
  private int id;
  @Column(nullable = false)
  private String name;
  @Embedded
  private Rank rank;

  @Override
  public int compareTo(Account account) {
    return this.rank.compareTo(account.rank);
  }

  public void setRank(Rank rank) {
    this.rank = rank;
  }
}
