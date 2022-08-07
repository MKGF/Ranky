package com.desierto.Ranky.domain.valueobject;

import java.util.ArrayList;
import java.util.List;

public class RankingConfiguration {

  String name;

  List<AccountInformation> accounts;

  public RankingConfiguration(String name) {
    this.name = name;
    this.accounts = new ArrayList<>();
  }
}
