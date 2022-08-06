package com.desierto.Ranky.infrastructure.configuration;

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
}
