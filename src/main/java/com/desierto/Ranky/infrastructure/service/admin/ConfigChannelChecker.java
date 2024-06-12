package com.desierto.Ranky.infrastructure.service.admin;

import static com.desierto.Ranky.infrastructure.utils.DiscordMessages.COMMAND_NOT_ALLOWED;
import static com.desierto.Ranky.infrastructure.utils.DiscordMessages.GUILD_NOT_FOUND;
import static com.desierto.Ranky.infrastructure.utils.DiscordMessages.NOT_A_SERVER_COMMAND;

import com.desierto.Ranky.infrastructure.configuration.ConfigLoader;
import com.desierto.Ranky.infrastructure.utils.DiscordOptionRetriever;
import java.util.Optional;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ConfigChannelChecker {

  @Autowired
  private DiscordOptionRetriever discordOptionRetriever;

  @Autowired
  private JDA bot;

  @Autowired
  private ConfigLoader config;

  public void execute(SlashCommandInteractionEvent event) {
    if (event.isFromGuild()) {
      event.getHook().sendMessage(NOT_A_SERVER_COMMAND.getMessage()).queue();
    } else {
      if (isAdmin(event.getUser().getId())) {
        String guildName = discordOptionRetriever.fromEventGetObjectName(event);
        Optional<Guild> optionalGuild = bot.getGuilds().stream()
            .filter(guild -> guild.getName().equalsIgnoreCase(guildName)).findFirst();
        if (optionalGuild.isEmpty()) {
          event.getHook().sendMessage(GUILD_NOT_FOUND.getMessage()).queue();
        } else {
          boolean exists = optionalGuild.get().getTextChannels().stream()
              .anyMatch(textChannel -> textChannel.getName().equals(config.getConfigChannel()));
          event.getHook().sendMessage(Boolean.toString(exists)).queue();
        }
      } else {
        event.getHook().sendMessage(COMMAND_NOT_ALLOWED.getMessage()).queue();
      }
    }
  }

  private boolean isAdmin(String userId) {
    return config.getAdminIds().stream().anyMatch(id -> id.equals(userId));
  }
}
