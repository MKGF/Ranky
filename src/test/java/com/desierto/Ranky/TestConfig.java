package com.desierto.Ranky;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EntityScan(basePackages = "com.desierto.Ranky.domain.entity")
@ComponentScan(basePackages = "com.desierto.Ranky.infrastructure")
@ComponentScan(basePackages = "com.desierto.Ranky.application")
public class TestConfig {

  @Bean
  public Validator validator() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    return factory.getValidator();
  }
}
