package com.desierto.LoLRankingMaker.domain.entity;


import com.desierto.LoLRankingMaker.domain.valueobject.AccountInformation;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Builder
@EqualsAndHashCode
@Entity
@ToString
public class Account implements Comparable<Account> {

  @Id
  @GeneratedValue
  private int id;
  @Column(nullable = false)
  private String name;
  @Embedded
  private AccountInformation accountInformation;

  @Override
  public int compareTo(Account account) {
    return this.accountInformation.getRank().compareTo(account.accountInformation.getRank());
  }
}
