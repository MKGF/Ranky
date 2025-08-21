package com.desierto.ranky.infrastructure.service.auth;

import java.util.Map;

public record UserSession(String token, String username, String userId, String iconUrl) {

  public static UserSession fromUserDetails(String token, Map<String, Object> userDetails) {
    String userId = (String) userDetails.get("id");
    String avatarHash = (String) userDetails.get("avatar");
    String avatarUrl = String.format("https://cdn.discordapp.com/avatars/%s/%s.jpg", userId,
        avatarHash);
    return new UserSession(token, (String) userDetails.get("username"),
        userId, avatarUrl);
  }
}
