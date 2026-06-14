package com.desierto.ranky.infrastructure.configuration;

import com.google.gson.Gson;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootConfiguration
@ComponentScan(basePackages = "com.desierto.ranky.infrastructure")
@ComponentScan(basePackages = "com.desierto.ranky.application")
@EntityScan(basePackages = "com.desierto.ranky.domain.entity")
public class RankyConfiguration implements WebMvcConfigurer {

  @Bean
  public Gson gson() {
    return new Gson();
  }

  @Bean
  public ExecutorService executorService() {
    return Executors.newFixedThreadPool(20);
  }

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
}
