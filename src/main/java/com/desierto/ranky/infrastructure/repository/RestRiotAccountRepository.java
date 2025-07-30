package com.desierto.ranky.infrastructure.repository;

import com.desierto.ranky.domain.entity.Account;
import com.desierto.ranky.domain.repository.RiotAccountRepository;
import com.desierto.ranky.domain.valueobject.Division;
import com.desierto.ranky.domain.valueobject.Rank;
import com.desierto.ranky.domain.valueobject.Rank.Tier;
import com.desierto.ranky.domain.valueobject.Winrate;
import com.desierto.ranky.infrastructure.configuration.ConfigLoader;
import com.desierto.ranky.infrastructure.dto.GameNameDTO;
import com.google.gson.Gson;
import com.merakianalytics.orianna.Orianna;
import com.merakianalytics.orianna.types.common.Queue;
import com.merakianalytics.orianna.types.common.Region;
import com.merakianalytics.orianna.types.core.account.Account.Builder;
import com.merakianalytics.orianna.types.core.league.LeagueEntry;
import com.merakianalytics.orianna.types.core.summoner.Summoner;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
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
      if (puuid == null) {
        return new Account(account.getName(), account.getTagLine());
      }
      GameNameDTO gameName = gson.fromJson(builder.get().toJSON(), GameNameDTO.class);
      return new Account(puuid, gameName.getGameName(), gameName.getTagLine());
    } catch (IllegalStateException e) {
      return new Account(account.getName(), account.getTagLine());
    }
  }

  @Override
  public Account enrichWithSoloQStats(Account account) {

    Summoner summoner = Orianna.summonerWithPuuid(account.getId()).get();
    Builder accountBuilder = Orianna.accountWithPuuid(account.getId());
    //We need to do this JSON parse because when we try to retrieve the coreData object from the Orianna.Account
    //we get the string we sent in the beginning, which might not be properly cased
    //It looks like a bug in Orianna, this is a workaround since parsing it to a string returns the correct name/tagLine coming from Riot
    GameNameDTO gameName = gson.fromJson(accountBuilder.get().toJSON(), GameNameDTO.class);
    try {
      LeagueEntry leagueEntry = summoner.getLeaguePosition(Queue.RANKED_SOLO);
      log.debug("Got ranked stats for account: " + gameName.getWholeName());
      account.updateRank(
          leagueEntry != null ?
              new Rank(
                  Tier.fromString(leagueEntry.getTier().name()),
                  Division.valueOf(leagueEntry.getDivision().name()),
                  leagueEntry.getLeaguePoints(),
                  new Winrate(leagueEntry.getWins(), leagueEntry.getLosses())
              ) : Rank.unranked()
      );
    } catch (NullPointerException e) {
      log.info(
          String.format("Couldn't retrieve SoloQ rank of account %s#%s", gameName.getGameName(),
              gameName.getTagLine()));
      account.updateRank(Rank.unranked());
    }
    account.updateGameName(gameName.getGameName(), gameName.getTagLine());
    return account;
  }

}
