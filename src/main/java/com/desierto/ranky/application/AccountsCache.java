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
      List<Account> existingSoloQAccounts = new ArrayList<>(rankingsSoloQ.get(key));
      List<Account> existingFlexQAccounts = new ArrayList<>(rankingsFlexQ.get(key));
      List<Account> soloQAccountsToRemove = new ArrayList<>();
      List<Account> flexQAccountsToRemove = new ArrayList<>();
      accounts.forEach(account -> {
        List<Account> matches = existingSoloQAccounts.stream().filter(account::isSameAccount)
            .toList();
        if (matches.size() > 1) {
          throw new AccountCouldNotBeDesambiguatedException(matches.get(0));
        } else if (matches.size() == 1) {
          soloQAccountsToRemove.add(matches.get(0));
        }
        matches = existingFlexQAccounts.stream().filter(account::isSameAccount).toList();
        if (matches.size() > 1) {
          throw new AccountCouldNotBeDesambiguatedException(matches.get(0));
        } else if (matches.size() == 1) {
          flexQAccountsToRemove.add(matches.get(0));
        }
      });
      existingSoloQAccounts.removeAll(soloQAccountsToRemove);
      existingFlexQAccounts.removeAll(flexQAccountsToRemove);
      rankingsSoloQ.remove(key);
      rankingsSoloQ.put(key, existingSoloQAccounts);
      rankingsFlexQ.remove(key);
      rankingsFlexQ.put(key, existingFlexQAccounts);
      log.info("Removed accounts from existing cache {}", key);
    } catch (NullPointerException ignored) {
    }
  }

  public void addAccountsIfRankingCached(String guildId, String rankingId,
      List<Account> soloQAccounts, List<Account> flexQAccounts) {
    try {
      String key = (guildId + ":" + rankingId).toLowerCase();
      if (!soloQAccounts.isEmpty()) {
        List<Account> existingSoloQAccounts = new ArrayList<>(rankingsSoloQ.get(key));
        existingSoloQAccounts.addAll(soloQAccounts);
        rankingsSoloQ.remove(key);
        rankingsSoloQ.put(key, existingSoloQAccounts);
      }
      if (!flexQAccounts.isEmpty()) {
        List<Account> existingFlexQAccounts = new ArrayList<>(rankingsSoloQ.get(key));
        existingFlexQAccounts.addAll(flexQAccounts);
        rankingsFlexQ.remove(key);
        rankingsFlexQ.put(key, existingFlexQAccounts);
      }
      log.info("Added accounts to existing cache {}", key);
    } catch (NullPointerException ignored) {
    }
  }

  @Scheduled(fixedRate = 1000 * 60 * CACHE_MINUTES)
  protected void clearCache() {
    List<String> soloQKeysToRemoveFromCache = new ArrayList<>();
    introductionTimesSoloQ.forEach((key, time) -> {
      if (LocalDateTime.now().isAfter(time.plusMinutes(CACHE_MINUTES))) {
        soloQKeysToRemoveFromCache.add(key.toLowerCase());
      }
    });
    soloQKeysToRemoveFromCache.forEach(key -> {
      rankingsSoloQ.remove(key.toLowerCase());
      introductionTimesSoloQ.remove(key.toLowerCase());
    });
    log.info("Cleared from soloQ accounts cache: {}", soloQKeysToRemoveFromCache);

    List<String> flexQKeysToRemoveFromCache = new ArrayList<>();
    introductionTimesFlexQ.forEach((key, time) -> {
      if (LocalDateTime.now().isAfter(time.plusMinutes(CACHE_MINUTES))) {
        flexQKeysToRemoveFromCache.add(key.toLowerCase());
      }
    });
    soloQKeysToRemoveFromCache.forEach(key -> {
      rankingsFlexQ.remove(key.toLowerCase());
      introductionTimesFlexQ.remove(key.toLowerCase());
    });
    log.info("Cleared from flexQ accounts cache: {}", flexQKeysToRemoveFromCache);
  }

  public boolean containsSoloQRanking(String rankingId, String guildId) {
    String key = (guildId + ":" + rankingId).toLowerCase();
    return rankingsSoloQ.containsKey(key);
  }

  public boolean containsFlexQRanking(String rankingId, String guildId) {
    String key = (guildId + ":" + rankingId).toLowerCase();
    return rankingsFlexQ.containsKey(key);
  }
}
