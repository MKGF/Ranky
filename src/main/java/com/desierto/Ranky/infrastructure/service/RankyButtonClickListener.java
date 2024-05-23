package com.desierto.Ranky.infrastructure.service;

import java.util.logging.Logger;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RankyButtonClickListener extends ListenerAdapter {

  public static final Logger log = Logger.getLogger("RankyButtonClickListener.class");

  @Override
  public void onButtonInteraction(ButtonInteractionEvent event) {
    log.info("ENTERED BUTTON INTERACTION LISTENER");
    event.reply("Hi!").queue();
  }

}
