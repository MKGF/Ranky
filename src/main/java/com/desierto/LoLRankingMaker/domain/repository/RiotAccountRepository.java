package com.desierto.LoLRankingMaker.domain.repository;

import com.desierto.LoLRankingMaker.domain.entity.Account;
import com.desierto.LoLRankingMaker.domain.valueobject.AccountInformation;
import java.util.List;
import java.util.Optional;

public interface RiotAccountRepository {

  List<AccountInformation> getAccountInformation(Account account);

  Optional<Account> getAccount(String name);
}
