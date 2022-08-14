package com.desierto.Ranky.domain.entity;


import com.desierto.Ranky.domain.valueobject.AccountInformation;
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
    if (this.accountInformation.getRank().compareTo(account.accountInformation.getRank()) != 0) {
      return this.accountInformation.getRank().compareTo(account.accountInformation.getRank());
    }

    return Integer.compare(this.accountInformation.getLeaguePoints(),
        account.accountInformation.getLeaguePoints()) * -1;
  }

  public String getFormattedForRanking() {
    return name + " | " + accountInformation.getRank().toString() + " | " + accountInformation
        .getLeaguePoints()
        + "LP | " + accountInformation.getWinrate().getWins() + "W/" + accountInformation
        .getWinrate()
        .getLosses() + "L | " + accountInformation.getWinrate().getPercentage().toString();
  }
}
