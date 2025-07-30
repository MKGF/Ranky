package com.desierto.ranky.application.service;

import static com.desierto.ranky.domain.valueobject.RankedMode.RANKED_SOLO_5X5;

import com.desierto.ranky.application.AccountsCache;
import com.desierto.ranky.domain.entity.Account;
import com.desierto.ranky.domain.entity.Ranking;
import com.desierto.ranky.domain.repository.RankingRepository;
import com.desierto.ranky.domain.repository.RiotAccountRepository;
import com.desierto.ranky.domain.service.IGetRankingService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class GetRankingService implements IGetRankingService {

  @Autowired
  private RankingRepository rankingRepository;

  @Autowired
  private RiotAccountRepository riotAccountRepository;

  @Autowired
  private AccountsCache accountsCache;

  @Override
  public List<Ranking> getAll(Guild guild) {
    return rankingRepository.findAll(guild);
  }

  @Override
  public Ranking get(String rankingId, Guild guild) {
    Ranking ranking = rankingRepository.read(rankingId, guild);
    Optional<List<Account>> cachedAccounts = accountsCache.find(
        guild.getId(), rankingId);
    if (cachedAccounts.isEmpty()) {
      List<Account> enrichedAccounts = ranking.getAccounts().stream()
          .map(account -> riotAccountRepository.enrichWithRankedStats(account, RANKED_SOLO_5X5))
          .collect(
              Collectors.toList());
      ranking.setAccounts(enrichedAccounts);
      accountsCache.save(guild.getId(), rankingId, enrichedAccounts);
    } else {
      ranking.setAccounts(cachedAccounts.get());
    }
    return ranking;
  }
}
