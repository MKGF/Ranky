package com.desierto.Ranky.domain;

import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(profiles = "test")
public class BaseTest {

  static {
    System.setProperty("api.key", "none");
    System.setProperty("riot.base.url", "none");
  }

}
