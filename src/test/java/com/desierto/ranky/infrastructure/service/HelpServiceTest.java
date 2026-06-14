package com.desierto.ranky.infrastructure.service;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.desierto.ranky.domain.utils.FileReader;
import com.desierto.ranky.infrastructure.configuration.ConfigLoader;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class HelpServiceTest {

  public static final String PATH_TO_HELP_TXT = "src/main/resources/config/helpCommandResponse.txt";

  @InjectMocks
  HelpService cut;

  @Mock
  ConfigLoader config;

  @BeforeEach
  void setPathToHelpTxt() {
    lenient().when(config.getPathToHelpMessage()).thenReturn(PATH_TO_HELP_TXT);
  }

  @Test
  void onExecute_buildsEmbedMessage_andQueuesMessageInInteractionHook() {
    EmbedBuilder message = new EmbedBuilder();
    String formattedMessage = String.format(FileReader.read(PATH_TO_HELP_TXT),
        config.getRankingLimit());
    message.setTitle("Ranky manual");
    message.setDescription(formattedMessage);
    SlashCommandInteractionEvent event = mock(SlashCommandInteractionEvent.class);
    InteractionHook hook = mock(InteractionHook.class);
    WebhookMessageCreateAction wmca = mock(WebhookMessageCreateAction.class);
    when(event.getHook()).thenReturn(hook);
    when(event.isFromGuild()).thenReturn(true);
    when(hook.sendMessageEmbeds(message.build())).thenReturn(wmca);

    cut.execute(event);

    verify(hook, times(1)).sendMessageEmbeds(message.build());
  }
}
