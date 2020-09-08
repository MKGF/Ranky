package domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
@EqualsAndHashCode
public class Account implements Comparable<Account> {

  private int id;
  private String name;
  private String leagueOfLegendsId;
  private Rank rank;

  @Override
  public int compareTo(Account account) {
    return this.rank.compareTo(account.rank);
  }
}
