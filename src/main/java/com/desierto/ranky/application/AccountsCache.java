package com.desierto.ranky.application;

import static com.desierto.ranky.domain.valueobject.RankedMode.RANKED_FLEX_SR;

import com.desierto.ranky.domain.entity.Account;
import com.desierto.ranky.domain.exception.account.AccountCouldNotBeDesambiguatedException;
import com.desierto.ranky.domain.valueobject.RankedMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AccountsCache {

  private final int CACHE_MINUTES = 10;

  private final Map<String, List<Account>> rankingsSoloQ;

  private final Map<String, LocalDateTime> introductionTimesSoloQ;

  private final Map<String, List<Account>> rankingsFlexQ;

  private final Map<String, LocalDateTime> introductionTimesFlexQ;

  public AccountsCache() {
    rankingsSoloQ = new HashMap<>();
    introductionTimesSoloQ = new HashMap<>();
    rankingsFlexQ = new HashMap<>();
    introductionTimesFlexQ = new HashMap<>();
  }

  public AccountsCache(Map<String, List<Account>> rankingsSoloQParam,
      Map<String, LocalDateTime> introductionTimesSoloQParam,
      Map<String, List<Account>> rankingsFlexQParam,
      Map<String, LocalDateTime> introductionTimesFlexQParam) {
    rankingsSoloQ = rankingsSoloQParam;
    introductionTimesSoloQ = introductionTimesSoloQParam;
    rankingsFlexQ = rankingsFlexQParam;
    introductionTimesFlexQ = introductionTimesFlexQParam;
  }

  public void save(String guildId, String rankingId, List<Account> accounts,
      RankedMode rankedMode) {
    String key = guildId + ":" + rankingId;
    if (rankedMode.equals(RANKED_FLEX_SR)) {
      try {
        rankingsFlexQ.remove(key.toLowerCase());
      } catch (NullPointerException ignored) {
      }
      rankingsFlexQ.put(key.toLowerCase(), accounts);
      introductionTimesFlexQ.put(key.toLowerCase(), LocalDateTime.now());
    } else {
      try {
        rankingsSoloQ.remove(key.toLowerCase());
      } catch (NullPointerException ignored) {
      }
      rankingsSoloQ.put(key.toLowerCase(), accounts);
      introductionTimesSoloQ.put(key.toLowerCase(), LocalDateTime.now());
    }
    log.info("Introduced accounts in cache with id {}", key.toLowerCase());
  }

  public Optional<List<Account>> find(String guildId, String rankingId, RankedMode rankedMode) {
    String key = guildId + ":" + rankingId;
    Optional<List<Account>> optionalAccounts;
    try {
      if (rankedMode.equals(RANKED_FLEX_SR)) {
        optionalAccounts = Optional.of(rankingsFlexQ.get(key.toLowerCase()));
      } else {
        optionalAccounts = Optional.of(rankingsSoloQ.get(key.toLowerCase()));
      }
      log.info("Retrieved accounts from cache for id {}", key.toLowerCase());
    } catch (NullPointerException ignored) {
      optionalAccounts = Optional.empty();
    }
    return optionalAccounts;
  }

  public void delete(String rankingId, String guildId) {
    String key = guildId + ":" + rankingId;
    rankingsFlexQ.remove(key);
    introductionTimesFlexQ.remove(key);
    rankingsSoloQ.remove(key);
    introductionTimesSoloQ.remove(key);
    log.info("Removed ranking with id = {} from cache", key.toLowerCase());
  }

  public void removeAccountsIfRankingCached(String guildId, String rankingId,
      List<Account> accounts) {
    try {
      String key = (guildId + ":" + rankingId).toLowerCase();
      List<Account> existingAccounts = new ArrayList<>(rankingsSoloQ.get(key));
      List<Account> accountsToRemove = new ArrayList<>();
      accounts.forEach(account -> {
        List<Account> matches = existingAccounts.stream().filter(account::isSameAccount).toList();
        if (matches.size() > 1) {
          throw new AccountCouldNotBeDesambiguatedException(matches.get(0));
        } else if (matches.size() == 1) {
          accountsToRemove.add(matches.get(0));
        }
      });
      existingAccounts.removeAll(accountsToRemove);
      rankingsSoloQ.remove(key);
      rankingsSoloQ.put(key, existingAccounts);
      log.info("Removed accounts from existing cache {}", key);
    } catch (NullPointerException ignored) {
    }
  }

  public void addAccountsIfRankingCached(String guildId, String rankingId, List<Account> accounts) {
    try {
      String key = (guildId + ":" + rankingId).toLowerCase();
      List<Account> existingAccounts = new ArrayList<>(rankingsSoloQ.get(key));
      existingAccounts.addAll(accounts);
      rankingsSoloQ.remove(key);
      rankingsSoloQ.put(key, existingAccounts);
      log.info("Added account to existing cache {}", key);
    } catch (NullPointerException ignored) {
    }
  }

  @Scheduled(fixedRate = 1000 * 60 * CACHE_MINUTES)
  protected void clearCache() {
    List<String> keysToRemoveFromCache = new ArrayList<>();
    introductionTimesSoloQ.forEach((key, time) -> {
      if (LocalDateTime.now().isAfter(time.plusMinutes(CACHE_MINUTES))) {
        keysToRemoveFromCache.add(key.toLowerCase());
      }
    });
    keysToRemoveFromCache.forEach(key -> {
      rankingsSoloQ.remove(key.toLowerCase());
      introductionTimesSoloQ.remove(key.toLowerCase());
    });
    log.info("Cleared from accounts cache: {}", keysToRemoveFromCache);
  }

  public boolean containsRanking(String rankingId, String guildId) {
    String key = (guildId + ":" + rankingId).toLowerCase();
    return rankingsSoloQ.containsKey(key);
  }
}
