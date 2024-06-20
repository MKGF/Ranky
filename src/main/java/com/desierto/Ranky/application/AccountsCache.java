package com.desierto.Ranky.application;

import com.desierto.Ranky.domain.entity.Account;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class AccountsCache {

  public static final Logger log = Logger.getLogger("AccountsCache.class");

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

  public void save(String key, List<Account> accounts) {
    try {
      rankings.remove(key.toLowerCase());
    } catch (NullPointerException ignored) {
    }
    rankings.put(key.toLowerCase(), accounts);
    introductionTimes.put(key.toLowerCase(), LocalDateTime.now());
    log.info(String.format("Introduced accounts in cache with id %s", key.toLowerCase()));
  }

  public Optional<List<Account>> find(String key) {
    Optional<List<Account>> optionalAccounts;
    try {
      optionalAccounts = Optional.of(rankings.get(key.toLowerCase()));
      log.info(String.format("Retrieved accounts from cache for id %s", key.toLowerCase()));
    } catch (NullPointerException ignored) {
      optionalAccounts = Optional.empty();
    }
    return optionalAccounts;
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
    log.info(String.format("Cleared from cache: %s", keysToRemoveFromCache));
  }
}
