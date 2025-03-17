package com.desierto.Ranky.infrastructure.service;

import static com.desierto.Ranky.infrastructure.utils.DiscordMessages.COMMAND_NOT_ALLOWED;
import static com.desierto.Ranky.infrastructure.utils.DiscordMessages.EXECUTE_COMMAND_FROM_SERVER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.desierto.Ranky.domain.entity.Account;
import com.desierto.Ranky.domain.entity.Ranking;
import com.desierto.Ranky.domain.service.IAccountsService;
import com.desierto.Ranky.infrastructure.configuration.ConfigLoader;
import com.desierto.Ranky.infrastructure.utils.DiscordOptionRetriever;
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
public class DiscordAddAccountsServiceTest {

  public static final String RANKY_USER = "rankyUser";
  DiscordAddAccountsService cut;

  @Mock
  ConfigLoader config;

  @Mock
  DiscordOptionRetriever discordOptionRetriever;

  @Mock
  IAccountsService accountsService;

  @BeforeEach
  public void setUp() {
    cut = new DiscordAddAccountsService(config, discordOptionRetriever, accountsService);
    when(config.getRankyUserRole()).thenReturn(RANKY_USER);
  }

  @Test
  public void onExecute_withoutRankyUserRole_doesNothingAndInforms() {
    SlashCommandInteractionEvent event = getAMockedEventWithMemberWithoutRole();
    String rankingName = "A ranking";
    when(discordOptionRetriever.fromEventGetObjectName(event)).thenReturn(rankingName);
    when(discordOptionRetriever.fromEventGetAccountList(event)).thenReturn(List.of(new Account()));

    cut.execute(event);

    verify(event.getHook(), times(1)).sendMessage(COMMAND_NOT_ALLOWED.getMessage());
    verify(accountsService, times(0)).addAccounts(anyString(), any(), anyList());
  }

  @Test
  public void onExecute_withEventNotComingFromAGuild_doesNothingAndInforms() {
    SlashCommandInteractionEvent event = getAMockedEventNotFromAGuild();
    String rankingName = "A ranking";
    when(discordOptionRetriever.fromEventGetObjectName(event)).thenReturn(rankingName);
    when(discordOptionRetriever.fromEventGetAccountList(event)).thenReturn(List.of(new Account()));

    cut.execute(event);

    verify(event.getHook(), times(1)).sendMessage(EXECUTE_COMMAND_FROM_SERVER.getMessage());
    verify(accountsService, times(0)).addAccounts(anyString(), any(), anyList());
  }

  @Test
  public void onExecute_withEmptyAccountList_doesNothing() {
    SlashCommandInteractionEvent event = getAMockedEvent();
    String rankingName = "A ranking";
    when(discordOptionRetriever.fromEventGetObjectName(event)).thenReturn(rankingName);
    when(discordOptionRetriever.fromEventGetAccountList(event)).thenReturn(List.of(new Account()));

    cut.execute(event);

    verify(event.getHook(), times(0)).sendMessage(anyString());
    verify(accountsService, times(0)).addAccounts(anyString(), any(), anyList());
  }

  @Test
  public void onExecute_withEnrichedWithIdAccountList_addsAccountToTheRankingAndInformsInHook() {
    SlashCommandInteractionEvent event = getAMockedEvent();
    String rankingName = "A ranking";
    Ranking ranking = new Ranking(rankingName);
    Account BBXhadow = new Account("BBXhadow", "RFF");
    Account enrichedBBXhadow = new Account("id", BBXhadow.getId(), BBXhadow.getTagLine());
    when(discordOptionRetriever.fromEventGetObjectName(event)).thenReturn(rankingName);
    when(discordOptionRetriever.fromEventGetAccountList(event)).thenReturn(List.of(BBXhadow));
    when(accountsService.addAccounts(rankingName, event.getGuild(), List.of(BBXhadow))).thenReturn(
        new Ranking(rankingName, List.of(enrichedBBXhadow)));

    cut.execute(event);

    verify(event.getHook(), times(1)).sendMessage("Accounts added successfully!");
    verify(accountsService, times(1)).addAccounts(rankingName, event.getGuild(), List.of(BBXhadow));
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
