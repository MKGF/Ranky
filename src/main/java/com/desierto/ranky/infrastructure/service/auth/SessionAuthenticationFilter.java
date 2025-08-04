package com.desierto.ranky.infrastructure.service.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Slf4j
public class SessionAuthenticationFilter extends OncePerRequestFilter {

  private final SessionCache sessionStore;

  @Autowired
  public SessionAuthenticationFilter(SessionCache sessionStore) {
    this.sessionStore = sessionStore;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain)
      throws ServletException, IOException {

    log.info("Filtering request");
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if ("SESSION_ID".equals(cookie.getName())) {
          UserSession session = sessionStore.get(cookie.getValue());
          if (session != null) {
            log.info("Successfully established session from cookie as {}", session);
            request.setAttribute("userSession", session);
          }
        }
      }
    }

    filterChain.doFilter(request, response);
  }
}

