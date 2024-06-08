package com.desierto.Ranky.infrastructure.service;

import static com.desierto.Ranky.infrastructure.utils.DiscordMessages.COMMAND_NOT_ALLOWED;
import static com.desierto.Ranky.infrastructure.utils.DiscordMessages.EXECUTE_COMMAND_FROM_SERVER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.desierto.Ranky.domain.entity.Account;
import com.desierto.Ranky.domain.entity.Ranking;
import com.desierto.Ranky.domain.repository.RiotAccountRepository;
import com.desierto.Ranky.infrastructure.configuration.ConfigLoader;
import com.desierto.Ranky.infrastructure.repository.ConfigChannelRankingRepository;
import com.desierto.Ranky.infrastructure.utils.DiscordOptionRetriever;
import com.google.gson.Gson;
import java.util.List;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class RemoveAccountsServiceTest {

  public static final String RANKY_USER = "rankyUser";

  RemoveAccountsService cut;

  @Mock
  ConfigLoader config;

  Gson gson;


  @Mock
  DiscordOptionRetriever discordOptionRetriever;

  @Mock
  RiotAccountRepository riotAccountRepository;

  private MockedConstruction<ConfigChannelRankingRepository> repo;

  @BeforeEach
  public void setUp() {
    gson = new Gson();
    cut = new RemoveAccountsService(config, discordOptionRetriever, gson, riotAccountRepository);
    when(config.getRankyUserRole()).thenReturn(RANKY_USER);
  }

  @AfterEach
  public void tearDown() {
    repo.close();
  }

  @Test
  public void onExecute_withoutRankyUserRole_doesNothingAndInforms() {
    SlashCommandInteractionEvent event = getAMockedEventWithMemberWithoutRole();
    String rankingName = "A ranking";
    Ranking ranking = new Ranking(rankingName);
    when(discordOptionRetriever.fromEventGetRankingName(event)).thenReturn(rankingName);
    when(discordOptionRetriever.fromEventGetAccountList(event)).thenReturn(List.of(new Account()));
    repo = mockDiscordRepo(ranking);

    cut.execute(event);

    verify(event.getHook(), times(1)).sendMessage(COMMAND_NOT_ALLOWED.getMessage());
  }

  @Test
  public void onExecute_withEventNotComingFromAGuild_doesNothingAndInforms() {
    SlashCommandInteractionEvent event = getAMockedEventNotFromAGuild();
    String rankingName = "A ranking";
    Ranking ranking = new Ranking(rankingName);
    when(discordOptionRetriever.fromEventGetRankingName(event)).thenReturn(rankingName);
    when(discordOptionRetriever.fromEventGetAccountList(event)).thenReturn(List.of(new Account()));
    repo = mockDiscordRepo(ranking);

    cut.execute(event);

    verify(event.getHook(), times(1)).sendMessage(EXECUTE_COMMAND_FROM_SERVER.getMessage());
  }

  @Test
  public void onExecute_withoutAccountsToRemove_doesNothing() {
    SlashCommandInteractionEvent event = getAMockedEvent();
    String rankingName = "A ranking";
    Ranking ranking = new Ranking(rankingName);
    when(discordOptionRetriever.fromEventGetRankingName(event)).thenReturn(rankingName);
    when(discordOptionRetriever.fromEventGetAccountList(event)).thenReturn(List.of(new Account()));
    repo = mockDiscordRepo(ranking);

    cut.execute(event);

    verify(event.getHook(), times(0)).sendMessage(anyString());
    verify(repo.constructed().get(0), times(1)).update(ranking);
  }

  @Test
  public void onExecute_withAccountsToRemove_removesAccountsAndInformsInHook() {
    SlashCommandInteractionEvent event = getAMockedEvent();
    String rankingName = "A ranking";
    Account BBXhadow = new Account("id", "BBXhadow", "RFF");
    Ranking ranking = new Ranking(rankingName, List.of(BBXhadow));
    when(discordOptionRetriever.fromEventGetRankingName(event)).thenReturn(rankingName);
    when(discordOptionRetriever.fromEventGetAccountList(event)).thenReturn(List.of(BBXhadow));
    when(riotAccountRepository.enrichIdentification(BBXhadow)).thenReturn(BBXhadow);
    repo = mockDiscordRepo(ranking);

    cut.execute(event);

    assertEquals(ranking.getAccounts().size(), 0);
    verify(event.getHook(), times(1)).sendMessage("Accounts removed successfully!");
    verify(repo.constructed().get(0), times(1)).update(ranking);
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

  private MockedConstruction<ConfigChannelRankingRepository> mockDiscordRepo(Ranking ranking) {
    return Mockito.mockConstruction(
        ConfigChannelRankingRepository.class, (mock, context) -> {
          when(mock.update(ranking)).thenReturn(ranking);
          when(mock.read(ranking.getId())).thenReturn(ranking);
        });
  }
}
