package com.desierto.ranky.infrastructure.service;

import static com.desierto.ranky.infrastructure.utils.DiscordMessages.COMMAND_NOT_ALLOWED;
import static com.desierto.ranky.infrastructure.utils.DiscordMessages.EXECUTE_COMMAND_FROM_SERVER;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.desierto.ranky.domain.exception.ranking.RankingCouldNotBeDeletedException;
import com.desierto.ranky.domain.service.IDeleteRankingService;
import com.desierto.ranky.infrastructure.configuration.ConfigLoader;
import java.util.List;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class DiscordDeleteRankingServiceTest {

  private static final String RANKY_USER = "rankyUser";

  DiscordDeleteRankingService cut;

  @Mock
  ConfigLoader config;

  @Mock
  IDeleteRankingService deleteRankingService;

  @BeforeAll
  public void setUp() {
    cut = new DiscordDeleteRankingService(config, deleteRankingService);
    when(config.getRankyUserRole()).thenReturn(RANKY_USER);
  }

  @Test
  public void onEvent_withMemberWithoutRole_doNothing() {
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
  public void onNonGuildEvent_doNothing() {
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
    cut.execute(event);
    verify(event.getHook(), times(1)).sendMessage(EXECUTE_COMMAND_FROM_SERVER.getMessage());
  }

  @Test
  public void onEvent_deletesRankingAndInformsInHook() {
    SlashCommandInteractionEvent event = getMockedEvent();
    String rankingId = "Test";
    when(deleteRankingService.execute(rankingId, event.getGuild())).thenReturn(true);
    cut.execute(event);
    verify(deleteRankingService, times(1)).execute(rankingId, event.getGuild());
    verify(event.getHook().sendMessage(anyString()), times(1)).queue();
  }

  @Test
  public void onEvent_whenDeleteWasNotSuccessful_throwsException() {
    SlashCommandInteractionEvent event = getMockedEvent();
    String rankingId = "Test";
    when(deleteRankingService.execute(rankingId, event.getGuild())).thenReturn(false);

    assertThrows(RankingCouldNotBeDeletedException.class, () -> cut.execute(event));
    verify(deleteRankingService, times(1)).execute(rankingId, event.getGuild());
  }

  private SlashCommandInteractionEvent getMockedEvent() {
    SlashCommandInteractionEvent event = mock(SlashCommandInteractionEvent.class);
    Guild guild = mock(Guild.class);
    InteractionHook hook = mock(InteractionHook.class);
    Member member = mock(Member.class);
    Role role = mock(Role.class);
    WebhookMessageCreateAction wmca = mock(WebhookMessageCreateAction.class);
    when(event.getHook()).thenReturn(hook);
    when(event.getMember()).thenReturn(member);
    when(member.getRoles()).thenReturn(List.of(role));
    when(role.getName()).thenReturn(RANKY_USER);
    when(event.isFromGuild()).thenReturn(true);
    when(event.getGuild()).thenReturn(guild);
    when(event.getOptions()).thenReturn(
        List.of(new OptionMapping(
                getParameter(),
                null,
                null,
                null
            )
        )
    );
    when(event.getHook()).thenReturn(hook);
    when(hook.sendMessage(anyString())).thenReturn(wmca);
    doNothing().when(wmca).queue();
    return event;
  }

  private DataObject getParameter() {
    return DataObject.fromJson("{\"name\":\"name\",\"type\":3,\"value\":\"Test\"}");
  }

}
