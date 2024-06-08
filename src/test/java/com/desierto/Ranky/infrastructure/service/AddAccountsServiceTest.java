package com.desierto.Ranky.infrastructure.service;

import static com.desierto.Ranky.infrastructure.utils.DiscordMessages.COMMAND_NOT_ALLOWED;
import static com.desierto.Ranky.infrastructure.utils.DiscordMessages.EXECUTE_COMMAND_FROM_SERVER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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
public class AddAccountsServiceTest {

  public static final String RANKY_USER = "rankyUser";
  AddAccountsService cut;

  @Mock
  ConfigLoader config;

  Gson gson;

  @Mock
  RiotAccountRepository riotAccountRepository;

  @Mock
  DiscordOptionRetriever discordOptionRetriever;

  private MockedConstruction<ConfigChannelRankingRepository> repo;

  @BeforeEach
  public void setUp() {
    gson = new Gson();
    cut = new AddAccountsService(config, discordOptionRetriever, gson, riotAccountRepository);
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
    verify(riotAccountRepository, times(0)).enrichIdentification(any());
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
    verify(riotAccountRepository, times(0)).enrichIdentification(any());
  }

  @Test
  public void onExecute_withEmptyAccountList_doesNothing() {
    SlashCommandInteractionEvent event = getAMockedEvent();
    String rankingName = "A ranking";
    Ranking ranking = new Ranking(rankingName);
    when(discordOptionRetriever.fromEventGetRankingName(event)).thenReturn(rankingName);
    when(discordOptionRetriever.fromEventGetAccountList(event)).thenReturn(List.of(new Account()));
    repo = mockDiscordRepo(ranking);

    cut.execute(event);

    verify(event.getHook(), times(0)).sendMessage(anyString());
    verify(repo.constructed().get(0), times(1)).update(ranking);
    verify(riotAccountRepository, times(0)).enrichIdentification(any());
  }

  @Test
  public void onExecute_withAccountListThatCouldNotBeEnrichedWithId_informsInHookAndDoesNotModifyTheRanking() {
    SlashCommandInteractionEvent event = getAMockedEvent();
    String rankingName = "A ranking";
    Ranking ranking = new Ranking(rankingName);
    Account delusionalTB = new Account("Delusional TB", "delu");
    when(discordOptionRetriever.fromEventGetRankingName(event)).thenReturn(rankingName);
    when(discordOptionRetriever.fromEventGetAccountList(event)).thenReturn(List.of(delusionalTB));
    when(riotAccountRepository.enrichIdentification(delusionalTB)).thenReturn(delusionalTB);
    repo = mockDiscordRepo(ranking);

    cut.execute(event);

    assertEquals(ranking.getAccounts().size(), 0);
    verify(event.getHook(), times(1)).sendMessage(
        "Couldn't retrieve accountId for the following account: "
            + delusionalTB.getNameAndTagLine());
    verify(repo.constructed().get(0), times(1)).update(ranking);
    verify(riotAccountRepository, times(1)).enrichIdentification(delusionalTB);
  }

  @Test
  public void onExecute_withEnrichedWithIdAccountList_addsAccountToTheRankingAndInformsInHook() {
    SlashCommandInteractionEvent event = getAMockedEvent();
    String rankingName = "A ranking";
    Ranking ranking = new Ranking(rankingName);
    Account BBXhadow = new Account("BBXhadow", "RFF");
    Account enrichedBBXhadow = new Account("id", BBXhadow.getId(), BBXhadow.getTagLine());
    when(discordOptionRetriever.fromEventGetRankingName(event)).thenReturn(rankingName);
    when(discordOptionRetriever.fromEventGetAccountList(event)).thenReturn(List.of(BBXhadow));
    when(riotAccountRepository.enrichIdentification(BBXhadow)).thenReturn(enrichedBBXhadow);
    repo = mockDiscordRepo(ranking);

    cut.execute(event);

    assertEquals(ranking.getAccounts().size(), 1);
    verify(event.getHook(), times(1)).sendMessage("Accounts added successfully!");
    verify(repo.constructed().get(0), times(1)).update(ranking);
    verify(riotAccountRepository, times(1)).enrichIdentification(BBXhadow);
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
