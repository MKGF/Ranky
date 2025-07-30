package com.desierto.ranky.application;

import com.desierto.ranky.domain.entity.Account;
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
    log.info(String.format("Introduced accounts in cache with id %s", key.toLowerCase()));
  }

  public Optional<List<Account>> find(String guildId, String rankingId) {
    String key = guildId + ":" + rankingId;
    Optional<List<Account>> optionalAccounts;
    try {
      optionalAccounts = Optional.of(rankings.get(key.toLowerCase()));
      log.info(String.format("Retrieved accounts from cache for id %s", key.toLowerCase()));
    } catch (NullPointerException ignored) {
      optionalAccounts = Optional.empty();
    }
    return optionalAccounts;
  }

  public void delete(String rankingId, String guildId) {
    String key = guildId + ":" + rankingId;
    rankings.remove(key);
    introductionTimes.remove(key);
    log.info(String.format("Removed ranking with id = %s from cache", key.toLowerCase()));
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
