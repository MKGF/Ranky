package com.desierto.Ranky.domain.repository;

import com.desierto.Ranky.domain.entity.Account;
import java.util.List;

public interface RiotAccountRepository {

  Account enrichWithId(Account account);

  List<Account> enrichWithSoloQStats(List<Account> account);
}
