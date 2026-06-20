package com.desierto.ranky.application.service;

import com.desierto.ranky.application.AccountsCache;
import com.desierto.ranky.domain.entity.Account;
import com.desierto.ranky.domain.entity.Ranking;
import com.desierto.ranky.domain.exception.account.AccountCouldNotBeDesambiguatedException;
import com.desierto.ranky.domain.repository.RankingRepository;
import com.desierto.ranky.domain.repository.RiotAccountRepository;
import com.desierto.ranky.domain.service.IAccountsService;
import com.desierto.ranky.domain.valueobject.RankedMode;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class AccountsService implements IAccountsService {

  @Autowired
  private RankingRepository rankingRepository;

  @Autowired
  private AccountsCache accountsCache;

  @Autowired
  private RiotAccountRepository riotAccountRepository;

  @Override
  public Ranking addAccounts(String rankingId, Guild guild, List<Account> accounts) {
    Ranking ranking = rankingRepository.read(rankingId, guild);
    List<Account> accountsWithId = accounts.stream().map(this::enrich).toList();
    accountsWithId.stream().filter(account -> !account.getId().isEmpty()).forEach(
        ranking::addAccount);
    List<Account> enrichedSoloQAccounts = new ArrayList<>();
    List<Account> enrichedFlexQAccounts = new ArrayList<>();
    if (accountsCache.containsSoloQRanking(rankingId, guild.getId())) {
      enrichedSoloQAccounts.addAll(accountsWithId.stream().map(
          account -> riotAccountRepository.enrichWithRankedStats(account,
              RankedMode.RANKED_SOLO_5x5)).toList());
    }
    if (accountsCache.containsFlexQRanking(rankingId, guild.getId())) {
      enrichedFlexQAccounts.addAll(accountsWithId.stream().map(
          account -> riotAccountRepository.enrichWithRankedStats(account,
              RankedMode.RANKED_FLEX_SR)).toList());
    }
    accountsCache.addAccountsIfRankingCached(guild.getId(), rankingId, enrichedSoloQAccounts,
        enrichedFlexQAccounts);

    return rankingRepository.update(ranking, guild);
  }

  @Override
  public Ranking removeAccounts(String rankingId, Guild guild, List<Account> accounts) {
    Ranking ranking = rankingRepository.read(rankingId, guild);
    List<Account> enrichedAccounts = ranking.getAccounts().stream().map(this::enrich).toList();
    accounts.forEach(account -> {
      List<Account> matches = enrichedAccounts.stream()
          .filter(account1 -> account1.isSameAccount(account))
          .toList();
      if (matches.size() > 1) {
        throw new AccountCouldNotBeDesambiguatedException(matches.get(0));
      } else if (matches.size() == 1) {
        ranking.removeAccount(matches.get(0));
      }
    });
    if (accountsCache.containsSoloQRanking(rankingId, guild.getId())) {
      accountsCache.removeAccountsIfRankingCached(guild.getId(), rankingId, accounts);
    }
    return rankingRepository.update(ranking, guild);
  }

  private Account enrich(Account account) {
    log.debug("INTO ENRICHMENT WITH ACCOUNT: " + account.getNameAndTagLine());
    return riotAccountRepository.enrichIdentification(account);
  }
}
