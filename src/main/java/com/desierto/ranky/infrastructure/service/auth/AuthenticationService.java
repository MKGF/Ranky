package com.desierto.ranky.infrastructure.service.auth;

import com.desierto.ranky.infrastructure.configuration.ConfigLoader;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class AuthenticationService {

  public static final String DISCORD_URL = "https://discord.com/oauth2/authorize?response_type=code&client_id=%s&scope=%s&redirect_uri=%s";
  public static final String CALLBACK_ENDPOINT = "%sauth/callback";
  public static final String SCOPE = "identify email";
  public static final String SESSION_ID = "SESSION_ID";

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

  public void redirect(HttpServletResponse response) throws IOException {
    String redirectUri = URLEncoder.encode(
        String.format(CALLBACK_ENDPOINT, config.getRankyHomeUrl()),
        StandardCharsets.UTF_8);
    String scope = URLEncoder.encode(SCOPE, StandardCharsets.UTF_8);

    String discordUrl = String.format(
        DISCORD_URL,
        config.getClientId(), scope, redirectUri);

    response.sendRedirect(discordUrl);
  }

  public Cookie authenticate(String code) {

    String token = getToken(code);
    String sessionId = createSession(token);

    Cookie cookie = new Cookie(SESSION_ID, sessionId);
    cookie.setHttpOnly(true);
    cookie.setPath("/");
    cookie.setMaxAge(2 * 60 * 60);
    cookie.setSecure(true);

    return cookie;
  }

  private String getToken(String code) {
    try {
      return (String) askForToken(code).get("access_token");
    } catch (Exception e) {
      log.error(String.format("Couldn't retrieve token from given code %s", code));
      log.error(e.getMessage());
      throw e;
    }
  }

  private Map<String, Object> askForToken(String code) {
    String url = "https://discord.com/api/oauth2/token";

    HttpEntity<MultiValueMap<String, String>> request = createTokenRequest(code);

    ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
    return response.getBody();
  }

  private HttpEntity<MultiValueMap<String, String>> createTokenRequest(String code) {
    MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
    body.add("grant_type", "authorization_code");
    body.add("code", code);
    body.add("redirect_uri", String.format(CALLBACK_ENDPOINT, config.getRankyHomeUrl()));

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    String credentials = config.getClientId() + ":" + config.getClientSecret();
    String encoded = Base64.getEncoder()
        .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
    headers.set("Authorization", "Basic " + encoded);
    return new HttpEntity<>(body, headers);
  }

  private String createSession(String token) {
    try {
      Map<String, Object> userDetails = getUserInfo(token);
      String sessionId = sessionCache.generate();
      sessionCache.store(sessionId, UserSession.fromUserDetails(token, userDetails));
      return sessionId;
    } catch (Exception e) {
      log.error(String.format("Couldn't retrieve userDetails from given token %s", token));
      log.error(e.getMessage());
      throw e;
    }
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

  public void remove(String sessionId) {
    if (sessionId != null) {
      sessionCache.remove(sessionId);
    }
  }
}
