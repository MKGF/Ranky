package com.desierto.Ranky.infrastructure.repository;

import com.desierto.Ranky.domain.entity.Account;
import com.desierto.Ranky.domain.repository.RiotAccountRepository;
import com.desierto.Ranky.domain.valueobject.Division;
import com.desierto.Ranky.domain.valueobject.Rank;
import com.desierto.Ranky.domain.valueobject.Rank.Tier;
import com.desierto.Ranky.domain.valueobject.Winrate;
import com.desierto.Ranky.infrastructure.configuration.ConfigLoader;
import com.desierto.Ranky.infrastructure.dto.GameNameDTO;
import com.google.gson.Gson;
import com.merakianalytics.orianna.Orianna;
import com.merakianalytics.orianna.types.common.Queue;
import com.merakianalytics.orianna.types.common.Region;
import com.merakianalytics.orianna.types.core.account.Account.Builder;
import com.merakianalytics.orianna.types.core.league.LeagueEntry;
import com.merakianalytics.orianna.types.core.summoner.Summoner;
import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class RestRiotAccountRepository implements RiotAccountRepository {

  private final ConfigLoader configLoader;

  private final Gson gson;

  @PostConstruct
  public void setUp() {
    Orianna.setRiotAPIKey(configLoader.getRiotApiKey());
    Orianna.setDefaultRegion(Region.EUROPE_WEST);
  }

  @Override
  public Account enrichIdentification(Account account) {
    try {
      Builder builder = Orianna.accountWithRiotId(
          account.getName(), account.getTagLine());
      String puuid = builder.get().getPuuid();
      GameNameDTO gameName = gson.fromJson(builder.get().toJSON(), GameNameDTO.class);
      return new Account(puuid, gameName.getGameName(), gameName.getTagLine());
    } catch (IllegalStateException e) {
      return new Account(account.getName(), account.getTagLine());
    }
  }

  @Override
  public List<Account> enrichWithSoloQStats(List<Account> accounts) {
    accounts.forEach(account -> {
      Summoner summoner = Orianna.summonerWithPuuid(account.getId()).get();
      Builder accountBuilder = Orianna.accountWithPuuid(account.getId());
      //We need to do this JSON parse because when we try to retrieve the coreData object from the Orianna.Account
      //we get the string we sent in the beginning, which might not be properly cased
      //It looks like a bug in Orianna, this is a workaround since parsing it to a string returns the correct name/tagLine coming from Riot
      GameNameDTO gameName = gson.fromJson(accountBuilder.get().toJSON(), GameNameDTO.class);
      LeagueEntry leagueEntry = summoner.getLeaguePosition(Queue.RANKED_SOLO);
      account.updateRank(
          leagueEntry != null ?
              new Rank(
                  Tier.fromString(leagueEntry.getTier().name()),
                  Division.valueOf(leagueEntry.getDivision().name()),
                  leagueEntry.getLeaguePoints(),
                  new Winrate(leagueEntry.getWins(), leagueEntry.getLosses())
              ) : Rank.unranked()
      );
      account.updateGameName(gameName.getGameName(), gameName.getTagLine());
    });
    return accounts;
  }

}
