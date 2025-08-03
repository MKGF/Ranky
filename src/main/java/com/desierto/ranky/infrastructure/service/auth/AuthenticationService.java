package com.desierto.ranky.infrastructure.service.auth;

import com.desierto.ranky.infrastructure.clients.DiscordClient;
import com.desierto.ranky.infrastructure.configuration.ConfigLoader;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthenticationService {

  private final DiscordClient discordClient;

  private final ConfigLoader config;

  private final SessionCache sessionCache;

  @Autowired
  public AuthenticationService(DiscordClient discordClient, ConfigLoader configLoader,
      SessionCache sessionCache) {
    this.discordClient = discordClient;
    this.config = configLoader;
    this.sessionCache = sessionCache;
  }

  public ResponseEntity<?> authenticate(String code) {
    // Get the token for this code
    String redirectUri = String.format("%sauth/callback", config.getRankyHomeUrl());
    Map<String, Object> response;
    try {
      response = discordClient.getToken("authorization_code", "identify",
          code, redirectUri);
    } catch (Exception e) {
      log.info(String.format("Couldn't retrieve token from given code %s", code));
      return ResponseEntity.unprocessableEntity().build();
    }
    log.info("RECEIVED TOKEN RESPONSE");
    response.forEach((key, value) -> log.info(key + ":" + value.toString()));
    String token = (String) response.get("access_token");
    // Get the user details
    Map<String, Object> userDetails;
    try {
      userDetails = discordClient.getUserInfo(token);
    } catch (Exception e) {
      log.info(String.format("Couldn't retrieve userDetails from given token %s", token));
      return ResponseEntity.unprocessableEntity().build();
    }
    log.info("RECEIVED USER DETAILS");
    userDetails.forEach((key, value) -> log.info(key + ":" + value.toString()));
    String sessionId = UUID.randomUUID().toString();
    // Store them in the cache
    sessionCache.store(sessionId, new UserSession(token, (String) userDetails.get("username"),
        (String) userDetails.get("userId")));

    ResponseCookie cookie = ResponseCookie.from("SESSION_ID", sessionId)
        .httpOnly(true)
        .path("/")
        .maxAge(Duration.ofHours(2))
        .build();

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, cookie.toString())
        .body(Map.of("status", "ok"));
  }
}
