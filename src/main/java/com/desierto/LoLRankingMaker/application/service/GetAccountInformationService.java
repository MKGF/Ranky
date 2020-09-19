package com.desierto.LoLRankingMaker.application.service;

import com.desierto.LoLRankingMaker.application.service.dto.AccountInformationDTO;
import com.desierto.LoLRankingMaker.domain.entity.Account;
import com.desierto.LoLRankingMaker.domain.exception.account.AccountNotFoundException;
import com.desierto.LoLRankingMaker.domain.repository.AccountRepository;
import com.desierto.LoLRankingMaker.domain.repository.RiotAccountRepository;
import com.desierto.LoLRankingMaker.domain.valueobject.AccountInformation;
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
