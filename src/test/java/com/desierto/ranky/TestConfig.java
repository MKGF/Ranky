package com.desierto.ranky;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

@TestConfiguration
@EntityScan(basePackages = "com.desierto.ranky.domain.entity")
@ComponentScan(basePackages = "com.desierto.ranky.infrastructure")
@ComponentScan(basePackages = "com.desierto.ranky.application")
@ActiveProfiles(profiles = "test")
public class TestConfig {

}
