package com.desierto.Ranky.infrastructure.service;

import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BotStatusUpdaterService {

  @Autowired
  public JDA bot;

  public void execute() {
    bot.getPresence().setActivity(
        Activity
            .customStatus(
                "Currently at " + bot.getGuilds().size() + " different servers.")
            .withState("Vibing"));
  }
}
