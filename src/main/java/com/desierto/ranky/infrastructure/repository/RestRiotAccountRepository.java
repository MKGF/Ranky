package com.desierto.ranky.infrastructure.repository;

import com.desierto.ranky.domain.entity.Account;
import com.desierto.ranky.domain.repository.RiotAccountRepository;
import com.desierto.ranky.domain.valueobject.Division;
import com.desierto.ranky.domain.valueobject.Rank;
import com.desierto.ranky.domain.valueobject.Rank.Tier;
import com.desierto.ranky.domain.valueobject.RankedMode;
import com.desierto.ranky.domain.valueobject.Winrate;
import com.desierto.ranky.infrastructure.clients.RiotAccountClient;
import com.desierto.ranky.infrastructure.clients.RiotLeagueClient;
import com.desierto.ranky.infrastructure.dto.riot.League;
import com.desierto.ranky.infrastructure.dto.riot.RiotAccount;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class RestRiotAccountRepository implements RiotAccountRepository {

  private final RiotAccountClient riotAccountClient;

  private final RiotLeagueClient riotLeagueClient;

  private RiotAccount getRiotAccount(String name, String tag) {
    return riotAccountClient.getAccountDto(name, tag);
  }

  @Override
  public Account enrichIdentification(Account account) {
    try {
      RiotAccount riotAccount = getRiotAccount(account.getName(), account.getTagLine());
      String puuid = riotAccount.puuid();
      if (puuid == null) {
        return new Account(account.getName(), account.getTagLine());
      }
      return new Account(puuid, riotAccount.gameName(), riotAccount.tagLine());
    } catch (IllegalStateException e) {
      return new Account(account.getName(), account.getTagLine());
    }
  }

  @Override
  public Account enrichWithRankedStats(Account account, RankedMode rankedMode) {
    List<League> leagues = riotLeagueClient.getLeaguesOfAccount(account.getId());
    try {
      League leagueEntry = leagues.stream()
          .filter(league -> league.queueType().equals(rankedMode.name())).findFirst()
          .orElseThrow(NullPointerException::new);
      log.debug("Got ranked stats for account: " + account.getNameAndTagLine());
      account.updateRank(
          leagueEntry != null ?
              new Rank(
                  Tier.fromString(leagueEntry.tier()),
                  Division.valueOf(leagueEntry.rank()),
                  leagueEntry.leaguePoints(),
                  new Winrate(leagueEntry.wins(), leagueEntry.losses())
              ) : Rank.unranked()
      );
    } catch (NullPointerException e) {
      log.info(
          String.format("Couldn't retrieve SoloQ rank of account %s", account.getNameAndTagLine())
      );
      account.updateRank(Rank.unranked());
    }
    RiotAccount riotAccount = getRiotAccount(account.getName(), account.getTagLine());
    account.updateGameName(riotAccount.gameName(), riotAccount.tagLine());
    return account;
  }

}
