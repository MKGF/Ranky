package com.desierto.ranky.application.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.restaction.CacheRestAction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GuildsServiceTest {

  @Mock
  private JDA jda;

  @InjectMocks
  private GuildsService cut;

  @Test
  void getGuild_loadsAndGetsFirstMatch() {
    String userId = "userId";
    String guildId = "guildId";
    Guild guild = mock(Guild.class);
    CacheRestAction<User> userCra = mock(CacheRestAction.class);
    CacheRestAction<Member> memberCra = mock(CacheRestAction.class);

    when(guild.getId()).thenReturn(guildId);
    when(jda.getGuilds()).thenReturn(List.of(guild));
    when(guild.retrieveMemberById(userId)).thenReturn(memberCra);
    when(jda.retrieveUserById(userId)).thenReturn(userCra);
    when(userCra.complete()).thenReturn(mock(User.class));
    when(memberCra.complete()).thenReturn(mock(Member.class));
    when(jda.getMutualGuilds(any(User.class))).thenReturn(List.of(guild));
    assertNotNull(cut.get(guildId, userId));
  }

  @Test
  void getGuild_loadsAndGetsAll() {
    String userId = "userId";
    Guild guild = mock(Guild.class);
    CacheRestAction<User> userCra = mock(CacheRestAction.class);
    CacheRestAction<Member> memberCra = mock(CacheRestAction.class);

    when(jda.getGuilds()).thenReturn(List.of(guild));
    when(guild.retrieveMemberById(userId)).thenReturn(memberCra);
    when(jda.retrieveUserById(userId)).thenReturn(userCra);
    when(userCra.complete()).thenReturn(mock(User.class));
    when(memberCra.complete()).thenReturn(mock(Member.class));
    when(jda.getMutualGuilds(any(User.class))).thenReturn(List.of(guild));
    assertFalse(cut.getAll(userId).isEmpty());
  }
}