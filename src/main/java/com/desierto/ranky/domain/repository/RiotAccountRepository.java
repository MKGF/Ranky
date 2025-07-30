package com.desierto.ranky.domain.repository;

import com.desierto.ranky.domain.entity.Account;
import com.desierto.ranky.domain.valueobject.RankedMode;

public interface RiotAccountRepository {

  Account enrichIdentification(Account account);

  Account enrichWithRankedStats(Account account, RankedMode rankedMode);
}
