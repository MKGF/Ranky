package com.desierto.ranky.infrastructure.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.desierto.ranky.infrastructure.service.auth.SessionCache;
import com.desierto.ranky.infrastructure.service.auth.UserSession;
import jakarta.servlet.http.Cookie;
import net.dv8tion.jda.api.JDA;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RankingsControllerTest {

  @MockBean
  private JDA jda;

  @Autowired
  private SessionCache sessionCache;

  @Autowired
  private MockMvc mockMvc;


  @Test
  void givenNoSession_returnsForbidden() throws Exception {
    mockMvc.perform(
        get("/ranking/mutual")
    ).andExpect(status().isForbidden());
  }

  @Test
  void givenSession_accessIsGranted() throws Exception {
    String sessionId = sessionCache.generate();
    sessionCache.store(sessionId, new UserSession("token", "username", "userId"));
    Cookie cookie = new Cookie("SESSION_ID", sessionId);
    mockMvc.perform(
        get("/ranking/mutual").cookie(cookie)
    ).andExpect(status().isNotFound());
  }

}