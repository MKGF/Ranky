package com.desierto.Ranky.domain.repository;

import com.desierto.Ranky.domain.entity.Account;

public interface RiotAccountRepository {

  Account enrichIdentification(Account account);

  Account enrichWithSoloQStats(Account account);
}
