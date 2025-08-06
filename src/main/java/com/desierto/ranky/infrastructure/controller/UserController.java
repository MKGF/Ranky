package com.desierto.ranky.infrastructure.controller;

import com.desierto.ranky.infrastructure.service.auth.UserSession;
import com.desierto.ranky.infrastructure.web.annotation.CurrentUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/me")
@Slf4j
public class UserController {

  @GetMapping
  public ResponseEntity<UserSession> me(@CurrentUser UserSession session) {
    try {
      return ResponseEntity.ok(session);
    } catch (Exception e) {
      return ResponseEntity.notFound().build();
    }
  }
}
