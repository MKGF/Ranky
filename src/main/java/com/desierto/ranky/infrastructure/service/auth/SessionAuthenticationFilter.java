package com.desierto.ranky.infrastructure.service.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
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

    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if ("SESSION_ID".equals(cookie.getName())) {
          UserSession session = sessionStore.get(cookie.getValue());
          if (session != null) {
            // Puedes poner la sesión en un atributo del request
            request.setAttribute("userSession", session);
          }
        }
      }
    }

    filterChain.doFilter(request, response);
  }
}

