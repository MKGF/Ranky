package com.desierto.Ranky.infrastructure;

import com.desierto.Ranky.infrastructure.configuration.ConfigLoader;
import java.util.logging.Logger;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Ranky extends SpringBootServletInitializer {

  public static String prefix = "/";
  public static final Logger log = Logger.getLogger("Ranky.class");

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
      checkPressenceInNewGuilds(bot, config);
    } catch (InterruptedException e) {
      SpringApplication.exit(context, () -> -1);
    }
  }

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder applicationBuilder) {
    return applicationBuilder.sources(Ranky.class);
  }

  private static void checkPressenceInNewGuilds(JDA bot, ConfigLoader config) {
    log.info(
        "JUST WOKEN UP. CHECKING MY SERVERS IN CASE OF MISSING CONFIG CHANNELS AND RANKY USER ROLES...");
    bot.getGuilds().forEach(guild -> {
      if (!guildHasRankyUserRole(config, guild)) {
        guild.createRole().setName(config.getRankyUserRole()).queue();
        log.info(String.format("Server: %s has no role. Creating it...", guild.getName()));
      }
      if (!guildHasConfigChannel(config, guild)) {
        guild.createTextChannel(config.getConfigChannel()).clearPermissionOverrides().queue();
        log.info(String.format("Server: %s has config channel. Creating it...", guild.getName()));
      }
    });
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
