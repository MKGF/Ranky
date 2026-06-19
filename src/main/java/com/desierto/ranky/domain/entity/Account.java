package com.desierto.ranky.domain.entity;


import com.desierto.ranky.domain.valueobject.Rank;
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

  public Account(String id) {
    this.id = id;
  }

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
    return this.name + (this.tagLine == null || !this.tagLine.isEmpty() ? "#" + this.tagLine : "");
  }

  @Override
  public int compareTo(Account other) {
    return this.rank.compareTo(other.rank);
  }

  public boolean isNotEmpty() {
    return name != null && !name.isEmpty();
  }

  public void updateGameName(String gameName, String tagLine) {
    this.name = gameName;
    this.tagLine = tagLine;
  }

  public boolean isSameAccount(Account other) {
    if ((this.onlyHasName() || other.onlyHasName())) {
      return this.name.equalsIgnoreCase(other.name);
    }
    return this.getNameAndTagLine().equalsIgnoreCase(other.getNameAndTagLine());
  }

  private boolean onlyHasName() {
    return !this.name.isEmpty() && this.tagLine.isEmpty();
  }

  public boolean lacksId() {
    return this.id == null || this.id.isEmpty();
  }
}
