package com.desierto.Ranky.infrastructure.listeners;

import jakarta.annotation.PostConstruct;
import java.util.logging.Logger;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class RankyMessageListener extends ListenerAdapter {

  public static final Logger log = Logger.getLogger("RankyMessageListener.class");

  @Autowired
  private JDA bot;

  @PostConstruct
  private void postConstruct() {
    bot.addEventListener(this);
    log.info(String.format("Added %s to the bot!", this.getClass().getName()));
  }

  @Override
  public void onMessageReceived(MessageReceivedEvent event) {

  }
}

