package com.desierto.LoLRankingMaker.infrastructure.repository;

import com.desierto.LoLRankingMaker.domain.builder.RankBuilder;
import com.desierto.LoLRankingMaker.domain.entity.Account;
import com.desierto.LoLRankingMaker.domain.exception.AccountHasNoLeaguesException;
import com.desierto.LoLRankingMaker.domain.repository.RiotAccountRepository;
import com.desierto.LoLRankingMaker.domain.valueobject.AccountInformation;
import com.desierto.LoLRankingMaker.domain.valueobject.Rank.Tier;
import com.desierto.LoLRankingMaker.domain.valueobject.Winrate;
import com.desierto.LoLRankingMaker.infrastructure.configuration.ConfigLoader;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import net.rithms.riot.api.ApiConfig;
import net.rithms.riot.api.RiotApi;
import net.rithms.riot.api.RiotApiException;
import net.rithms.riot.api.endpoints.league.dto.LeaguePosition;
import net.rithms.riot.api.endpoints.summoner.dto.Summoner;
import net.rithms.riot.constant.Platform;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class RestRiotAccountRepository implements RiotAccountRepository {

  private final ConfigLoader configLoader;

  @Override
  public AccountInformation getAccountInformation(Account account) throws RiotApiException {
    ApiConfig config = new ApiConfig().setKey(configLoader.getApiKey());
    RiotApi api = new RiotApi(config);
    Summoner summoner = api.getSummonerByName(Platform.EUW, account.getName());

    Set<LeaguePosition> leagues = api
        .getLeaguePositionsBySummonerId(Platform.EUW, summoner.getId());

    List<AccountInformation> accountInformationList = leagues.stream().map(
        leaguePosition -> AccountInformation.builder()
            .leaguePoints(leaguePosition.getLeaguePoints())
            .winrate(
                Winrate.builder()
                    .wins(BigDecimal.valueOf(leaguePosition.getWins()))
                    .losses(BigDecimal.valueOf(leaguePosition.getLosses()))
                    .build()
            )
            .rank(
                new RankBuilder()
                    .tier(Tier.valueOf(leaguePosition.getTier()))
                    .division(Integer.valueOf(leaguePosition.getRank()))
                    .build()
            )
            .build()
    ).collect(Collectors.toList());
    return accountInformationList.stream().findAny().orElseThrow(AccountHasNoLeaguesException::new);
  }
}
