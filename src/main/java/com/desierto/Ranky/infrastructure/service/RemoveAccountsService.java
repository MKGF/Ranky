package com.desierto.Ranky.infrastructure.service;

import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RemoveAccountsService {

  public void execute(SlashCommandInteractionEvent event) {
    InteractionHook hook = event.getHook();

  }

}
