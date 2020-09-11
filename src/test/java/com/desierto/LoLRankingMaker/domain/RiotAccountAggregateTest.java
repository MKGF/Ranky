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
import java.math.BigDecimal;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import net.rithms.riot.api.RiotApiException;
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
  public void givenAnAccount_shouldReturnAccountInformation() throws RiotApiException {
    Rank gold2 = new RankBuilder().division(2).tier(Tier.GOLD).build();
    Account account = Account.builder().name("MAIKY")
        .rank(gold2).build();
    RiotAccountAggregate riotAccountAggregate = RiotAccountAggregate.builder().account(account)
        .riotAccountRepository(riotAccountRepository).build();
    AccountInformation expectedAccountInformation = AccountInformation.builder().leaguePoints(80)
        .rank(gold2).winrate(
            Winrate.builder().wins(BigDecimal.valueOf(130)).losses(BigDecimal.valueOf(126)).build())
        .build();

    when(riotAccountRepository.getAccountInformation(account))
        .thenReturn(expectedAccountInformation);

    assertEquals(expectedAccountInformation, riotAccountAggregate.getAccountInformation());
  }

  @Test
  public void whenRequestingAccountInformation_triggersValidation() throws RiotApiException {
    Rank gold2 = new RankBuilder().division(2).tier(Tier.GOLD).build();
    Account account = Account.builder().name("MAIKY")
        .rank(gold2).build();
    AccountInformation expectedAccountInformation = AccountInformation.builder().leaguePoints(800)
        .rank(gold2).winrate(
            Winrate.builder().wins(BigDecimal.valueOf(130)).losses(BigDecimal.valueOf(126)).build())
        .build();

    when(riotAccountRepository.getAccountInformation(account))
        .thenReturn(expectedAccountInformation);

    Set<ConstraintViolation<AccountInformation>> violations = validator
        .validate(riotAccountRepository.getAccountInformation(account));

    assertNotEquals(0, violations.size());
    assertEquals("tiene que ser menor o igual que 100",
        violations.stream().findAny().get().getMessage());
  }
}
