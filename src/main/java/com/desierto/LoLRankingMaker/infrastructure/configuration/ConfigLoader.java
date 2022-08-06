package com.desierto.LoLRankingMaker.infrastructure.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class ConfigLoader {

  @Value("${api.key}")
  private String apiKey;

  @Value("${riot.base.url}")
  private String riotBaseUrl;

  @Value("${discord.api.key}")
  private String discordApiKey;
}
