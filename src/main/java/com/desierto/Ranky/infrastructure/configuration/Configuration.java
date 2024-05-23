package com.desierto.Ranky.infrastructure.configuration;

import com.desierto.Ranky.application.service.RankyButtonClickListener;
import com.desierto.Ranky.application.service.RankyGuildJoinListener;
import com.desierto.Ranky.application.service.RankyMessageListener;
import com.desierto.Ranky.application.service.RankySlashCommandListener;
import com.desierto.Ranky.infrastructure.commands.Command;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
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
  public JDA jda(ConfigLoader config,
      RankyGuildJoinListener rankyGuildJoinListener,
      RankyMessageListener rankyMessageListener,
      RankySlashCommandListener rankySlashCommandListener,
      RankyButtonClickListener rankyButtonClickListener
  ) {
    JDA bot = JDABuilder.createDefault(config.getDiscApiKey())
        .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.DIRECT_MESSAGES,
            GatewayIntent.GUILD_MODERATION).build();
    bot.getPresence().setActivity(
        Activity.playing("currently at " + bot.getGuilds().size() + " different servers.")
            .withState("Vibing"));
    bot.addEventListener(rankyGuildJoinListener);
    bot.addEventListener(rankyMessageListener);
    bot.addEventListener(rankySlashCommandListener);
    bot.addEventListener(rankyButtonClickListener);
    bot.updateCommands().addCommands(Command.getDiscordCommands()).queue();
    return bot;
  }
}
