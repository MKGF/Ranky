package com.desierto.Ranky.infrastructure;

import static net.dv8tion.jda.api.requests.GatewayIntent.DIRECT_MESSAGE_TYPING;
import static net.dv8tion.jda.api.requests.GatewayIntent.GUILD_MESSAGE_TYPING;
import static net.dv8tion.jda.api.requests.GatewayIntent.GUILD_PRESENCES;
import static net.dv8tion.jda.api.requests.GatewayIntent.GUILD_WEBHOOKS;

import com.desierto.Ranky.application.service.RankyGuildJoinListener;
import com.desierto.Ranky.application.service.RankyMessageListener;
import com.desierto.Ranky.domain.exception.BotCredentialsMissingException;
import com.desierto.Ranky.domain.repository.RiotAccountRepository;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
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
      List<GatewayIntent> intents = Arrays.asList(GatewayIntent.values());
      intents.removeAll(Arrays.asList(GUILD_PRESENCES, GUILD_WEBHOOKS, GUILD_MESSAGE_TYPING,
          DIRECT_MESSAGE_TYPING));
      bot = JDABuilder.create(System.getenv("DISCORD_API_KEY"), intents)
          .setActivity(Activity.of(ActivityType.PLAYING, "AL TETO")).build();
      bot.addEventListener(new RankyGuildJoinListener());
      bot.addEventListener(new RankyMessageListener(context.getBean(RiotAccountRepository.class)));

    } catch (LoginException e) {
      throw new BotCredentialsMissingException(e.getMessage());
    } finally {
      if (bot == null) {
        SpringApplication.exit(context, () -> -1);
      }
    }
  }

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder applicationBuilder) {
    return applicationBuilder.sources(Ranky.class);
  }
}
