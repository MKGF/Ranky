package com.desierto.LoLRankingMaker.infrastructure.repository;

import com.desierto.LoLRankingMaker.domain.entity.Account;
import com.desierto.LoLRankingMaker.domain.repository.AccountRepository;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface CrudAccountRepository extends CrudRepository<Account, Long>, AccountRepository {

  Optional<Account> findById(Long accountId);

  Account save(Account account);
}
