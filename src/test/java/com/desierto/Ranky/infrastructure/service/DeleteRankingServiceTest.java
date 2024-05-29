package com.desierto.Ranky.infrastructure.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.desierto.Ranky.domain.exception.ranking.RankingCouldNotBeDeletedException;
import com.desierto.Ranky.infrastructure.configuration.ConfigLoader;
import com.desierto.Ranky.infrastructure.repository.ConfigChannelRankingRepository;
import com.google.gson.Gson;
import java.util.List;
import net.dv8tion.jda.api.entities.Guild;
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
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class DeleteRankingServiceTest {

  DeleteRankingService cut;

  @Mock
  ConfigLoader config;

  Gson gson;

  @BeforeAll
  public void setUp() {
    gson = new Gson();
    cut = new DeleteRankingService(config, gson);
  }

  @Test
  public void onNonGuildEvent_doNothing() {
    SlashCommandInteractionEvent event = mock(SlashCommandInteractionEvent.class);
    cut.execute(event);
  }

  @Test
  public void onEvent_deletesRankingAndInformsInHook() {
    SlashCommandInteractionEvent event = getMockedEvent();
    String rankingName = "Test";
    MockedConstruction<ConfigChannelRankingRepository> repo = Mockito.mockConstruction(
        ConfigChannelRankingRepository.class, (mock, context) -> {
          when(mock.delete(rankingName)).thenReturn(true);
        });
    cut.execute(event);
    verify(repo.constructed().get(0), times(1)).delete(rankingName);
    verify(event.getHook().sendMessage(anyString()), times(1)).queue();
    repo.close();
  }

  @Test
  public void onEvent_whenDeleteWasNotSuccessful_throwsException() {
    SlashCommandInteractionEvent event = getMockedEvent();
    String rankingName = "Test";
    MockedConstruction<ConfigChannelRankingRepository> repo = Mockito.mockConstruction(
        ConfigChannelRankingRepository.class, (mock, context) -> {
          when(mock.delete(rankingName)).thenReturn(false);
        });

    assertThrows(RankingCouldNotBeDeletedException.class, () -> cut.execute(event));
    verify(repo.constructed().get(0), times(1)).delete(rankingName);
    repo.close();
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
