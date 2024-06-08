package com.desierto.Ranky.domain.entity;


import com.desierto.Ranky.domain.valueobject.Rank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Builder
@EqualsAndHashCode
@ToString
public class Account implements Comparable<Account> {

  private String id;
  private String name;

  private String tagLine;

  private Rank rank;

  public Account(String name, String tagLine) {
    this.id = "";
    this.name = name;
    this.tagLine = tagLine;
  }

  public Account(String id, String name, String tagLine) {
    this.id = id;
    this.name = name;
    this.tagLine = tagLine;
  }

  public Account() {

  }

  public void updateRank(Rank rank) {
    this.rank = rank;
  }

  public String getNameAndTagLine() {
    return this.name + "#" + this.tagLine;
  }

  @Override
  public int compareTo(Account other) {
    return this.rank.compareTo(other.rank);
  }

  public String getFormattedForRanking(int index) {
    return index + " - " + name + " | "
        + this.getRank().toString();
  }

  public boolean isNotEmpty() {
    return name != null && !name.isEmpty() && tagLine != null && !tagLine.isEmpty();
  }
}
