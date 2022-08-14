package com.desierto.Ranky.infrastructure;

import com.desierto.Ranky.application.service.RankyListener;
import com.desierto.Ranky.domain.exception.BotCredentialsMissingException;
import com.desierto.Ranky.domain.repository.RiotAccountRepository;
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

  public static void main(String[] args) {
    System.setProperty(RIOT_API_KEY, args[0]);
    ConfigurableApplicationContext context = SpringApplication
        .run(Ranky.class, args);
    try {
      bot = JDABuilder.createDefault(args[1])
          .setActivity(Activity.of(ActivityType.WATCHING, "QUE MIRAS CERDO")).build();
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
