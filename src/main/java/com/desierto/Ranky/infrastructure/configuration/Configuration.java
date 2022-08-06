package com.desierto.Ranky.infrastructure.configuration;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootConfiguration
@ComponentScan(basePackages = "com.desierto.Ranky.infrastructure")
@ComponentScan(basePackages = "com.desierto.Ranky.application")
@EntityScan(basePackages = "com.desierto.Ranky.domain.entity")
public class Configuration implements WebMvcConfigurer {
  
}
