package com.desierto.LoLRankingMaker.application.service;

import com.desierto.LoLRankingMaker.application.service.dto.AccountDTO;
import com.desierto.LoLRankingMaker.domain.exception.account.AccountNotFoundException;
import com.desierto.LoLRankingMaker.domain.repository.AccountRepository;
import com.desierto.LoLRankingMaker.domain.repository.RiotAccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class CreateAccountService {

  private final AccountRepository accountRepository;
  private final RiotAccountRepository riotAccountRepository;

  public AccountDTO execute(String name) {
    return AccountDTO.fromDomain(accountRepository.save(riotAccountRepository.getAccount(name)
        .orElseThrow(() -> new AccountNotFoundException(name))));
  }
}
