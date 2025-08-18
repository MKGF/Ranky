package com.desierto.ranky.infrastructure.controller;

import static com.desierto.ranky.application.fixtures.GuildFixtures.aGuild;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.desierto.ranky.infrastructure.BaseIT;
import com.desierto.ranky.infrastructure.service.auth.SessionCache;
import com.desierto.ranky.infrastructure.service.auth.UserSession;
import jakarta.servlet.http.Cookie;
import java.util.List;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.restaction.CacheRestAction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

class RankingsControllerTest extends BaseIT {

  @MockBean
  private JDA jda;

  @Autowired
  private SessionCache sessionCache;

  @Autowired
  private MockMvc mockMvc;


  @Test
  void givenNoSession_returnsForbidden() throws Exception {
    mockMvc.perform(
        get("/rankings/mutual")
    ).andExpect(status().isForbidden());
  }

  @Test
  void givenSession_accessIsGranted_butNoGuildIsFound() throws Exception {
    String userId = "userId";
    String sessionId = sessionCache.generate();
    sessionCache.store(sessionId, new UserSession("token", "username", userId));
    mockJda(userId);
    Cookie cookie = new Cookie("SESSION_ID", sessionId);
    mockMvc.perform(
        get("/rankings/mutual").cookie(cookie)
    ).andExpect(status().isNotFound());
  }

  @Test
  void givenSession_whenRequestingRankings_returnsRankings() throws Exception {
    String userId = "userId";
    String sessionId = sessionCache.generate();
    sessionCache.store(sessionId, new UserSession("token", "username", userId));
    mockJda(aGuild(), userId);
    Cookie cookie = new Cookie("SESSION_ID", sessionId);
    mockMvc.perform(
            get("/rankings/mutual").cookie(cookie)
        ).andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id", is("guildId")))
        .andExpect(jsonPath("$[0].name", is("guildName")))
        .andExpect(jsonPath("$[0].iconUrl", is("guildIconUrl")));
  }

  private void mockJda(Guild guild, String userId) {
    User user = mock(User.class);
    CacheRestAction<User> craUser = mock(CacheRestAction.class);
    when(jda.retrieveUserById(userId)).thenReturn(craUser);
    when(craUser.complete()).thenReturn(user);
    when(jda.getMutualGuilds(user)).thenReturn(List.of(guild));
    when(jda.getGuilds()).thenReturn(List.of(guild));
  }

  private void mockJda(String userId) {
    User user = mock(User.class);
    CacheRestAction<User> craUser = mock(CacheRestAction.class);
    when(jda.retrieveUserById(userId)).thenReturn(craUser);
    when(craUser.complete()).thenReturn(user);
    when(jda.getMutualGuilds(user)).thenReturn(List.of());
    when(jda.getGuilds()).thenReturn(List.of());
  }

}