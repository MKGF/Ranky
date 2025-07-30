package com.desierto.ranky.infrastructure.clients;

import com.desierto.ranky.infrastructure.configuration.RiotClientConfig;
import com.desierto.ranky.infrastructure.dto.riot.RiotAccount;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "riotAccountClient",
    url = "https://europe.api.riotgames.com",
    configuration = RiotClientConfig.class
)
public interface RiotAccountClient {

  @GetMapping("/riot/account/v1/accounts/by-riot-id/{name}/{tag}")
  RiotAccount getAccountDto(@PathVariable("name") String name, @PathVariable("tag") String tag);

  @GetMapping("/riot/account/v1/accounts/by-puuid/{id}")
  RiotAccount getAccountDto(@PathVariable("id") String id);
}
