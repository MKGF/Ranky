package com.desierto.ranky.infrastructure.service;

import static com.desierto.ranky.infrastructure.utils.DiscordMessages.COMMAND_NOT_ALLOWED;
import static com.desierto.ranky.infrastructure.utils.DiscordMessages.EXECUTE_COMMAND_FROM_SERVER;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.desierto.ranky.domain.service.IRankingPublisherService;
import com.desierto.ranky.infrastructure.configuration.ConfigLoader;
import com.desierto.ranky.infrastructure.utils.DiscordOptionRetriever;
import java.util.List;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DiscordRankingPublisherServiceTest {

  private static final String RANKY_USER = "rankyUser";


  @InjectMocks
  DiscordRankingPublisherService cut;

  @Mock
  ConfigLoader config;

  @Mock
  IRankingPublisherService rankingPublisherService;

  @Mock
  DiscordOptionRetriever discordOptionRetriever;

  @Test
  void onEvent_withMemberWithoutRole_doNothing() {
    SlashCommandInteractionEvent event = mock(SlashCommandInteractionEvent.class);
    Member member = mock(Member.class);
    InteractionHook hook = mock(InteractionHook.class);
    WebhookMessageCreateAction wmca = mock(WebhookMessageCreateAction.class);
    when(event.getMember()).thenReturn(member);
    when(member.getRoles()).thenReturn(List.of());
    when(event.getHook()).thenReturn(hook);
    when(hook.sendMessage(anyString())).thenReturn(wmca);
    cut.execute(event);
    verify(event.getHook(), times(1)).sendMessage(COMMAND_NOT_ALLOWED.getMessage());
  }

  @Test
  void onNonGuildEvent_doNothing() {
    SlashCommandInteractionEvent event = mock(SlashCommandInteractionEvent.class);
    Member member = mock(Member.class);
    Role role = mock(Role.class);
    InteractionHook hook = mock(InteractionHook.class);
    WebhookMessageCreateAction wmca = mock(WebhookMessageCreateAction.class);
    when(event.getMember()).thenReturn(member);
    when(member.getRoles()).thenReturn(List.of(role));
    when(role.getName()).thenReturn(RANKY_USER);
    when(event.getHook()).thenReturn(hook);
    when(hook.sendMessage(anyString())).thenReturn(wmca);
    when(config.getRankyUserRole()).thenReturn(RANKY_USER);
    cut.execute(event);
    verify(event.getHook(), times(1)).sendMessage(EXECUTE_COMMAND_FROM_SERVER.getMessage());
  }

  @Test
  void onEvent_createsRankingAndInformsInHook() {
    SlashCommandInteractionEvent event = getMockedEvent();
    Member member = mock(Member.class);
    Role role = mock(Role.class);
    when(event.getMember()).thenReturn(member);
    when(member.getRoles()).thenReturn(List.of(role));
    when(role.getName()).thenReturn(RANKY_USER);
    String rankingName = "Test";
    when(discordOptionRetriever.fromEventGetObjectName(event)).thenReturn(rankingName);
    when(config.getRankyUserRole()).thenReturn(RANKY_USER);
    cut.execute(event);
    verify(event.getHook().sendMessage(anyString()), times(1)).queue();
    verify(rankingPublisherService, times(1)).publish(rankingName, event.getGuild());
  }

  private SlashCommandInteractionEvent getMockedEvent() {
    SlashCommandInteractionEvent event = mock(SlashCommandInteractionEvent.class);
    Guild guild = mock(Guild.class);
    InteractionHook hook = mock(InteractionHook.class);
    WebhookMessageCreateAction wmca = mock(WebhookMessageCreateAction.class);
    when(event.isFromGuild()).thenReturn(true);
    when(event.getGuild()).thenReturn(guild);
    when(event.getHook()).thenReturn(hook);
    when(hook.sendMessage(anyString())).thenReturn(wmca);
    doNothing().when(wmca).queue();
    return event;
  }

  private DataObject getParameter() {
    return DataObject.fromJson("{\"name\":\"name\",\"type\":3,\"value\":\"Test\"}");
  }
}