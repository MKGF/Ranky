package com.desierto.ranky.infrastructure.controller;

import static org.springframework.http.ResponseEntity.ok;

import com.desierto.ranky.domain.service.IGuildsService;
import com.desierto.ranky.infrastructure.exceptions.RoleNotFoundException;
import com.desierto.ranky.infrastructure.service.auth.UserPowerChecker;
import com.desierto.ranky.infrastructure.service.auth.UserSession;
import com.desierto.ranky.infrastructure.web.annotation.CurrentUser;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/me")
@Slf4j
public class UserController {

  private IGuildsService guildsService;

  private UserPowerChecker userPowerChecker;

  @Autowired
  public UserController(IGuildsService guildsService, UserPowerChecker userPowerChecker) {
    this.guildsService = guildsService;
    this.userPowerChecker = userPowerChecker;
  }

  @GetMapping
  public ResponseEntity<UserSession> me(@CurrentUser UserSession session) {
    try {
      return ok(session);
    } catch (Exception e) {
      return ResponseEntity.notFound().build();
    }
  }

  @GetMapping("/atGuild/{guildId}")
  public ResponseEntity<Boolean> power(@CurrentUser UserSession session,
      @PathVariable String guildId) {
    log.info("Entered power");
    log.info("Session: {}", session.toString());
    Guild guild = guildsService.get(guildId, session.userId());
    try {
      userPowerChecker.check(session, guild);
      return ok(true);
    } catch (RoleNotFoundException ignored) {
      return ok(false);
    }
  }
}
