package com.desierto.ranky.infrastructure.configuration;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RiotClientConfig {

  @Bean
  public RequestInterceptor riotRequestInterceptor(ConfigLoader configLoader) {
    return requestTemplate -> requestTemplate.query("api_key", configLoader.getRiotApiKey());
  }
}
