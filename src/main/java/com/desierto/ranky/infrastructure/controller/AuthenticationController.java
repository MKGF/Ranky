package com.desierto.ranky.infrastructure.controller;

import com.desierto.ranky.infrastructure.configuration.ConfigLoader;
import com.desierto.ranky.infrastructure.service.auth.AuthenticationService;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    authenticationService.redirect(response);
  }

  @GetMapping("/callback")
  public void handleCallback(HttpServletResponse response, @RequestParam String code)
      throws IOException {
    response.addCookie(authenticationService.authenticate(code));
    response.sendRedirect("https://ranky.top/#/servers");
  }
}
