package com.desierto.ranky.infrastructure.service.auth;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SessionCache {

  private final int CACHE_MINUTES = 120;
  private final Map<String, UserSession> sessions = new ConcurrentHashMap<>();

  private final Map<String, LocalDateTime> introductionTimes = new ConcurrentHashMap<>();

  public String generate() {
    String id;
    do {
      id = UUID.randomUUID().toString();
    } while (sessions.containsKey(id));
    return id;
  }

  public void store(String sessionId, UserSession session) {
    sessions.put(sessionId, session);
    introductionTimes.put(sessionId, LocalDateTime.now());
    log.info(String.format("Stored cookie for %s", session.toString()));
  }

  public UserSession get(String sessionId) {
    return sessions.get(sessionId);
  }

  public void remove(String sessionId) {
    sessions.remove(sessionId);
    introductionTimes.remove(sessionId);
    log.info(String.format("Removed session %s", sessionId));
  }

  public void remove(UserSession userSession) {
    Optional<String> keyToRemove = sessions.entrySet().stream()
        .filter(entry -> entry.getValue().equals(userSession)).map(Entry::getKey).findFirst();
    if (keyToRemove.isPresent()) {
      sessions.remove(keyToRemove.get());
      introductionTimes.remove(keyToRemove.get());
      log.info(String.format("Removed session %s", keyToRemove));
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
    keysToRemoveFromCache.forEach(this::remove);
    log.info(String.format("Cleared from session cache: %s", keysToRemoveFromCache));
  }
}
