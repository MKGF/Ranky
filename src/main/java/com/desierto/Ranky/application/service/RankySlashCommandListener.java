package com.desierto.Ranky.application.service;

import static com.desierto.Ranky.infrastructure.commands.Command.ADD_ACCOUNTS;
import static com.desierto.Ranky.infrastructure.commands.Command.CREATE;
import static com.desierto.Ranky.infrastructure.commands.Command.DELETE;
import static com.desierto.Ranky.infrastructure.commands.Command.HELP_RANKY;
import static com.desierto.Ranky.infrastructure.commands.Command.RANKING;
import static com.desierto.Ranky.infrastructure.commands.Command.REMOVE_ACCOUNTS;

import java.util.logging.Logger;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RankySlashCommandListener extends ListenerAdapter {

  @Autowired
  private HelpRankyService helpRankyService;
  @Autowired
  private GetRankingService getRankingService;
  @Autowired
  private CreateRankingService createRankingService;
  @Autowired
  private DeleteRankingService deleteRankingService;
  @Autowired
  private AddAccountsService addAccountsService;
  @Autowired
  private RemoveAccountsService removeAccountsService;

  public static final Logger log = Logger.getLogger("RankySlashCommandListener.class");

  @Override
  public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
    log.info("ENTERED SLASH COMMAND LISTENER");
    event.deferReply(true).queue();
    if (event.getCommandString().equals("/" + HELP_RANKY.getCommandId())) {
      helpRankyService.execute(event);
    }
    if (event.getCommandString().equals("/" + RANKING.getCommandId())) {
      getRankingService.execute(event);
    }
    if (event.getCommandString().equals("/" + CREATE.getCommandId())) {
      createRankingService.execute(event);
    }
    if (event.getCommandString().equals("/" + DELETE.getCommandId())) {
      deleteRankingService.execute(event);
    }
    if (event.getCommandString().equals("/" + ADD_ACCOUNTS.getCommandId())) {
      addAccountsService.execute(event);
    }
    if (event.getCommandString().equals("/" + REMOVE_ACCOUNTS.getCommandId())) {
      removeAccountsService.execute(event);
    }
  }
}
