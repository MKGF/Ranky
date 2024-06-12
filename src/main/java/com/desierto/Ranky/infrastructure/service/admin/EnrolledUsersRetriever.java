package com.desierto.Ranky.infrastructure.service.admin;

import static com.desierto.Ranky.infrastructure.utils.DiscordMessages.COMMAND_NOT_ALLOWED;
import static com.desierto.Ranky.infrastructure.utils.DiscordMessages.GUILD_NOT_FOUND;
import static com.desierto.Ranky.infrastructure.utils.DiscordMessages.NOT_A_SERVER_COMMAND;

import com.desierto.Ranky.infrastructure.configuration.ConfigLoader;
import com.desierto.Ranky.infrastructure.utils.DiscordOptionRetriever;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EnrolledUsersRetriever {

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
          optionalGuild.get().findMembers(member -> member.getRoles().stream()
                  .anyMatch(role -> role.getName().equals(config.getRankyUserRole())))
              .onSuccess(members -> {
                    String message = members.stream().map(Member::toString)
                        .collect(Collectors.joining("\n"));
                    if (message.isEmpty()) {
                      event.getHook().sendMessage("No users with the power role found.").queue();
                    } else {
                      event.getHook().sendMessage(message).queue();
                    }
                  }
              );
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
