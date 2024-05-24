package com.desierto.Ranky;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;

@Configuration
@EntityScan(basePackages = "com.desierto.Ranky.domain.entity")
@ComponentScan(basePackages = "com.desierto.Ranky.infrastructure")
@ComponentScan(basePackages = "com.desierto.Ranky.application")
@ActiveProfiles(profiles = "test")
public class TestConfig {
  
}
