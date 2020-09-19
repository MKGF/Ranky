package com.desierto.LoLRankingMaker.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.when;

import com.desierto.LoLRankingMaker.domain.aggregate.RiotAccountAggregate;
import com.desierto.LoLRankingMaker.domain.builder.RankBuilder;
import com.desierto.LoLRankingMaker.domain.entity.Account;
import com.desierto.LoLRankingMaker.domain.repository.RiotAccountRepository;
import com.desierto.LoLRankingMaker.domain.valueobject.AccountInformation;
import com.desierto.LoLRankingMaker.domain.valueobject.Rank;
import com.desierto.LoLRankingMaker.domain.valueobject.Rank.Tier;
import com.desierto.LoLRankingMaker.domain.valueobject.Winrate;
import com.desierto.LoLRankingMaker.infrastructure.configuration.Configuration;
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
@ContextConfiguration(classes = {Configuration.class})
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
    assertEquals("tiene que ser menor o igual que 100",
        violations.stream().findAny().get().getMessage());
  }
}
