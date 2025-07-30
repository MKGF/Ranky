package com.desierto.ranky.infrastructure;

import static com.desierto.ranky.domain.utils.FileReader.read;

import com.desierto.ranky.infrastructure.configuration.ConfigLoader;
import com.desierto.ranky.infrastructure.service.BotStatusUpdaterService;
import com.desierto.ranky.infrastructure.service.WelcomeGuildService;
import com.desierto.ranky.infrastructure.service.WelcomeOwnerService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@Slf4j
@EnableFeignClients
public class Ranky extends SpringBootServletInitializer {

  public static final String PATH_TO_EMBED_MESSAGE_TXT = "src/main/resources/config/onGuildJoinEmbedMessage.txt";
  public static final String PATH_TO_NON_RIOT_ENDORSEMENT_MESSAGE_TXT = "src/main/resources/config/nonRiotEndorsementMessage.txt";

  public static void main(String[] args) {
    ConfigurableApplicationContext context = SpringApplication
        .run(Ranky.class, args);
    setupDiscordBot(context);
  }

  private static void setupDiscordBot(ConfigurableApplicationContext context) {
    JDA bot = context.getBean(JDA.class);
    ConfigLoader config = context.getBean(ConfigLoader.class);
    try {
      bot.awaitReady();
      checkPressenceInNewGuilds(bot, config, context);
    } catch (InterruptedException e) {
      SpringApplication.exit(context, () -> -1);
    }
  }

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder applicationBuilder) {
    return applicationBuilder.sources(Ranky.class);
  }

  private static void checkPressenceInNewGuilds(JDA bot, ConfigLoader config,
      ConfigurableApplicationContext context) {
    log.info(
        "JUST WOKEN UP. CHECKING MY SERVERS IN CASE OF MISSING CONFIG CHANNELS AND RANKY USER ROLES...");
    bot.getGuilds().forEach(guild -> {
      boolean isNew = false;
      WelcomeGuildService welcomeGuildService;
      WelcomeOwnerService welcomeOwnerService;
      if (!guildHasRankyUserRole(config, guild)) {
        isNew = true;
        guild.createRole().setName(config.getRankyUserRole()).queue();
        log.info(String.format("Server: %s has no role. Creating it...", guild.getName()));
      }
      if (!guildHasConfigChannel(config, guild)) {
        isNew = true;
        guild.createTextChannel(config.getConfigChannel()).clearPermissionOverrides().queue();
        log.info(String.format("Server: %s has config channel. Creating it...", guild.getName()));
      }
      if (isNew) {
        welcomeGuildService = context.getBean(WelcomeGuildService.class);
        welcomeOwnerService = context.getBean(WelcomeOwnerService.class);
        Member owner = guild.retrieveOwner().complete();
        String welcomeEmbedMessage = String.format(read(
                PATH_TO_EMBED_MESSAGE_TXT),
            config.getRankyUserRole(),
            config.getConfigChannel(),
            config.getRankingLimit());
        String nonRiotEndorsementMessage = read(
            PATH_TO_NON_RIOT_ENDORSEMENT_MESSAGE_TXT);
        welcomeGuildService.execute(guild, welcomeEmbedMessage, nonRiotEndorsementMessage);
        welcomeOwnerService.execute(guild, owner, welcomeEmbedMessage, nonRiotEndorsementMessage);
      }
    });

    context.getBean(BotStatusUpdaterService.class).execute();
  }

  private static boolean guildHasConfigChannel(ConfigLoader config, Guild guild) {
    return guild.getTextChannels().stream()
        .anyMatch(textChannel -> textChannel.getName().equals(config.getConfigChannel()));
  }

  private static boolean guildHasRankyUserRole(ConfigLoader config, Guild guild) {
    return guild.getRoles().stream()
        .anyMatch(role -> role.getName().equals(config.getRankyUserRole()));
  }
}
