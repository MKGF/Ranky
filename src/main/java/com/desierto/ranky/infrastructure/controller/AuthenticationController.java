package com.desierto.ranky.infrastructure.controller;

import com.desierto.ranky.infrastructure.configuration.ConfigLoader;
import com.desierto.ranky.infrastructure.service.auth.AuthenticationService;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthenticationController {

  private final ConfigLoader config;

  private final AuthenticationService authenticationService;

  @Autowired
  public AuthenticationController(ConfigLoader config,
      AuthenticationService authenticationService) {
    this.config = config;
    this.authenticationService = authenticationService;
  }

  @GetMapping
  public void redirectToDiscord(HttpServletResponse response) throws IOException {
    String redirectUri = URLEncoder.encode(
        String.format("%sauth/callback", config.getRankyHomeUrl()),
        StandardCharsets.UTF_8);
    String scope = URLEncoder.encode("identify email", StandardCharsets.UTF_8);

    String discordUrl = String.format(
        "https://discord.com/oauth2/authorize?response_type=code&client_id=%s&scope=%s&redirect_uri=%s",
        config.getClientId(), scope, redirectUri);

    response.sendRedirect(discordUrl);
  }

  @GetMapping("/callback")
  public ResponseEntity<?> handleCallback(@RequestParam String code) {
    return authenticationService.authenticate(code);
  }
}
