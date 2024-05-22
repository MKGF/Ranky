package com.desierto.Ranky.application.service;

import java.util.logging.Logger;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class RankySlashCommandListener extends ListenerAdapter {

  public static final Logger log = Logger.getLogger("RankySlashCommandListener.class");

  @Override
  public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
    log.info("ENTERED SLASH COMMAND LISTENER");
    event.deferReply(true).queue();
    InteractionHook hook = event.getHook();
    hook.setEphemeral(true);
    hook.sendMessage("Hello! If you want to say hi to the whole guild, click the button.")
        .addActionRow(
            Button.primary("Make public", Emoji.fromFormatted("<:github:849286315580719104>"))
                .asEnabled().withLabel("Make public")
        ).queue();
  }
}
