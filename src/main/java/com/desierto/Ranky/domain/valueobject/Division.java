package com.desierto.Ranky.domain.valueobject;

import java.util.Comparator;

public enum Division {
  I(4),
  II(3),
  III(2),
  IV(1);

  private final int level;

  private Division(int level) {
    this.level = level;
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
}