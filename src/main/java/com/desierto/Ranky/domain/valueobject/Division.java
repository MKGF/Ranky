package com.desierto.Ranky.domain.valueobject;

import java.util.Comparator;

public enum Division {
  I(4, "Ⅰ"),
  II(3, "Ⅱ"),
  III(2, "Ⅲ"),
  IV(1, "Ⅳ");

  private final int level;

  private final String romanNumber;

  private Division(int level, String romanNumber) {
    this.level = level;
    this.romanNumber = romanNumber;
  }

  public static Comparator<Division> getComparator() {
    return new Comparator<Division>() {
      public int compare(
          Division o1, Division o2) {
        return Integer.compare(o1.level, o2.level);
      }
    };
  }

  public int compare(Division o) {
    return Integer.compare(this.level, o.level);
  }

  @Override
  public String toString() {
    return this.romanNumber;
  }
}