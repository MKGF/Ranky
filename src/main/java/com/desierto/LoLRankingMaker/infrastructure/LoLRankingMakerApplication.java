package com.desierto.LoLRankingMaker.infrastructure;

import com.desierto.LoLRankingMaker.domain.exception.BotCredentialsMissingException;
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
public class LoLRankingMakerApplication extends SpringBootServletInitializer {

  public static JDA bot = null;
  public static String prefix = "/";

  public static void main(String[] args) {
    ConfigurableApplicationContext context = SpringApplication
        .run(LoLRankingMakerApplication.class, args);
    try {
      bot = JDABuilder.createDefault(System.getenv("DISCORD_API_KEY"))
          .setActivity(Activity.of(ActivityType.WATCHING, "QUE MIRAS CERDO")).build();
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
    return applicationBuilder.sources(LoLRankingMakerApplication.class);
  }
}
