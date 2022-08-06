package com.desierto.Ranky.domain.repository;

import com.desierto.Ranky.domain.entity.Account;
import java.util.Optional;

public interface AccountRepository {

  Optional<Account> findById(Long accountId);

  Account save(Account account);
}
