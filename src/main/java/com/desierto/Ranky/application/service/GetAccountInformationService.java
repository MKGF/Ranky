package com.desierto.Ranky.application.service;

import com.desierto.Ranky.application.service.dto.AccountInformationDTO;
import com.desierto.Ranky.domain.entity.Account;
import com.desierto.Ranky.domain.exception.account.AccountNotFoundException;
import com.desierto.Ranky.domain.repository.AccountRepository;
import com.desierto.Ranky.domain.repository.RiotAccountRepository;
import com.desierto.Ranky.domain.valueobject.AccountInformation;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GetAccountInformationService {

  private final AccountRepository accountRepository;

  private final RiotAccountRepository riotAccountRepository;

  public List<AccountInformationDTO> execute(long accountId) {
    Account account = accountRepository.findById(accountId)
        .orElseThrow(() -> new AccountNotFoundException(accountId));
    List<AccountInformation> accountInformations = riotAccountRepository
        .getAccountInformation(account);
    return accountInformations.stream().map(AccountInformationDTO::fromDomain)
        .collect(Collectors.toList());
  }
}
