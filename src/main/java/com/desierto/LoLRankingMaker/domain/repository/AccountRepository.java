package com.desierto.LoLRankingMaker.domain.repository;

import com.desierto.LoLRankingMaker.domain.entity.Account;
import java.util.Optional;

public interface AccountRepository {

  Optional<Account> findById(Long accountId);

  Account save(Account account);
}
