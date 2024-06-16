package com.desierto.Ranky.infrastructure.listeners;

import static com.desierto.Ranky.infrastructure.commands.Command.ADD_ACCOUNTS;
import static com.desierto.Ranky.infrastructure.commands.Command.CREATE;
import static com.desierto.Ranky.infrastructure.commands.Command.DELETE;
import static com.desierto.Ranky.infrastructure.commands.Command.EXISTS_CONFIG_CHANNEL;
import static com.desierto.Ranky.infrastructure.commands.Command.GET_ENROLLED_USERS;
import static com.desierto.Ranky.infrastructure.commands.Command.GET_GUILDS;
import static com.desierto.Ranky.infrastructure.commands.Command.HELP;
import static com.desierto.Ranky.infrastructure.commands.Command.RANKING;
import static com.desierto.Ranky.infrastructure.commands.Command.REMOVE_ACCOUNTS;
import static com.desierto.Ranky.infrastructure.commands.Command.RETRIEVE_CONFIG_CHANNEL_CONTENT;

import com.desierto.Ranky.infrastructure.service.AddAccountsService;
import com.desierto.Ranky.infrastructure.service.CreateRankingService;
import com.desierto.Ranky.infrastructure.service.DeleteRankingService;
import com.desierto.Ranky.infrastructure.service.GetRankingService;
import com.desierto.Ranky.infrastructure.service.HelpService;
import com.desierto.Ranky.infrastructure.service.RemoveAccountsService;
import com.desierto.Ranky.infrastructure.service.admin.ConfigChannelChecker;
import com.desierto.Ranky.infrastructure.service.admin.ConfigChannelContentRetriever;
import com.desierto.Ranky.infrastructure.service.admin.EnrolledUsersRetriever;
import com.desierto.Ranky.infrastructure.service.admin.GuildRetriever;
import jakarta.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
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
  private HelpService helpService;
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
  private GuildRetriever guildRetriever;

  @Autowired
  private EnrolledUsersRetriever enrolledUsersRetriever;

  @Autowired
  private ConfigChannelChecker configChannelChecker;

  @Autowired
  private ConfigChannelContentRetriever configChannelContentRetriever;

  @Autowired
  private ExecutorService executorService;

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
    if (event.getCommandString().contains("/" + HELP.getCommandId())) {
      executorService.execute(() -> helpService.execute(event));
    }
    if (event.getCommandString().contains("/" + RANKING.getCommandId())) {
      executorService.execute(() -> getRankingService.execute(event));

    }
    if (event.getCommandString().contains("/" + CREATE.getCommandId())) {
      executorService.execute(() -> createRankingService.execute(event));
    }
    if (event.getCommandString().contains("/" + DELETE.getCommandId())) {
      executorService.execute(() -> deleteRankingService.execute(event));
    }
    if (event.getCommandString().contains("/" + ADD_ACCOUNTS.getCommandId())) {
      executorService.execute(() -> addAccountsService.execute(event));
    }
    if (event.getCommandString().contains("/" + REMOVE_ACCOUNTS.getCommandId())) {
      executorService.execute(() -> removeAccountsService.execute(event));
    }
    if (event.getCommandString().contains("/" + GET_GUILDS.getCommandId())) {
      executorService.execute(() -> guildRetriever.execute(event));
    }
    if (event.getCommandString().contains("/" + GET_ENROLLED_USERS.getCommandId())) {
      executorService.execute(() -> enrolledUsersRetriever.execute(event));
    }
    if (event.getCommandString().contains("/" + EXISTS_CONFIG_CHANNEL.getCommandId())) {
      executorService.execute(() -> configChannelChecker.execute(event));
    }
    if (event.getCommandString().contains("/" + RETRIEVE_CONFIG_CHANNEL_CONTENT.getCommandId())) {
      executorService.execute(() -> configChannelContentRetriever.execute(event));
    }
  }
}
