package com.desierto.Ranky.infrastructure.listeners;

import jakarta.annotation.PostConstruct;
import java.util.logging.Logger;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RankyButtonClickListener extends ListenerAdapter {

  public static final Logger log = Logger.getLogger("RankyButtonClickListener.class");

  @Autowired
  private JDA bot;

  @PostConstruct
  private void postConstruct() {
    bot.addEventListener(this);
    log.info(String.format("Added %s to the bot!", this.getClass().getName()));
  }

  @Override
  public void onButtonInteraction(ButtonInteractionEvent event) {
    log.info("ENTERED BUTTON INTERACTION LISTENER");
    event.reply("Hi!").queue();
  }

}
