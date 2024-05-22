package com.desierto.Ranky.infrastructure;

import com.desierto.Ranky.application.service.RankyGuildJoinListener;
import com.desierto.Ranky.application.service.RankyMessageListener;
import com.desierto.Ranky.domain.exception.BotCredentialsMissingException;
import com.desierto.Ranky.domain.repository.RiotAccountRepository;
import com.desierto.Ranky.infrastructure.configuration.ConfigLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
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

    ConfigLoader config = context.getBean(ConfigLoader.class);
    try {
      bot = JDABuilder.createDefault(config.getDiscordApiKey())
          .enableIntents(GatewayIntent.GUILD_MEMBERS).build();
      bot.addEventListener(new RankyGuildJoinListener());
      bot.addEventListener(new RankyMessageListener(context.getBean(RiotAccountRepository.class)));
    } catch (LoginException e) {
      throw new BotCredentialsMissingException(e.getMessage());
    } finally {
      if (bot == null) {
        SpringApplication.exit(context, () -> -1);
      }
      try {
        bot.awaitReady();
      } catch (InterruptedException e) {
        SpringApplication.exit(context, () -> -1);
      }
      bot.updateCommands().addCommands(getCommands()).queue();

      bot.getPresence().setActivity(
          Activity.playing("currently at " + bot.getGuilds().size() + " different servers."));
    }
  }

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder applicationBuilder) {
    return applicationBuilder.sources(Ranky.class);
  }

  private static List<CommandData> getCommands() {
    List<CommandData> commands = new ArrayList<>();
    commands.add(Commands.slash("test", "Testing slash commands. Does nothing."));

    return commands;
  }
}
