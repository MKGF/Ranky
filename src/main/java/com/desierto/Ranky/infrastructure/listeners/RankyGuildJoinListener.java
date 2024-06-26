package com.desierto.Ranky.infrastructure.listeners;

import static com.desierto.Ranky.domain.utils.FileReader.read;

import com.desierto.Ranky.infrastructure.configuration.ConfigLoader;
import com.desierto.Ranky.infrastructure.service.BotStatusUpdaterService;
import com.desierto.Ranky.infrastructure.service.WelcomeGuildService;
import com.desierto.Ranky.infrastructure.service.WelcomeOwnerService;
import jakarta.annotation.PostConstruct;
import java.util.logging.Logger;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class RankyGuildJoinListener extends ListenerAdapter {

  public static final Logger log = Logger.getLogger("RankyGuildJoinListener.class");

  @Autowired
  private ConfigLoader config;

  @Autowired
  private JDA bot;

  @Autowired
  private WelcomeGuildService welcomeGuildService;

  @Autowired
  private WelcomeOwnerService welcomeOwnerService;

  @Autowired
  private BotStatusUpdaterService botStatusUpdaterService;

  @PostConstruct
  private void postConstruct() {
    bot.addEventListener(this);
    log.info(String.format("Added %s to the bot!", this.getClass().getName()));
  }

  @Override
  public void onGuildJoin(GuildJoinEvent event) {
    Guild guild = event.getGuild();
    Member owner = guild.retrieveOwner().complete();
    String welcomeEmbedMessage = String.format(read(
            config.getPathToEmbedMessage()),
        config.getRankyUserRole(),
        config.getConfigChannel(),
        config.getRankingLimit());
    String nonRiotEndorsementMessage = read(
        config.getPathToNonRiotEndorsementMessage());
    log.info("JOINED GUILD: " + event.getGuild().getName());
    if (owner != null) {
      log.info(
          "OWNER OF THE GUILD: " + owner.getUser().getName() + "/" + owner.getUser()
              .getId());
    } else {
      log.info("GUILD HAS NO OWNER.");
    }

    botStatusUpdaterService.execute();

    createConfigChannel(event);
    createRankyRole(event);
    welcomeGuildService.execute(guild, welcomeEmbedMessage, nonRiotEndorsementMessage);
    welcomeOwnerService.execute(guild, owner, welcomeEmbedMessage, nonRiotEndorsementMessage);

  }

  private static boolean guildHasConfigChannel(ConfigLoader config, Guild guild) {
    return guild.getTextChannels().stream()
        .anyMatch(textChannel -> textChannel.getName().equals(config.getConfigChannel()));
  }

  private static boolean guildHasRankyUserRole(ConfigLoader config, Guild guild) {
    return guild.getRoles().stream()
        .anyMatch(role -> role.getName().equals(config.getRankyUserRole()));
  }

  private void createRankyRole(GuildJoinEvent event) {
    if (!guildHasRankyUserRole(config, event.getGuild())) {
      event.getGuild().createRole().setName(config.getRankyUserRole()).queue();
    }
  }

  private void createConfigChannel(GuildJoinEvent event) {
    if (!guildHasConfigChannel(config, event.getGuild())) {
      event.getGuild().createTextChannel(config.getConfigChannel()).clearPermissionOverrides()
          .queue();
    }
  }
}