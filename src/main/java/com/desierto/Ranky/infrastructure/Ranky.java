package com.desierto.Ranky.infrastructure;

import java.util.logging.Logger;
import net.dv8tion.jda.api.JDA;
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
    try {
      bot.awaitReady();
    } catch (InterruptedException e) {
      SpringApplication.exit(context, () -> -1);
    }
  }

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder applicationBuilder) {
    return applicationBuilder.sources(Ranky.class);
  }
}
