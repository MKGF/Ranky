package com.desierto.LoLRankingMaker.domain.aggregate;

import com.desierto.LoLRankingMaker.domain.entity.Account;
import com.desierto.LoLRankingMaker.domain.repository.RiotAccountRepository;
import com.desierto.LoLRankingMaker.domain.valueobject.AccountInformation;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import net.rithms.riot.api.RiotApiException;

@AllArgsConstructor
@Getter
@Builder
public class RiotAccountAggregate {

  private Account account;
  private RiotAccountRepository riotAccountRepository;

  @Valid
  public AccountInformation getAccountInformation() throws RiotApiException {
    return riotAccountRepository.getAccountInformation(account);
  }
}
