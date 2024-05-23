package com.desierto.Ranky.application.service;

import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BotStatusUpdaterService {
  
  @Autowired
  public JDA jda;

  public void execute() {
    jda.getPresence().setActivity(
        Activity
            .customStatus(
                "Currently at " + jda.getGuilds().size() + " different servers."));
  }
}
