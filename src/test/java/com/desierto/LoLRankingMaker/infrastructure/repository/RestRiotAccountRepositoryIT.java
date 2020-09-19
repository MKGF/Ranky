package com.desierto.LoLRankingMaker.infrastructure.repository;

import static com.desierto.LoLRankingMaker.domain.enumerates.QueueType.SOLOQ;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.desierto.LoLRankingMaker.domain.builder.RankBuilder;
import com.desierto.LoLRankingMaker.domain.entity.Account;
import com.desierto.LoLRankingMaker.domain.valueobject.AccountInformation;
import com.desierto.LoLRankingMaker.domain.valueobject.Rank.Tier;
import com.desierto.LoLRankingMaker.domain.valueobject.Winrate;
import com.desierto.LoLRankingMaker.infrastructure.BaseIT;
import com.desierto.LoLRankingMaker.infrastructure.configuration.ConfigLoader;
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
          restRiotAccountRepository.getAccount(name).get());
    }
  }
}
