package com.desierto.ranky.infrastructure.service;

import static com.desierto.ranky.infrastructure.utils.DiscordMessages.COMMAND_NOT_ALLOWED;
import static com.desierto.ranky.infrastructure.utils.DiscordMessages.EXECUTE_COMMAND_FROM_SERVER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.desierto.ranky.domain.entity.Account;
import com.desierto.ranky.domain.entity.Ranking;
import com.desierto.ranky.domain.service.IAccountsService;
import com.desierto.ranky.infrastructure.configuration.ConfigLoader;
import com.desierto.ranky.infrastructure.utils.DiscordOptionRetriever;
import java.util.List;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class DiscordRemoveAccountsServiceTest {

  public static final String RANKY_USER = "rankyUser";

  DiscordRemoveAccountsService cut;

  @Mock
  ConfigLoader config;

  @Mock
  DiscordOptionRetriever discordOptionRetriever;

  @Mock
  IAccountsService accountsService;

  @BeforeEach
  public void setUp() {
    cut = new DiscordRemoveAccountsService(config, discordOptionRetriever, accountsService);
    when(config.getRankyUserRole()).thenReturn(RANKY_USER);
  }

  @Test
  void onExecute_withoutRankyUserRole_doesNothingAndInforms() {
    SlashCommandInteractionEvent event = getAMockedEventWithMemberWithoutRole();
    String rankingId = "A ranking";
    when(discordOptionRetriever.fromEventGetObjectName(event)).thenReturn(rankingId);
    when(discordOptionRetriever.fromEventGetAccountListToAdd(event)).thenReturn(
        List.of(new Account()));

    cut.execute(event);

    verify(event.getHook(), times(1)).sendMessage(COMMAND_NOT_ALLOWED.getMessage());
  }

  @Test
  void onExecute_withEventNotComingFromAGuild_doesNothingAndInforms() {
    SlashCommandInteractionEvent event = getAMockedEventNotFromAGuild();
    String rankingId = "A ranking";
    Ranking ranking = new Ranking(rankingId);
    when(discordOptionRetriever.fromEventGetObjectName(event)).thenReturn(rankingId);
    when(discordOptionRetriever.fromEventGetAccountListToAdd(event)).thenReturn(
        List.of(new Account()));

    cut.execute(event);

    verify(event.getHook(), times(1)).sendMessage(EXECUTE_COMMAND_FROM_SERVER.getMessage());
  }

  @Test
  void onExecute_withoutAccountsToRemove_doesNothing() {
    SlashCommandInteractionEvent event = getAMockedEvent();
    String rankingId = "A ranking";
    when(discordOptionRetriever.fromEventGetObjectName(event)).thenReturn(rankingId);
    when(discordOptionRetriever.fromEventGetAccountListToAdd(event)).thenReturn(List.of());

    cut.execute(event);

    verify(event.getHook(), never()).sendMessage(anyString());
    verify(accountsService, never()).removeAccounts(anyString(), any(), anyList());
  }

  @Test
  void onExecute_withAccountsToRemove_removesAccountsAndInformsInHook() {
    SlashCommandInteractionEvent event = getAMockedEvent();
    String rankingId = "A ranking";
    Account BBXhadow = new Account("id", "BBXhadow", "RFF");
    when(discordOptionRetriever.fromEventGetObjectName(event)).thenReturn(rankingId);
    when(discordOptionRetriever.fromEventGetAccountListToRemove(event)).thenReturn(
        List.of(BBXhadow));

    cut.execute(event);

    verify(event.getHook(), times(1)).sendMessage("Accounts removed successfully!");
    verify(accountsService, times(1)).removeAccounts(rankingId, event.getGuild(),
        List.of(BBXhadow));
  }

  private SlashCommandInteractionEvent getAMockedEventWithMemberWithoutRole() {
    SlashCommandInteractionEvent event = mock(SlashCommandInteractionEvent.class);
    InteractionHook hook = mock(InteractionHook.class);
    Member member = mock(Member.class);
    WebhookMessageCreateAction wmca = mock(WebhookMessageCreateAction.class);
    when(event.getHook()).thenReturn(hook);
    when(event.getMember()).thenReturn(member);
    when(member.getRoles()).thenReturn(List.of());
    when(hook.sendMessage(anyString())).thenReturn(wmca);
    return event;
  }

  private SlashCommandInteractionEvent getAMockedEventNotFromAGuild() {
    SlashCommandInteractionEvent event = mock(SlashCommandInteractionEvent.class);
    InteractionHook hook = mock(InteractionHook.class);
    Member member = mock(Member.class);
    Role role = mock(Role.class);
    WebhookMessageCreateAction wmca = mock(WebhookMessageCreateAction.class);
    when(event.isFromGuild()).thenReturn(false);
    when(event.getHook()).thenReturn(hook);
    when(event.getMember()).thenReturn(member);
    when(member.getRoles()).thenReturn(List.of(role));
    when(role.getName()).thenReturn(RANKY_USER);
    when(hook.sendMessage(anyString())).thenReturn(wmca);
    return event;
  }

  private SlashCommandInteractionEvent getAMockedEvent() {
    SlashCommandInteractionEvent event = mock(SlashCommandInteractionEvent.class);
    InteractionHook hook = mock(InteractionHook.class);
    Member member = mock(Member.class);
    Role role = mock(Role.class);
    WebhookMessageCreateAction wmca = mock(WebhookMessageCreateAction.class);
    when(event.isFromGuild()).thenReturn(true);
    when(event.getHook()).thenReturn(hook);
    when(event.getMember()).thenReturn(member);
    when(member.getRoles()).thenReturn(List.of(role));
    when(role.getName()).thenReturn(RANKY_USER);
    when(hook.sendMessage(anyString())).thenReturn(wmca);
    return event;
  }
}
