package com.desierto.ranky.infrastructure.service.auth;

public record UserSession(String token, String username, String userId) {

}
