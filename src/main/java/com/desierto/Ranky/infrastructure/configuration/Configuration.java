package com.desierto.Ranky.infrastructure.configuration;

import com.desierto.Ranky.infrastructure.commands.Command;
import com.google.gson.Gson;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootConfiguration
@ComponentScan(basePackages = "com.desierto.Ranky.infrastructure")
@ComponentScan(basePackages = "com.desierto.Ranky.application")
@EntityScan(basePackages = "com.desierto.Ranky.domain.entity")
public class Configuration implements WebMvcConfigurer {

  @Bean
  public JDA jda(ConfigLoader config) {
    JDA bot = JDABuilder.createDefault(config.getDiscApiKey())
        .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.DIRECT_MESSAGES,
            GatewayIntent.GUILD_MODERATION, GatewayIntent.MESSAGE_CONTENT)
        .setMemberCachePolicy(MemberCachePolicy.ALL)
        .build();

    bot.updateCommands().addCommands(Command.getDiscordCommands()).queue();
    return bot;
  }

  @Bean
  public Gson gson() {
    return new Gson();
  }

  @Bean
  public ExecutorService executorService() {
    return Executors.newFixedThreadPool(20);
  }
}
