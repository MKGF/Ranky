package com.desierto.Ranky.domain.repository;

import com.desierto.Ranky.domain.entity.Account;
import com.desierto.Ranky.domain.valueobject.AccountInformation;
import java.util.List;
import java.util.Optional;

public interface RiotAccountRepository {

  List<AccountInformation> getAccountInformation(Account account);

  Optional<Account> getAccountByName(String name);

  Optional<Account> getAccountById(String id);

}
