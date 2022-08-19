package com.desierto.Ranky.infrastructure.repository;

import static com.desierto.Ranky.domain.enumerates.QueueType.SOLOQ;

import com.desierto.Ranky.domain.builder.RankBuilder;
import com.desierto.Ranky.domain.entity.Account;
import com.desierto.Ranky.domain.repository.RiotAccountRepository;
import com.desierto.Ranky.domain.valueobject.AccountInformation;
import com.desierto.Ranky.domain.valueobject.Rank;
import com.desierto.Ranky.domain.valueobject.Rank.Tier;
import com.desierto.Ranky.domain.valueobject.Winrate;
import com.desierto.Ranky.infrastructure.configuration.ConfigLoader;
import com.merakianalytics.orianna.Orianna;
import com.merakianalytics.orianna.types.common.Region;
import com.merakianalytics.orianna.types.core.league.LeaguePositions;
import com.merakianalytics.orianna.types.core.summoner.Summoner;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class RestRiotAccountRepository implements RiotAccountRepository {

  private final ConfigLoader configLoader;

  @PostConstruct
  public void setUp() {
    Orianna.setRiotAPIKey(configLoader.getApiKey());
    Orianna.setDefaultRegion(Region.EUROPE_WEST);
  }

  @Override
  public List<AccountInformation> getAccountInformation(Account account) {
    LeaguePositions leaguePositions = Orianna.leaguePositionsForSummoner(
        Orianna.summonerNamed(account.getName()).get()
    )
        .get();

    return leaguePositions.stream().map(leagueEntry ->
        AccountInformation.builder()
            .rank(new RankBuilder()
                .division(leagueEntry.getDivision().ordinal())
                .tier(Tier.valueOf(leagueEntry.getTier().name()))
                .build()
            )
            .winrate(Winrate.builder()
                .wins(leagueEntry.getWins())
                .losses(leagueEntry.getLosses())
                .build()
            )
            .leaguePoints(leagueEntry.getLeaguePoints())
            .queueType(leagueEntry.getLeague().getQueue().getTag())
            .build()
    ).collect(Collectors.toList());
  }

  @Override
  public Optional<Account> getAccountByName(String name) {
    Summoner summoner = Orianna.summonerNamed(name).get();
    if (summoner.exists()) {
      LeaguePositions leaguePositions = Orianna.leaguePositionsForSummoner(
          summoner
      )
          .get();

      AccountInformation soloQ = leaguePositions.stream()
          .filter(leagueEntry -> leagueEntry.getQueue() != null && leagueEntry.getQueue().getTag()
              .equalsIgnoreCase(SOLOQ.getValue()))
          .map(leagueEntry -> AccountInformation.builder()
              .rank(new RankBuilder()
                  .division(leagueEntry.getDivision().ordinal() + 1)
                  .tier(Tier.valueOf(leagueEntry.getTier().name()))
                  .build()
              )
              .winrate(Winrate.builder()
                  .wins(leagueEntry.getWins())
                  .losses(leagueEntry.getLosses())
                  .build()
              )
              .leaguePoints(leagueEntry.getLeaguePoints())
              .queueType(leagueEntry.getLeague().getQueue().getTag())
              .build()).findAny().orElse(
              AccountInformation.builder().rank(Rank.unranked()).leaguePoints(0)
                  .queueType(SOLOQ.getValue())
                  .winrate(Winrate.unranked()).build()
          );
      return Optional.of(Account.builder()
          .id(summoner.getAccountId())
          .name(leaguePositions.getSummoner().getName())
          .accountInformation(soloQ)
          .build());
    } else {
      return Optional.empty();
    }
  }

  @Override
  public Optional<Account> getAccountById(String id) {
    Summoner summoner = Orianna.summonerWithId(id).get();
    if (summoner.exists()) {
      LeaguePositions leaguePositions = Orianna.leaguePositionsForSummoner(
          summoner
      )
          .get();

      AccountInformation soloQ = leaguePositions.stream()
          .filter(leagueEntry -> leagueEntry.getQueue() != null && leagueEntry.getQueue().getTag()
              .equalsIgnoreCase(SOLOQ.getValue()))
          .map(leagueEntry -> AccountInformation.builder()
              .rank(new RankBuilder()
                  .division(leagueEntry.getDivision().ordinal() + 1)
                  .tier(Tier.valueOf(leagueEntry.getTier().name()))
                  .build()
              )
              .winrate(Winrate.builder()
                  .wins(leagueEntry.getWins())
                  .losses(leagueEntry.getLosses())
                  .build()
              )
              .leaguePoints(leagueEntry.getLeaguePoints())
              .queueType(leagueEntry.getLeague().getQueue().getTag())
              .build()).findAny().orElse(
              AccountInformation.builder().rank(Rank.unranked()).leaguePoints(0)
                  .queueType(SOLOQ.getValue())
                  .winrate(Winrate.unranked()).build()
          );
      return Optional.of(Account.builder()
          .id(summoner.getAccountId())
          .name(leaguePositions.getSummoner().getName())
          .accountInformation(soloQ)
          .build());
    } else {
      return Optional.empty();
    }
  }


}
