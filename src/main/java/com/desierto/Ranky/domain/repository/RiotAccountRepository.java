package com.desierto.Ranky.domain.repository;

import com.desierto.Ranky.domain.entity.Account;
import com.desierto.Ranky.domain.valueobject.Rank;

public interface RiotAccountRepository {

  Account enrichWithId(Account account);

  Rank getSoloQRankOfAccount(Account account);
}
