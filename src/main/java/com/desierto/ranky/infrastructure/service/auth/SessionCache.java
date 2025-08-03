package com.desierto.ranky.infrastructure.service.auth;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
