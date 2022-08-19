package com.desierto.Ranky.infrastructure.repository;

import static com.desierto.Ranky.domain.enumerates.QueueType.SOLOQ;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.desierto.Ranky.domain.builder.RankBuilder;
import com.desierto.Ranky.domain.entity.Account;
import com.desierto.Ranky.domain.valueobject.AccountInformation;
import com.desierto.Ranky.domain.valueobject.Rank.Tier;
import com.desierto.Ranky.domain.valueobject.Winrate;
import com.desierto.Ranky.infrastructure.BaseIT;
import com.desierto.Ranky.infrastructure.configuration.ConfigLoader;
import com.merakianalytics.orianna.Orianna;
import com.merakianalytics.orianna.types.core.league.LeagueEntry;
import com.merakianalytics.orianna.types.core.league.LeaguePositions;
import com.merakianalytics.orianna.types.core.summoner.Summoner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class RestRiotAccountRepositoryIT extends BaseIT {

  @Autowired
  private ConfigLoader configLoader;

  private RestRiotAccountRepository restRiotAccountRepository;

  @BeforeEach
  public void set_up() {
    restRiotAccountRepository = new RestRiotAccountRepository(configLoader);
  }

  @Test
  public void gets_account() {
    String name = "pensigosu";
    Summoner summoner = Orianna.summonerNamed(name).get();
    if (summoner.exists()) {
      LeaguePositions leaguePositions = Orianna.leaguePositionsForSummoner(
          Orianna.summonerNamed(name).get()
      )
          .get();
      LeagueEntry soloQEntry = leaguePositions.stream()
          .filter(leagueEntry -> leagueEntry.getQueue().getTag().equalsIgnoreCase(SOLOQ.getValue()))
          .findAny().get();
      Account expectedAccount = Account.builder()
          .name(soloQEntry.getSummoner().getName())
          .accountInformation(
              AccountInformation.builder()
                  .rank(
                      new RankBuilder()
                          .division(soloQEntry.getDivision().ordinal() + 1)
                          .tier(Tier.valueOf(soloQEntry.getTier().name()))
                          .build()
                  )
                  .winrate(
                      Winrate.builder()
                          .wins(soloQEntry.getWins())
                          .losses(soloQEntry.getLosses())
                          .build()
                  )
                  .leaguePoints(soloQEntry.getLeaguePoints())
                  .queueType(SOLOQ.getValue())
                  .build()
          )
          .build();

      assertEquals(expectedAccount,
          restRiotAccountRepository.getAccountByName(name).get());
    }
  }
}
