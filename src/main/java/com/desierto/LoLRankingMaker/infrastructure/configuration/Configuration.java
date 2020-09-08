package com.desierto.LoLRankingMaker.infrastructure.configuration;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootConfiguration
@ComponentScan(basePackages = "com.desierto.LoLRankingMaker.infrastructure")
@ComponentScan(basePackages = "com.desierto.LoLRankingMaker.application")
@EntityScan(basePackages = "com.desierto.LoLRankingMaker.domain.entity")
public class Configuration implements WebMvcConfigurer {

}
