package com.desierto.ranky.infrastructure.configuration;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.codec.Encoder;
import feign.form.FormEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
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
    log.info("Request intercepted to add following credentials: " + credentials);
  }

  @Bean
  public Encoder feignFormEncoder() {
    return new FormEncoder();
  }
}
