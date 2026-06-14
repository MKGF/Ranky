package com.desierto.ranky;

import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

@TestConfiguration
@ComponentScan(basePackages = "com.desierto.ranky.infrastructure")
@ComponentScan(basePackages = "com.desierto.ranky.application")
@EntityScan(basePackages = "com.desierto.ranky.domain.entity")
@ActiveProfiles(profiles = "test")
public class TestConfig {

}
