package com.desierto.LoLRankingMaker.domain.aggregate;

import com.desierto.LoLRankingMaker.domain.entity.Account;
import com.desierto.LoLRankingMaker.domain.repository.RiotAccountRepository;
import com.desierto.LoLRankingMaker.domain.valueobject.AccountInformation;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class RiotAccountAggregate {

  private Account account;
  private RiotAccountRepository riotAccountRepository;

  @Valid
  public AccountInformation getAccountInformation() {
    return riotAccountRepository.getAccountInformation(account);
  }
}
