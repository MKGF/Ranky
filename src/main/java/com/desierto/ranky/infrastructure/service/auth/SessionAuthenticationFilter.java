package com.desierto.ranky.infrastructure.service.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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

    try {
      Cookie[] cookies = request.getCookies();

      if (cookies != null) {
        Arrays.stream(cookies)
            .filter(cookie -> "SESSION_ID".equals(cookie.getName()))
            .findFirst()
            .ifPresent(cookie -> {
              String sessionId = cookie.getValue();
              UserSession session = sessionStore.get(sessionId);

              if (session != null) {
                log.info("Authenticated session for user: {}", session.userId());

                request.setAttribute("userSession", session);

                Authentication auth = new UsernamePasswordAuthenticationToken(
                    session,
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_USER"))
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
              } else {
                log.info("Session ID not found in session store: {}", sessionId);
              }
            });
      }

      filterChain.doFilter(request, response);

    } finally {
      SecurityContextHolder.clearContext();
    }
  }
}

