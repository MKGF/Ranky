package com.desierto.ranky.infrastructure.clients;

import com.desierto.ranky.infrastructure.configuration.RiotClientConfig;
import com.desierto.ranky.infrastructure.dto.riot.RiotAccount;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
    name = "riotAccountClient",
    url = "https://europe.api.riotgames.com", // Ajusta la región
    configuration = RiotClientConfig.class
)
public interface RiotAccountClient {

  @GetMapping("/riot/account/v1/accounts/by-riot-id/{name}/{tag}")
  RiotAccount getAccountDto(String name, String tag);
}
