package com.desierto.ranky.infrastructure.service.auth;

import java.util.Map;

public record UserSession(String token, String username, String userId) {

  public static UserSession fromUserDetails(String token, Map<String, Object> userDetails) {
    return new UserSession(token, (String) userDetails.get("username"),
        (String) userDetails.get("id"));
  }
}
