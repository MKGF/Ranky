package com.desierto.ranky.domain.repository;

import com.desierto.ranky.domain.entity.Account;

public interface RiotAccountRepository {

  Account enrichIdentification(Account account);

  Account enrichWithSoloQStats(Account account);
}
