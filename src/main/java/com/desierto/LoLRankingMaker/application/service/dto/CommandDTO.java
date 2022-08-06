package com.desierto.LoLRankingMaker.application.service.dto;

import com.desierto.LoLRankingMaker.infrastructure.Ranky;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandDTO extends ListenerAdapter {

  @Override
  public void onMessageReceived(MessageReceivedEvent event) {
    if (event.getMessage().getContentRaw().startsWith(Ranky.prefix)) {
      
    }
  }

}
