package com.desierto.Ranky.infrastructure.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.desierto.Ranky.domain.utils.FileReader;
import com.desierto.Ranky.infrastructure.configuration.ConfigLoader;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class HelpRankyServiceTest {

  public static final String PATH_TO_HELP_RANKY_TXT = "src/main/resources/config/helpRankyCommandResponse.txt";

  HelpRankyService cut;

  @Mock
  ConfigLoader config;

  @BeforeAll
  public void setUp() {
    cut = new HelpRankyService(config);
  }

  @Test
  public void onExecute_buildsEmbedMessage_andQueuesMessageInInteractionHook() {
    EmbedBuilder message = new EmbedBuilder();
    String formattedMessage = String.format(FileReader.read(PATH_TO_HELP_RANKY_TXT),
        config.getRankingLimit());
    message.setTitle("Ranky manual");
    message.setDescription(formattedMessage);
    SlashCommandInteractionEvent event = mock(SlashCommandInteractionEvent.class);
    InteractionHook hook = mock(InteractionHook.class);
    WebhookMessageCreateAction wmca = mock(WebhookMessageCreateAction.class);
    when(event.getHook()).thenReturn(hook);
    when(hook.sendMessageEmbeds(message.build())).thenReturn(wmca);

    cut.execute(event);

    verify(hook, times(1)).sendMessageEmbeds(message.build());
  }
}
