package com.desierto.Ranky.domain;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ActiveProfiles(profiles = "test")
@ExtendWith(SpringExtension.class)
public class BaseTest {

  static {
    System.setProperty("riot.api.key", "none");
    System.setProperty("riot.base.url", "none");
    System.setProperty("disc.api.key", "none");
    System.setProperty("ranking.limit", "100");
    System.setProperty("ranky.user.role", "Ranky user");
    System.setProperty("config.channel", "config-channel");
    System.setProperty("message.listener.enabled", "false");
  }

}
