package com.desierto.Ranky.infrastructure.repository;

import com.desierto.Ranky.domain.entity.Account;
import com.desierto.Ranky.domain.repository.RiotAccountRepository;
import com.desierto.Ranky.domain.valueobject.Division;
import com.desierto.Ranky.domain.valueobject.Rank;
import com.desierto.Ranky.domain.valueobject.Rank.Tier;
import com.desierto.Ranky.domain.valueobject.Winrate;
import com.desierto.Ranky.infrastructure.configuration.ConfigLoader;
import com.merakianalytics.orianna.Orianna;
import com.merakianalytics.orianna.types.common.Queue;
import com.merakianalytics.orianna.types.common.Region;
import com.merakianalytics.orianna.types.core.league.LeagueEntry;
import com.merakianalytics.orianna.types.core.summoner.Summoner;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class RestRiotAccountRepository implements RiotAccountRepository {

  private final ConfigLoader configLoader;

  @PostConstruct
  public void setUp() {
    Orianna.setRiotAPIKey(configLoader.getRiotApiKey());
    Orianna.setDefaultRegion(Region.EUROPE_WEST);
  }

  @Override
  public Account enrichWithId(Account account) {
    try {
      return new Account(Orianna.accountWithRiotId(
          account.getName(), account.getTagLine()).get().getPuuid(), account.getName(),
          account.getTagLine());
    } catch (IllegalStateException e) {
      return new Account(account.getName(), account.getTagLine());
    }
  }

  @Override
  public Rank getSoloQRankOfAccount(Account account) {
    Summoner summoner = Orianna.summonerWithPuuid(account.getId()).get();
    LeagueEntry leagueEntry = summoner.getLeaguePosition(Queue.RANKED_SOLO);

    return new Rank(
        Tier.fromString(leagueEntry.getTier().name()),
        Division.valueOf(leagueEntry.getDivision().name()),
        leagueEntry.getLeaguePoints(),
        new Winrate(leagueEntry.getWins(), leagueEntry.getLosses())
    );
  }

}
