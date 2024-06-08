package com.desierto.Ranky.infrastructure.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class ConfigLoader {

  @Value("${riot.api.key}")
  private String riotApiKey;

  @Value("${riot.base.url}")
  private String riotBaseUrl;

  @Value("${disc.api.key}")
  private String discApiKey;

  @Value("${ranking.limit}")
  private int rankingLimit;

  @Value("${ranky.user.role}")
  private String rankyUserRole;

  @Value("${config.channel}")
  private String configChannel;

  @Value("${message.listener.enabled}")
  private Boolean isMessageListenerEnabled;

  @Value("${creator}")
  private String creator;

  @Value("${sponsors}")
  private String sponsors;
}
