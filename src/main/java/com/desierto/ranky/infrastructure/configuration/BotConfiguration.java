package com.desierto.ranky.infrastructure.configuration;

import com.desierto.ranky.infrastructure.commands.Command;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "ranky.bot.enabled", havingValue = "true", matchIfMissing = true)
public class BotConfiguration {

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
}
