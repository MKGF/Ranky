package com.desierto.Ranky.infrastructure.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(final HttpSecurity http) throws Exception {
    http
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    http
        .anonymous()
        .and()
        .csrf()
        .disable()
        .authorizeRequests()
        .antMatchers("/")
        .permitAll();
    http.headers().frameOptions().disable();
  }
}
