package com.desierto.Ranky.infrastructure;

import com.desierto.Ranky.application.service.RankyListener;
import com.desierto.Ranky.domain.exception.BotCredentialsMissingException;
import com.desierto.Ranky.domain.repository.RiotAccountRepository;
import java.util.logging.Logger;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
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
          .setActivity(Activity.of(ActivityType.WATCHING, "YA FUNCIONO SOLO")).build();
      bot.addEventListener(new RankyListener(context.getBean(RiotAccountRepository.class)));
      
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
