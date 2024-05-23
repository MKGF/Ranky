package com.desierto.Ranky.infrastructure.service;

import com.desierto.Ranky.infrastructure.configuration.ConfigLoader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class HelpRankyService {

  @Autowired
  private ConfigLoader config;

  public void execute(SlashCommandInteractionEvent event) {
    InteractionHook hook = event.getHook();
    hook.setEphemeral(true);
    EmbedBuilder message = new EmbedBuilder();
    String formattedMessage = String.format(getFileContent(),
        config.getRankingLimit());
    message.setTitle("Ranky manual");
    message.setDescription(formattedMessage);
    hook.sendMessageEmbeds(message.build()).queue();

  }

  private String getFileContent() {
    try {
      return Files.readString(
          Paths.get(
              "src/main/java/com/desierto/Ranky/infrastructure/commands/helpRankyCommandResponse.txt"));
    } catch (IOException e) {
      return "Help message not retrieved correctly. Contact code owners please.";
    }
  }
}
