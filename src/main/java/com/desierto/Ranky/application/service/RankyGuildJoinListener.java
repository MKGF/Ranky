package com.desierto.Ranky.application.service;

import static com.desierto.Ranky.domain.utils.FileReader.read;

import com.desierto.Ranky.infrastructure.configuration.ConfigLoader;
import java.util.logging.Logger;
import lombok.AllArgsConstructor;
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
  public ConfigLoader config;

  @Autowired
  public WelcomeGuildService welcomeGuildService;

  @Autowired
  public WelcomeOwnerService welcomeOwnerService;

  @Autowired
  public BotStatusUpdaterService botStatusUpdaterService;


  @Override
  public void onGuildJoin(GuildJoinEvent event) {
    Guild guild = event.getGuild();
    Member owner = guild.retrieveOwner().complete();
    String welcomeEmbedMessage = String.format(read(
            "src/main/java/com/desierto/Ranky/infrastructure/commands/onGuildJoinEmbedMessage.txt"),
        config.getRankyUserRole(),
        config.getConfigChannel(),
        config.getRankingLimit());
    String nonRiotEndorsementMessage = read(
        "src/main/java/com/desierto/Ranky/infrastructure/commands/nonRiotEndorsementMessage.txt");
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

  private void createRankyRole(GuildJoinEvent event) {
    event.getGuild().createRole().setName(config.getRankyUserRole()).queue();
  }

  private void createConfigChannel(GuildJoinEvent event) {
    event.getGuild().createTextChannel(config.getConfigChannel()).clearPermissionOverrides()
        .queue();
  }
}