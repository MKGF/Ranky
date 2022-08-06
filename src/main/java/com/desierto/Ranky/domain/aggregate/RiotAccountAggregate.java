package com.desierto.Ranky.domain.aggregate;

import com.desierto.Ranky.domain.entity.Account;
import com.desierto.Ranky.domain.repository.RiotAccountRepository;
import com.desierto.Ranky.domain.valueobject.AccountInformation;
import java.util.List;
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
  public List<AccountInformation> getAccountInformation() {
    return riotAccountRepository.getAccountInformation(account);
  }
}
