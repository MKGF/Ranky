package com.desierto.LoLRankingMaker.domain.entity;

public class Tables {

  public static final String ACCOUNT = "ACCOUNT";
  public static final String RANKING = "RANKING";
  public static final String RANKING_ACCOUNTS = "RANKING_ACCOUNTS";

  public static String[] getAll() {
    return new String[]{RANKING_ACCOUNTS, RANKING, ACCOUNT};
  }
}
