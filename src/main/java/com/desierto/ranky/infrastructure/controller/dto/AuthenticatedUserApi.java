package com.desierto.ranky.infrastructure.controller.dto;

import com.desierto.ranky.infrastructure.service.auth.UserSession;

public record AuthenticatedUserApi(String username, String userId, String iconUrl) {

  public static AuthenticatedUserApi fromUserSession(UserSession userSession) {
    return new AuthenticatedUserApi(userSession.username(), userSession.userId(),
        userSession.iconUrl());
  }

}
