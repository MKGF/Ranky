package com.desierto.ranky.infrastructure.clients;

import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "discordClient",
    url = "https://discord.com"
)
public interface DiscordClient {

  @PostMapping("/api/oauth2/token")
  Map<String, Object> getToken(@RequestParam("client_id") String clientId,
      @RequestParam("client_secret") String clientSecret,
      @RequestParam("grant_type") String grantType,
      @RequestParam("scope") String scope,
      @RequestParam("code") String code,
      @RequestParam("redirect_uri") String redirectUri
  );

  @GetMapping("/api/users/@me")
  Map<String, Object> getUserInfo(@RequestHeader("Authorization") String token);
}
