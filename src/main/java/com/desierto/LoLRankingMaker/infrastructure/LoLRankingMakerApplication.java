package com.desierto.LoLRankingMaker.infrastructure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class LoLRankingMakerApplication extends SpringBootServletInitializer {

  public static void main(String[] args) {
    SpringApplication.run(LoLRankingMakerApplication.class, args);
  }

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder applicationBuilder) {
    return applicationBuilder.sources(LoLRankingMakerApplication.class);
  }
}
