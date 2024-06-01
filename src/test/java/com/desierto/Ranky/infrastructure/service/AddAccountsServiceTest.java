package com.desierto.Ranky.infrastructure.service;

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
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class AddAccountsServiceTest {

  AddAccountsService cut;

  @Mock
  ConfigLoader config;

  Gson gson;

  @Mock
  RiotAccountRepository riotAccountRepository;

  private MockedStatic<DiscordOptionRetriever> dor;

  private MockedConstruction<ConfigChannelRankingRepository> repo;

  @BeforeAll
  public void setUp() {
    gson = new Gson();
    cut = new AddAccountsService(config, gson, riotAccountRepository);
  }

  @AfterEach
  public void tearDown() {
    dor.close();
    repo.close();
  }

  @Test
  public void onExecute_withEmptyAccountList_doesNothing() {
    SlashCommandInteractionEvent event = getAMockedEvent();
    String rankingName = "A ranking";
    Ranking ranking = new Ranking(rankingName);
    setupDiscordOptionRetriever(rankingName, List.of(new Account()), event);
    repo = mockDiscordRepo(ranking);

    cut.execute(event);

    verify(event.getHook(), times(0)).sendMessage(anyString());
    verify(repo.constructed().get(0), times(1)).update(ranking);
    verify(riotAccountRepository, times(0)).enrichWithId(any());
  }

  @Test
  public void onExecute_withAccountListThatCouldNotBeEnrichedWithId_informsInHookAndDoesNotModifyTheRanking() {
    SlashCommandInteractionEvent event = getAMockedEvent();
    String rankingName = "A ranking";
    Ranking ranking = new Ranking(rankingName);
    Account delusionalTB = new Account("Delusional TB", "delu");
    setupDiscordOptionRetriever(rankingName, List.of(delusionalTB), event);
    when(riotAccountRepository.enrichWithId(delusionalTB)).thenReturn(delusionalTB);
    repo = mockDiscordRepo(ranking);

    cut.execute(event);

    assertEquals(ranking.getAccounts().size(), 0);
    verify(event.getHook(), times(1)).sendMessage(
        "Couldn't retrieve accountId for the following account: "
            + delusionalTB.getNameAndTagLine());
    verify(repo.constructed().get(0), times(1)).update(ranking);
    verify(riotAccountRepository, times(1)).enrichWithId(delusionalTB);
  }

  @Test
  public void onExecute_withEnrichedWithIdAccountList_addsAccountToTheRankingAndInformsInHook() {
    SlashCommandInteractionEvent event = getAMockedEvent();
    String rankingName = "A ranking";
    Ranking ranking = new Ranking(rankingName);
    Account BBXhadow = new Account("BBXhadow", "RFF");
    Account enrichedBBXhadow = new Account("id", BBXhadow.getId(), BBXhadow.getTagLine());
    setupDiscordOptionRetriever(rankingName, List.of(BBXhadow), event);
    when(riotAccountRepository.enrichWithId(BBXhadow)).thenReturn(enrichedBBXhadow);
    repo = mockDiscordRepo(ranking);

    cut.execute(event);

    assertEquals(ranking.getAccounts().size(), 1);
    verify(event.getHook(), times(1)).sendMessage("Accounts added successfully!");
    verify(repo.constructed().get(0), times(1)).update(ranking);
    verify(riotAccountRepository, times(1)).enrichWithId(BBXhadow);
  }

  private SlashCommandInteractionEvent getAMockedEvent() {
    SlashCommandInteractionEvent event = mock(SlashCommandInteractionEvent.class);
    InteractionHook hook = mock(InteractionHook.class);
    WebhookMessageCreateAction wmca = mock(WebhookMessageCreateAction.class);
    when(event.getHook()).thenReturn(hook);
    when(hook.sendMessage(anyString())).thenReturn(wmca);
    return event;
  }

  private void setupDiscordOptionRetriever(String rankingName, List<Account> accounts,
      SlashCommandInteractionEvent event) {
    dor = Mockito.mockStatic(DiscordOptionRetriever.class);
    dor.when(() -> DiscordOptionRetriever.fromEventGetRankingName(event))
        .thenReturn(rankingName);
    dor.when(() -> DiscordOptionRetriever.fromEventGetAccountList(event))
        .thenReturn(accounts);
  }

  private MockedConstruction<ConfigChannelRankingRepository> mockDiscordRepo(Ranking ranking) {
    return Mockito.mockConstruction(
        ConfigChannelRankingRepository.class, (mock, context) -> {
          when(mock.update(ranking)).thenReturn(ranking);
          when(mock.find(ranking.getId())).thenReturn(ranking);
        });
  }
}
