package com.desierto.ranky.infrastructure.configuration;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DiscordClientConfig implements RequestInterceptor {

  private final ConfigLoader config;

  @Autowired
  public DiscordClientConfig(ConfigLoader config) {
    this.config = config;
  }


  @Override
  public void apply(RequestTemplate template) {
    String credentials = config.getClientId() + ":" + config.getClientSecret();
    String encoded = Base64.getEncoder()
        .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
    template.header("Authorization", "Basic " + encoded);
  }
}
