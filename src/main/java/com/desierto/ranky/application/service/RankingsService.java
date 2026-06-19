package com.desierto.ranky.application.service;

import static com.desierto.ranky.domain.valueobject.RankedMode.RANKED_SOLO_5x5;

import com.desierto.ranky.application.AccountsCache;
import com.desierto.ranky.domain.entity.Account;
import com.desierto.ranky.domain.entity.Ranking;
import com.desierto.ranky.domain.repository.RankingRepository;
import com.desierto.ranky.domain.repository.RiotAccountRepository;
import com.desierto.ranky.domain.service.IRankingsService;
import java.util.List;
import java.util.Optional;
import net.dv8tion.jda.api.entities.Guild;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RankingsService implements IRankingsService {

  private final RankingRepository rankingRepository;

  private final RiotAccountRepository riotAccountRepository;

  private final AccountsCache accountsCache;

  @Autowired
  public RankingsService(RankingRepository rankingRepository,
      RiotAccountRepository riotAccountRepository, AccountsCache accountsCache) {
    this.rankingRepository = rankingRepository;
    this.riotAccountRepository = riotAccountRepository;
    this.accountsCache = accountsCache;
  }

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
      List<Account> enrichedAccounts = riotAccountRepository.enrichAccountsWithRankedStats(
          ranking.getAccounts(), RANKED_SOLO_5x5);
      ranking.setAccounts(enrichedAccounts);
      accountsCache.save(guild.getId(), rankingId, enrichedAccounts);
    } else {
      ranking.setAccounts(cachedAccounts.get());
    }
    return ranking;
  }
}
