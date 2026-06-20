package com.desierto.ranky.domain.repository;

import com.desierto.ranky.domain.entity.Account;
import com.desierto.ranky.domain.valueobject.RankedMode;
import java.util.List;

public interface RiotAccountRepository {

  Account enrichIdentification(Account account);

  Account enrichWithRankedStats(Account account, RankedMode rankedMode);

  List<Account> enrichAccountsWithRankedStats(List<Account> accounts, RankedMode rankedMode);
}
