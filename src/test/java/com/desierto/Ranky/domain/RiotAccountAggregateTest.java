package com.desierto.Ranky.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.when;

import com.desierto.Ranky.TestConfig;
import com.desierto.Ranky.domain.aggregate.RiotAccountAggregate;
import com.desierto.Ranky.domain.builder.RankBuilder;
import com.desierto.Ranky.domain.entity.Account;
import com.desierto.Ranky.domain.repository.RiotAccountRepository;
import com.desierto.Ranky.domain.valueobject.AccountInformation;
import com.desierto.Ranky.domain.valueobject.Rank;
import com.desierto.Ranky.domain.valueobject.Rank.Tier;
import com.desierto.Ranky.domain.valueobject.Winrate;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfig.class})
public class RiotAccountAggregateTest extends BaseTest {

  @Mock
  private RiotAccountRepository riotAccountRepository;


  @Autowired
  private Validator validator;

  @Test
  public void givenAnAccount_shouldReturnAccountInformation() {
    Rank gold2 = new RankBuilder().division(2).tier(Tier.GOLD).build();
    Account account = Account.builder().name("MAIKY").accountInformation(
        AccountInformation.builder().rank(gold2).leaguePoints(80).winrate(Winrate.builder().wins(
            130).losses(126).build()).build())
        .build();
    RiotAccountAggregate riotAccountAggregate = RiotAccountAggregate.builder().account(account)
        .riotAccountRepository(riotAccountRepository).build();
    List<AccountInformation> expectedAccountInformation = List
        .of(AccountInformation.builder().leaguePoints(80)
            .rank(gold2).winrate(
                Winrate.builder().wins(130).losses(126)
                    .build())
            .build());

    when(riotAccountRepository.getAccountInformation(account))
        .thenReturn(expectedAccountInformation);

    assertEquals(expectedAccountInformation, riotAccountAggregate.getAccountInformation());
  }

  @Test
  public void whenRequestingAccountInformation_triggersValidation() {
    Rank gold2 = new RankBuilder().division(2).tier(Tier.GOLD).build();
    Account account = Account.builder().name("MAIKY").accountInformation(
        AccountInformation.builder().rank(gold2).leaguePoints(800).winrate(
            Winrate.builder().wins(130).losses(126).build())
            .build())
        .build();
    List<AccountInformation> expectedAccountInformation = List
        .of(AccountInformation.builder().leaguePoints(800)
            .rank(gold2).winrate(
                Winrate.builder().wins(130).losses(126)
                    .build())
            .build());

    when(riotAccountRepository.getAccountInformation(account))
        .thenReturn(expectedAccountInformation);

    Set<ConstraintViolation<AccountInformation>> violations = new java.util.HashSet<>();
    List<AccountInformation> accountInformations = riotAccountRepository
        .getAccountInformation(account);
    accountInformations
        .forEach(accountInformation -> violations.addAll(validator.validate(accountInformation)));

    assertNotEquals(0, violations.size());
  }
}
