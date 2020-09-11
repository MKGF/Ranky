package com.desierto.LoLRankingMaker.application.service;

import com.desierto.LoLRankingMaker.application.service.dto.AccountInformationDTO;
import com.desierto.LoLRankingMaker.domain.entity.Account;
import com.desierto.LoLRankingMaker.domain.repository.RiotAccountRepository;
import lombok.AllArgsConstructor;
import net.rithms.riot.api.RiotApiException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GetAccountInformationService {

  private final RiotAccountRepository riotAccountRepository;

  public AccountInformationDTO execute(long accountId) throws RiotApiException {
    return AccountInformationDTO.fromDomain(
        riotAccountRepository.getAccountInformation(Account.builder().name("PensiGoSu").build())
    );
  }
}
