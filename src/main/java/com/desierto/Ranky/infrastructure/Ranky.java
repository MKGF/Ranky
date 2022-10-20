package com.desierto.Ranky.infrastructure;

import com.desierto.Ranky.application.service.RankyGuildJoinListener;
import com.desierto.Ranky.application.service.RankyMessageListener;
import com.desierto.Ranky.domain.exception.BotCredentialsMissingException;
import com.desierto.Ranky.domain.repository.RiotAccountRepository;
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
  public static final String RIOT_API_KEY = "api.key";
  public static final String RIOT_BASE_URL = "riot.base.url";
  public static final Logger log = Logger.getLogger("Ranky.class");

  public static void main(String[] args) {
    System.setProperty(RIOT_API_KEY, System.getenv("RIOT_API_KEY"));
    System.setProperty(RIOT_BASE_URL, "https://euw1.api.riotgames.com");
    ConfigurableApplicationContext context = SpringApplication
        .run(Ranky.class, args);
    try {
      bot = JDABuilder.createDefault(System.getenv("DISCORD_API_KEY"))
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
    commands.add(Commands.slash("helpRanky", "Gives an explanation on how to use the bot."));
    commands.add(Commands.slash("create", "Creates a ranking. Usage: /create \"Test ranking\""));
    commands.add(Commands.slash("addAccount",
        "Adds an account to a ranking. Usage: /addAccount \"Test ranking\" account"));
    commands.add(Commands.slash("addMultiple",
        "Adds several accounts to a ranking. Usage: /addMultiple \"Test ranking\" account1, account2"));
    commands.add(Commands.slash("removeAccount",
        "Removes an account from the ranking. Usage: /removeAccount \"Test ranking\" account"));
    commands.add(Commands.slash("addStream",
        "Adds a stream to an account from the ranking. Usage: /addStream \"Test ranking\" \"account\" link-to-stream"));
    commands.add(Commands.slash("ranking",
        "Gets the information of the ranking. Usage: /ranking \"Test ranking\""));

    return commands;
  }
}
