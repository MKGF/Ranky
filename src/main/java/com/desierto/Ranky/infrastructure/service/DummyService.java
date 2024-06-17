package com.desierto.Ranky.infrastructure.service;

import static com.desierto.Ranky.infrastructure.utils.DiscordMessages.NOT_A_SERVER_COMMAND;

import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DummyService {

  public void execute(SlashCommandInteractionEvent event) {
    if (!event.isFromGuild()) {
      InteractionHook hook = event.getHook();
      EmbedBuilder message = new EmbedBuilder();
      String formattedMessage = System.getProperty("user.dir");
      message.setDescription(formattedMessage);
      hook.sendMessageEmbeds(message.build()).queue();
    } else {
      event.getHook().sendMessage(NOT_A_SERVER_COMMAND.getMessage()).queue();
    }
  }
}
