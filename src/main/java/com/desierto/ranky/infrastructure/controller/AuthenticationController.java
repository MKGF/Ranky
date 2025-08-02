package com.desierto.ranky.infrastructure.controller;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthenticationController {

  //TODO
  @GetMapping
  public void redirectToDiscord(HttpServletResponse response) throws IOException {
    String clientId = "TU_CLIENT_ID";
    String redirectUri = URLEncoder.encode("http://localhost:8080/auth/discord/callback",
        StandardCharsets.UTF_8);
    String scope = URLEncoder.encode("identify email", StandardCharsets.UTF_8);

    String discordUrl = "https://discord.com/oauth2/authorize" +
        "?response_type=code" +
        "&client_id=" + clientId +
        "&scope=" + scope +
        "&redirect_uri=" + redirectUri;

    response.sendRedirect(discordUrl);
  }

  //TODO
  @GetMapping("/callback")
  public ResponseEntity<?> handleCallback(@RequestParam String code) {
    // Aquí puedes hacer la petición POST para obtener el access_token
    // y luego usarlo para pedir /users/@me

    // Para este ejemplo lo dejamos como un placeholder:
    return ResponseEntity.ok("Código recibido: " + code);
  }
}
