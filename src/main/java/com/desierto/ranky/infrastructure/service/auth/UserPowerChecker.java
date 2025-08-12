package com.desierto.ranky.infrastructure.service.auth;

import com.desierto.ranky.infrastructure.configuration.ConfigLoader;
import com.desierto.ranky.infrastructure.exceptions.RoleNotFoundException;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserPowerChecker {

  private ConfigLoader config;

  @Autowired
  public UserPowerChecker(ConfigLoader config) {
    this.config = config;
  }

  public void check(UserSession session, Guild guild) {
    if (guild.retrieveMemberById(session.userId()).complete().getRoles().stream()
        .noneMatch(role -> role.getName().equalsIgnoreCase(config.getRankyUserRole()))) {
      throw new RoleNotFoundException();
    }
  }
}
