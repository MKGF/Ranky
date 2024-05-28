package com.desierto.Ranky.infrastructure.utils;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class DiscordExceptionHandler {

  public static void handleExceptionOnSlashCommandEvent(RuntimeException ex,
      SlashCommandInteractionEvent event) {
    event.getHook().sendMessage(ex.getMessage()).queue();
    throw ex;
  }
}
