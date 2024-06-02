package com.desierto.Ranky.infrastructure.service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.desierto.Ranky.domain.entity.Ranking;
import com.desierto.Ranky.infrastructure.configuration.ConfigLoader;
import com.desierto.Ranky.infrastructure.repository.ConfigChannelRankingRepository;
import com.desierto.Ranky.infrastructure.utils.DiscordOptionRetriever;
import com.google.gson.Gson;
import java.util.List;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class CreateRankingServiceTest {

  CreateRankingService cut;

  @Mock
  ConfigLoader config;

  Gson gson;

  @Mock
  DiscordOptionRetriever discordOptionRetriever;

  @BeforeEach
  public void setUp() {
    gson = new Gson();
    cut = new CreateRankingService(config, gson, discordOptionRetriever);
  }

  @Test
  public void onNonGuildEvent_doNothing() {
    SlashCommandInteractionEvent event = mock(SlashCommandInteractionEvent.class);
    cut.execute(event);
  }

  @Test
  public void onEvent_createsRankingAndInformsInHook() {
    SlashCommandInteractionEvent event = getMockedEvent();
    String rankingName = "Test";
    Ranking ranking = new Ranking(rankingName);
    MockedConstruction<ConfigChannelRankingRepository> repo = Mockito.mockConstruction(
        ConfigChannelRankingRepository.class, (mock, context) -> {
          when(mock.update(ranking)).thenReturn(ranking);
        });
    cut.execute(event);
    verify(repo.constructed().get(0), times(1)).update(ranking);
    verify(event.getHook().sendMessage(anyString()), times(1)).queue();
  }

  private SlashCommandInteractionEvent getMockedEvent() {
    SlashCommandInteractionEvent event = mock(SlashCommandInteractionEvent.class);
    Guild guild = mock(Guild.class);
    InteractionHook hook = mock(InteractionHook.class);
    WebhookMessageCreateAction wmca = mock(WebhookMessageCreateAction.class);
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
