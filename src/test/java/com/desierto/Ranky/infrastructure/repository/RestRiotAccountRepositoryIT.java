package com.desierto.Ranky.infrastructure.repository;

import com.desierto.Ranky.infrastructure.BaseIT;
import com.desierto.Ranky.infrastructure.configuration.ConfigLoader;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

public class RestRiotAccountRepositoryIT extends BaseIT {

  @Autowired
  private ConfigLoader configLoader;

  private RestRiotAccountRepository restRiotAccountRepository;

  @BeforeEach
  public void set_up() {
    restRiotAccountRepository = new RestRiotAccountRepository(configLoader);
  }

}
