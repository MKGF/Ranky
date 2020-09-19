package com.desierto.LoLRankingMaker;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = {
    "com.desierto.LoLRankingMaker.infrastructure.repository"
})
@EntityScan(basePackages = "com.desierto.LoLRankingMaker.domain.entity")
@ComponentScan(basePackages = "com.desierto.LoLRankingMaker.infrastructure")
@ComponentScan(basePackages = "com.desierto.LoLRankingMaker.application")
public class TestConfig {

}
