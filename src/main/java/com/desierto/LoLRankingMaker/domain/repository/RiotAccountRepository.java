package com.desierto.LoLRankingMaker.domain.repository;

import com.desierto.LoLRankingMaker.domain.entity.Account;
import com.desierto.LoLRankingMaker.domain.valueobject.AccountInformation;

public interface RiotAccountRepository {

  AccountInformation getAccountInformation(Account account);
}
