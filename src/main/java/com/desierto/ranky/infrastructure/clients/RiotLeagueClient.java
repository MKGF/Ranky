package com.desierto.ranky.infrastructure.clients;

import com.desierto.ranky.infrastructure.configuration.RiotClientConfig;
import com.desierto.ranky.infrastructure.dto.riot.League;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
    name = "riotLeagueClient",
    url = "https://euw1.api.riotgames.com", // Ajusta la región
    configuration = RiotClientConfig.class
)
public interface RiotLeagueClient {

  @GetMapping("/lol/league/v4/entries/by-puuid/{id}")
  List<League> getLeaguesOfAccount(String id);
}
