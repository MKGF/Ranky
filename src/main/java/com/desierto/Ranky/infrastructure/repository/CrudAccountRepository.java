package com.desierto.Ranky.infrastructure.repository;

import com.desierto.Ranky.domain.entity.Account;
import com.desierto.Ranky.domain.repository.AccountRepository;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface CrudAccountRepository extends CrudRepository<Account, Long>, AccountRepository {

  Optional<Account> findById(Long accountId);

  Account save(Account account);
}
