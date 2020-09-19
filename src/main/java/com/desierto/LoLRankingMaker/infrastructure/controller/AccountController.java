package com.desierto.LoLRankingMaker.infrastructure.controller;

import com.desierto.LoLRankingMaker.application.service.CreateAccountService;
import com.desierto.LoLRankingMaker.application.service.dto.AccountDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
@AllArgsConstructor
@Slf4j
public class AccountController {

  private final CreateAccountService createAccountService;

  @PostMapping("/{name}")
  public AccountDTO getAccountInformation(@PathVariable String name) {
    AccountDTO accountDTO = createAccountService.execute(name);
    System.out.println(accountDTO.toString());
    return accountDTO;
  }
}
