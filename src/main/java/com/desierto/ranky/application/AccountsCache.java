package com.desierto.ranky.application;

import com.desierto.ranky.domain.entity.Account;
import com.desierto.ranky.domain.exception.account.AccountCouldNotBeDesambiguatedException;
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

  private Map<String, List<Account>> rankings;

  private Map<String, LocalDateTime> introductionTimes;

  public AccountsCache() {
    rankings = new HashMap<>();
    introductionTimes = new HashMap<>();
  }

  public AccountsCache(Map<String, List<Account>> rankingsParam,
      Map<String, LocalDateTime> introductionTimesParam) {
    rankings = rankingsParam;
    introductionTimes = introductionTimesParam;
  }

  public void save(String guildId, String rankingId, List<Account> accounts) {
    String key = guildId + ":" + rankingId;
    try {
      rankings.remove(key.toLowerCase());
    } catch (NullPointerException ignored) {
    }
    rankings.put(key.toLowerCase(), accounts);
    introductionTimes.put(key.toLowerCase(), LocalDateTime.now());
    log.info("Introduced accounts in cache with id {}", key.toLowerCase());
  }

  public Optional<List<Account>> find(String guildId, String rankingId) {
    String key = guildId + ":" + rankingId;
    Optional<List<Account>> optionalAccounts;
    try {
      optionalAccounts = Optional.of(rankings.get(key.toLowerCase()));
      log.info("Retrieved accounts from cache for id {}", key.toLowerCase());
    } catch (NullPointerException ignored) {
      optionalAccounts = Optional.empty();
    }
    return optionalAccounts;
  }

  public void delete(String rankingId, String guildId) {
    String key = guildId + ":" + rankingId;
    rankings.remove(key);
    introductionTimes.remove(key);
    log.info("Removed ranking with id = {} from cache", key.toLowerCase());
  }

  public void removeAccountsIfRankingCached(String guildId, String rankingId,
      List<Account> accounts) {
    try {
      String key = (guildId + ":" + rankingId).toLowerCase();
      List<Account> existingAccounts = new ArrayList<>(rankings.get(key));
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
      rankings.remove(key);
      rankings.put(key, existingAccounts);
      log.info("Removed accounts from existing cache {}", key);
    } catch (NullPointerException ignored) {
    }
  }

  public void addAccountsIfRankingCached(String guildId, String rankingId, List<Account> accounts) {
    try {
      String key = (guildId + ":" + rankingId).toLowerCase();
      List<Account> existingAccounts = new ArrayList<>(rankings.get(key));
      existingAccounts.addAll(accounts);
      rankings.remove(key);
      rankings.put(key, existingAccounts);
      log.info("Added account to existing cache {}", key);
    } catch (NullPointerException ignored) {
    }
  }

  @Scheduled(fixedRate = 1000 * 60 * CACHE_MINUTES)
  protected void clearCache() {
    List<String> keysToRemoveFromCache = new ArrayList<>();
    introductionTimes.forEach((key, time) -> {
      if (LocalDateTime.now().isAfter(time.plusMinutes(CACHE_MINUTES))) {
        keysToRemoveFromCache.add(key.toLowerCase());
      }
    });
    keysToRemoveFromCache.forEach(key -> {
      rankings.remove(key.toLowerCase());
      introductionTimes.remove(key.toLowerCase());
    });
    log.info("Cleared from accounts cache: {}", keysToRemoveFromCache);
  }

  public boolean containsRanking(String rankingId, String guildId) {
    String key = (guildId + ":" + rankingId).toLowerCase();
    return rankings.containsKey(key);
  }
}
