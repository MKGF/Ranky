package com.desierto.Ranky.infrastructure.service;

import static com.desierto.Ranky.infrastructure.utils.DiscordMessages.EXECUTE_COMMAND_FROM_SERVER;

import com.desierto.Ranky.domain.utils.FileReader;
import com.desierto.Ranky.infrastructure.configuration.ConfigLoader;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class HelpRankyService {

  public static final String PATH_TO_HELP_RANKY_TXT = "src/main/resources/config/helpRankyCommandResponse.txt";
  @Autowired
  private ConfigLoader config;

  public void execute(SlashCommandInteractionEvent event) {
    if (event.isFromGuild()) {
      InteractionHook hook = event.getHook();
      EmbedBuilder message = new EmbedBuilder();
      String formattedMessage = String.format(FileReader.read(PATH_TO_HELP_RANKY_TXT),
          config.getRankingLimit());
      message.setTitle("Ranky manual");
      message.setDescription(formattedMessage);
      hook.sendMessageEmbeds(message.build()).queue();
    } else {
      event.getHook().sendMessage(EXECUTE_COMMAND_FROM_SERVER.getMessage()).queue();
    }
  }
}
