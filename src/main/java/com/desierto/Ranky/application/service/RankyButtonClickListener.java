package com.desierto.Ranky.application.service;

import java.util.logging.Logger;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class RankyButtonClickListener extends ListenerAdapter {

  public static final Logger log = Logger.getLogger("RankyButtonClickListener.class");

  @Override
  public void onButtonInteraction(ButtonInteractionEvent event) {
    log.info("ENTERED BUTTON INTERACTION LISTENER");
    event.reply("Hi!").queue();
  }

}
