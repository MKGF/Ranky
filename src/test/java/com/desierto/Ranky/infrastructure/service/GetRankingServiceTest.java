package com.desierto.Ranky.infrastructure.service;

import static com.desierto.Ranky.infrastructure.utils.DiscordMessages.EXECUTE_COMMAND_FROM_SERVER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.desierto.Ranky.domain.entity.Ranking;
import com.desierto.Ranky.domain.repository.RiotAccountRepository;
import com.desierto.Ranky.infrastructure.configuration.ConfigLoader;
import com.desierto.Ranky.infrastructure.repository.ConfigChannelRankingRepository;
import com.desierto.Ranky.infrastructure.utils.DiscordOptionRetriever;
import com.desierto.Ranky.infrastructure.utils.DiscordRankingFormatter;
import com.google.gson.Gson;
import java.util.List;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class GetRankingServiceTest {

  GetRankingService cut;

  @Mock
  ConfigLoader config;

  @Mock
  DiscordOptionRetriever discordOptionRetriever;

  Gson gson;

  @Mock
  RiotAccountRepository riotAccountRepository;

  @Mock
  DiscordRankingFormatter discordRankingFormatter;

  private MockedConstruction<ConfigChannelRankingRepository> repo;

  @BeforeEach
  public void setUp() {
    gson = new Gson();
    cut = new GetRankingService(config, discordOptionRetriever, gson, riotAccountRepository,
        discordRankingFormatter);
  }

  @AfterEach
  public void tearDown() {
    repo.close();
  }

  @Test
  public void onEvent_whenNotFromGuild_doesNothingAndInforms() {
    SlashCommandInteractionEvent event = getAMockedEventNotFromAGuild();
    Ranking ranking = new Ranking("");
    repo = mockDiscordRepo(ranking);
    cut.execute(event);

    verify(event.getHook(), times(1)).sendMessage(EXECUTE_COMMAND_FROM_SERVER.getMessage());
  }

  @Test
  public void onEvent_returnsMessageWithFormattedRankingAndButton() {
    SlashCommandInteractionEvent event = getAMockedEvent();
    Ranking ranking = new Ranking("id");
    String formattedRanking = "formattedRanking";
    repo = mockDiscordRepo(ranking);
    when(discordOptionRetriever.fromEventGetRankingName(event)).thenReturn("id");
    when(riotAccountRepository.enrichWithSoloQStats(anyList())).thenReturn(List.of());
    when(discordRankingFormatter.formatRankingEntries(any(), any())).thenReturn("formattedRanking");

    cut.execute(event);

    ArgumentCaptor<MessageCreateData> captor = ArgumentCaptor.captor();
    verify(event.getHook(), times(1)).sendMessage(captor.capture());
    assertThat(captor.getValue().getContent().equals(formattedRanking)).isTrue();
    assertThat(captor.getValue().getComponents().get(0) instanceof ActionRow).isTrue();
    assertThat(captor.getValue().getComponents().get(0).getButtons().size() > 0).isTrue();
  }

  private SlashCommandInteractionEvent getAMockedEventNotFromAGuild() {
    SlashCommandInteractionEvent event = mock(SlashCommandInteractionEvent.class);
    InteractionHook hook = mock(InteractionHook.class);
    WebhookMessageCreateAction wmca = mock(WebhookMessageCreateAction.class);
    when(event.isFromGuild()).thenReturn(false);
    when(event.getHook()).thenReturn(hook);
    when(hook.sendMessage(anyString())).thenReturn(wmca);
    return event;
  }

  private SlashCommandInteractionEvent getAMockedEvent() {
    SlashCommandInteractionEvent event = mock(SlashCommandInteractionEvent.class);
    InteractionHook hook = mock(InteractionHook.class);
    WebhookMessageCreateAction wmca = mock(WebhookMessageCreateAction.class);
    when(event.isFromGuild()).thenReturn(true);
    when(event.getHook()).thenReturn(hook);
    when(hook.sendMessage(any(MessageCreateData.class))).thenReturn(wmca);
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
