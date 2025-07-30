package com.desierto.ranky.infrastructure.listeners;

import static com.desierto.ranky.infrastructure.commands.Command.ADD_ACCOUNTS;
import static com.desierto.ranky.infrastructure.commands.Command.CREATE;
import static com.desierto.ranky.infrastructure.commands.Command.DELETE;
import static com.desierto.ranky.infrastructure.commands.Command.EXISTS_CONFIG_CHANNEL;
import static com.desierto.ranky.infrastructure.commands.Command.GET_ENROLLED_USERS;
import static com.desierto.ranky.infrastructure.commands.Command.GET_GUILDS;
import static com.desierto.ranky.infrastructure.commands.Command.HELP;
import static com.desierto.ranky.infrastructure.commands.Command.RANKING;
import static com.desierto.ranky.infrastructure.commands.Command.REMOVE_ACCOUNTS;
import static com.desierto.ranky.infrastructure.commands.Command.RETRIEVE_CONFIG_CHANNEL_CONTENT;

import com.desierto.ranky.infrastructure.service.DiscordAddAccountsService;
import com.desierto.ranky.infrastructure.service.DiscordCreateRankingService;
import com.desierto.ranky.infrastructure.service.DiscordDeleteRankingService;
import com.desierto.ranky.infrastructure.service.DiscordGetRankingService;
import com.desierto.ranky.infrastructure.service.DiscordRemoveAccountsService;
import com.desierto.ranky.infrastructure.service.HelpService;
import com.desierto.ranky.infrastructure.service.admin.ConfigChannelChecker;
import com.desierto.ranky.infrastructure.service.admin.ConfigChannelContentRetriever;
import com.desierto.ranky.infrastructure.service.admin.EnrolledUsersRetriever;
import com.desierto.ranky.infrastructure.service.admin.GuildRetriever;
import jakarta.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class RankySlashCommandListener extends ListenerAdapter {

  @Autowired
  private HelpService helpService;
  @Autowired
  private DiscordGetRankingService discordGetRankingService;
  @Autowired
  private DiscordCreateRankingService discordCreateRankingService;
  @Autowired
  private DiscordDeleteRankingService discordDeleteRankingService;
  @Autowired
  private DiscordAddAccountsService discordAddAccountsService;
  @Autowired
  private DiscordRemoveAccountsService discordRemoveAccountsService;

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


  @PostConstruct
  private void postConstruct() {
    bot.addEventListener(this);
    log.info(String.format("Added %s to the bot!", this.getClass().getName()));
  }

  @Override
  public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
    log.debug("ENTERED SLASH COMMAND LISTENER");
    event.deferReply(true).queue();
    event.getHook().setEphemeral(true);
    handleCommand(event);
  }

  private void handleCommand(SlashCommandInteractionEvent event) {
    if (event.getCommandString().contains("/" + HELP.getCommandId())) {
      executorService.execute(() -> helpService.execute(event));
    }
    if (event.getCommandString().contains("/" + RANKING.getCommandId())) {
      executorService.execute(() -> discordGetRankingService.execute(event));
    }
    if (event.getCommandString().contains("/" + CREATE.getCommandId())) {
      executorService.execute(() -> discordCreateRankingService.execute(event));
    }
    if (event.getCommandString().contains("/" + DELETE.getCommandId())) {
      executorService.execute(() -> discordDeleteRankingService.execute(event));
    }
    if (event.getCommandString().contains("/" + ADD_ACCOUNTS.getCommandId())) {
      executorService.execute(() -> discordAddAccountsService.execute(event));
    }
    if (event.getCommandString().contains("/" + REMOVE_ACCOUNTS.getCommandId())) {
      executorService.execute(() -> discordRemoveAccountsService.execute(event));
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
