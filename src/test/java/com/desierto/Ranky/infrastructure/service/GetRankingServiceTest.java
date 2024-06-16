package com.desierto.Ranky.infrastructure.service;

import static com.desierto.Ranky.infrastructure.utils.DiscordMessages.EXECUTE_COMMAND_FROM_SERVER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.desierto.Ranky.application.AccountsCache;
import com.desierto.Ranky.domain.entity.Ranking;
import com.desierto.Ranky.domain.repository.RiotAccountRepository;
import com.desierto.Ranky.infrastructure.configuration.ConfigLoader;
import com.desierto.Ranky.infrastructure.repository.ConfigChannelRankingRepository;
import com.desierto.Ranky.infrastructure.utils.DiscordOptionRetriever;
import com.desierto.Ranky.infrastructure.utils.DiscordRankingFormatter;
import com.google.gson.Gson;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.restaction.MessageEditAction;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

  @Mock
  AccountsCache accountsCache;

  @Mock
  PrintRankingService printRankingService;

  private MockedConstruction<ConfigChannelRankingRepository> repo;

  @BeforeEach
  public void setUp() {
    gson = new Gson();
    cut = new GetRankingService(config, discordOptionRetriever, gson, riotAccountRepository,
        discordRankingFormatter, accountsCache, printRankingService);
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
  public void onEvent_withSinglePageRanking_returnsMessageWithFormattedRankingAndButton() {
    SlashCommandInteractionEvent event = getAMockedEvent();
    Ranking ranking = new Ranking("id");
    repo = mockDiscordRepo(ranking);
    when(discordOptionRetriever.fromEventGetObjectName(event)).thenReturn("id");
    when(discordRankingFormatter.formatRankingEntries(any())).thenReturn("formattedRanking");

    cut.execute(event);

    verify(printRankingService, times(1)).printSinglePage(eq(event), eq(ranking.getId()), anyList(),
        any());
  }

  //Case with bigger ranking pagination is correct and button have correct ids

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
    Interaction interaction = mock(Interaction.class);
    WebhookMessageCreateAction wmca = mock(WebhookMessageCreateAction.class);
    Guild guild = mock(Guild.class);
    Message message = mock(Message.class);
    MessageEditAction mea = mock(MessageEditAction.class);
    when(hook.getInteraction()).thenReturn(interaction);
    when(event.isFromGuild()).thenReturn(true);
    when(event.getGuild()).thenReturn(guild);
    when(interaction.getGuild()).thenReturn(guild);
    when(guild.getId()).thenReturn("guildId");
    when(event.getHook()).thenReturn(hook);
    when(hook.sendMessage(any(MessageCreateData.class))).thenReturn(wmca);
    when(hook.sendMessage(anyString())).thenReturn(wmca);
    when(wmca.complete()).thenReturn(message);
    when(message.editMessage(anyString())).thenReturn(mea);
    when(mea.complete()).thenReturn(message);
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
