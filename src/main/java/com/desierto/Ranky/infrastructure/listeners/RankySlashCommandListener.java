package com.desierto.Ranky.infrastructure.listeners;

import static com.desierto.Ranky.infrastructure.commands.Command.ADD_ACCOUNTS;
import static com.desierto.Ranky.infrastructure.commands.Command.CREATE;
import static com.desierto.Ranky.infrastructure.commands.Command.DELETE;
import static com.desierto.Ranky.infrastructure.commands.Command.HELP_RANKY;
import static com.desierto.Ranky.infrastructure.commands.Command.RANKING;
import static com.desierto.Ranky.infrastructure.commands.Command.REMOVE_ACCOUNTS;

import com.desierto.Ranky.infrastructure.service.AddAccountsService;
import com.desierto.Ranky.infrastructure.service.CreateRankingService;
import com.desierto.Ranky.infrastructure.service.DeleteRankingService;
import com.desierto.Ranky.infrastructure.service.GetRankingService;
import com.desierto.Ranky.infrastructure.service.HelpRankyService;
import com.desierto.Ranky.infrastructure.service.RemoveAccountsService;
import jakarta.annotation.PostConstruct;
import java.util.logging.Logger;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.JDA;
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

  @Autowired
  private JDA bot;

  public static final Logger log = Logger.getLogger("RankySlashCommandListener.class");


  @PostConstruct
  private void postConstruct() {
    bot.addEventListener(this);
    log.info(String.format("Added %s to the bot!", this.getClass().getName()));
  }

  @Override
  public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
    log.info("ENTERED SLASH COMMAND LISTENER");
    event.deferReply(true).queue();
    event.getHook().setEphemeral(true);
    if (event.getCommandString().contains("/" + HELP_RANKY.getCommandId())) {
      helpRankyService.execute(event);
    }
    if (event.getCommandString().contains("/" + RANKING.getCommandId())) {
      getRankingService.execute(event);
    }
    if (event.getCommandString().contains("/" + CREATE.getCommandId())) {
      createRankingService.execute(event);
    }
    if (event.getCommandString().contains("/" + DELETE.getCommandId())) {
      deleteRankingService.execute(event);
    }
    if (event.getCommandString().contains("/" + ADD_ACCOUNTS.getCommandId())) {
      addAccountsService.execute(event);
    }
    if (event.getCommandString().contains("/" + REMOVE_ACCOUNTS.getCommandId())) {
      removeAccountsService.execute(event);
    }
  }
}
