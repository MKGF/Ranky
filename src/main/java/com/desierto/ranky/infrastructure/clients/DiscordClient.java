package com.desierto.ranky.infrastructure.clients;

import com.desierto.ranky.infrastructure.configuration.DiscordClientConfig;
import feign.Headers;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "discordClient",
    url = "https://discord.com",
    configuration = DiscordClientConfig.class
)
public interface DiscordClient {

  @PostMapping(value = "/api/oauth2/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  @Headers("Content-Type: application/x-www-form-urlencoded")
  Map<String, Object> getToken(
      @RequestParam("grant_type") String grantType,
      @RequestParam("scope") String scope,
      @RequestParam("code") String code,
      @RequestParam("redirect_uri") String redirectUri
  );

  @GetMapping("/api/users/@me")
  Map<String, Object> getUserInfo(@RequestHeader("Authorization") String token);
}
