package com.desierto.Ranky.infrastructure;

import com.desierto.Ranky.application.service.RankyButtonClickListener;
import com.desierto.Ranky.application.service.RankyGuildJoinListener;
import com.desierto.Ranky.application.service.RankyMessageListener;
import com.desierto.Ranky.application.service.RankySlashCommandListener;
import com.desierto.Ranky.infrastructure.commands.Command;
import com.desierto.Ranky.infrastructure.configuration.ConfigLoader;
import java.util.logging.Logger;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Ranky extends SpringBootServletInitializer {

  public static JDA bot = null;
  public static String prefix = "/";
  public static final Logger log = Logger.getLogger("Ranky.class");

  public static void main(String[] args) {
    ConfigurableApplicationContext context = SpringApplication
        .run(Ranky.class, args);
    setupDiscordBot(context);
  }

  private static void setupDiscordBot(ConfigurableApplicationContext context) {
    ConfigLoader config = context.getBean(ConfigLoader.class);
    bot = JDABuilder.createDefault(config.getDiscApiKey())
        .enableIntents(GatewayIntent.GUILD_MEMBERS).build();
    addBotListeners(context);
    if (bot == null) {
      SpringApplication.exit(context, () -> -1);
    }
    try {
      bot.awaitReady();
    } catch (InterruptedException e) {
      SpringApplication.exit(context, () -> -1);
    }
    addBotCommands();

    bot.getPresence().setActivity(
        Activity.playing("currently at " + bot.getGuilds().size() + " different servers.")
            .withState("Vibing"));
  }

  private static void addBotCommands() {
    bot.updateCommands().addCommands(Command.getDiscordCommands()).queue();
  }

  private static void addBotListeners(ConfigurableApplicationContext context) {
    bot.addEventListener(context.getBean(RankyGuildJoinListener.class));
    bot.addEventListener(context.getBean(RankyMessageListener.class));
    bot.addEventListener(context.getBean(RankySlashCommandListener.class));
    bot.addEventListener(context.getBean(RankyButtonClickListener.class));
  }

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder applicationBuilder) {
    return applicationBuilder.sources(Ranky.class);
  }
}
