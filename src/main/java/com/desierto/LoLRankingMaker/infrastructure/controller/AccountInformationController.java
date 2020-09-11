package com.desierto.LoLRankingMaker.infrastructure.controller;

import com.desierto.LoLRankingMaker.application.service.GetAccountInformationService;
import com.desierto.LoLRankingMaker.application.service.dto.AccountInformationDTO;
import lombok.AllArgsConstructor;
import net.rithms.riot.api.RiotApiException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account-information")
@AllArgsConstructor
public class AccountInformationController {

  private final GetAccountInformationService getAccountInformationService;

  @GetMapping("/{accountId}")
  public AccountInformationDTO getAccountInformation(@PathVariable long accountId)
      throws RiotApiException {
    return getAccountInformationService.execute(accountId);
  }
}
