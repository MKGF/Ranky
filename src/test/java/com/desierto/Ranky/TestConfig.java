package com.desierto.Ranky;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EntityScan(basePackages = "com.desierto.Ranky.domain.entity")
@ComponentScan(basePackages = "com.desierto.Ranky.infrastructure")
@ComponentScan(basePackages = "com.desierto.Ranky.application")
public class TestConfig {
  
}
