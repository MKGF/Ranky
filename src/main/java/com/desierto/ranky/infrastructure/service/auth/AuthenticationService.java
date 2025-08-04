package com.desierto.ranky.infrastructure.service.auth;

import com.desierto.ranky.infrastructure.configuration.ConfigLoader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class AuthenticationService {

  private final RestTemplate restTemplate;

  private final ConfigLoader config;

  private final SessionCache sessionCache;

  @Autowired
  public AuthenticationService(RestTemplateBuilder builder, ConfigLoader configLoader,
      SessionCache sessionCache) {
    this.restTemplate = builder.build();
    this.config = configLoader;
    this.sessionCache = sessionCache;
  }

  public ResponseEntity<?> authenticate(String code) {
    // Get the token for this code
    Map<String, Object> response;
    try {
      response = getToken(code);
    } catch (Exception e) {
      log.info(e.getMessage());
      log.info(String.format("Couldn't retrieve token from given code %s", code));
      return ResponseEntity.unprocessableEntity().build();
    }
    log.info("RECEIVED TOKEN RESPONSE");
    response.forEach((key, value) -> log.info(key + ":" + value.toString()));
    String token = (String) response.get("access_token");
    // Get the user details
    Map<String, Object> userDetails;
    try {
      userDetails = getUserInfo(token);
    } catch (Exception e) {
      log.info(e.getMessage());
      log.info(String.format("Couldn't retrieve userDetails from given token %s", token));
      return ResponseEntity.unprocessableEntity().build();
    }
    log.info("RECEIVED USER DETAILS");
    userDetails.forEach((key, value) -> log.info(key + ":" + value));
    String sessionId = UUID.randomUUID().toString();
    // Store them in the cache
    sessionCache.store(sessionId, new UserSession(token, (String) userDetails.get("username"),
        (String) userDetails.get("id")));

    ResponseCookie cookie = ResponseCookie.from("SESSION_ID", sessionId)
        .httpOnly(true)
        .path("/")
        .maxAge(Duration.ofHours(2))
        .build();

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, cookie.toString())
        .body(Map.of("status", "ok"));
  }

  private Map<String, Object> getToken(String code) {
    String url = "https://discord.com/api/oauth2/token";

    MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
    body.add("grant_type", "authorization_code");
    body.add("code", code);
    body.add("redirect_uri", "https://api.ranky.top/auth/callback");

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    String credentials = config.getClientId() + ":" + config.getClientSecret();
    String encoded = Base64.getEncoder()
        .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
    headers.set("Authorization", "Basic " + encoded);

    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

    ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
    return response.getBody();
  }

  private Map<String, Object> getUserInfo(String token) {
    String url = "https://discord.com/api/users/@me";

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);

    HttpEntity<Void> request = new HttpEntity<>(headers);

    ResponseEntity<Map> response = restTemplate.exchange(
        url,
        HttpMethod.GET,
        request,
        Map.class
    );

    return response.getBody();
  }
}
