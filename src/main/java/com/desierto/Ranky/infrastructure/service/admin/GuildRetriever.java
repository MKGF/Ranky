package com.desierto.Ranky.infrastructure.service.admin;

import com.desierto.Ranky.infrastructure.configuration.ConfigLoader;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GuildRetriever {

  @Autowired
  private ConfigLoader config;

  @Autowired
  private JDA bot;

  public void execute(SlashCommandInteractionEvent event) {
    if (event.isFromGuild()) {
      event.getHook().sendMessage("This command can't be used from a server.").queue();
    } else {
      if (!isAdmin(event.getInteraction().getUser().getId())) {
        event.getHook().sendMessage("You are not allowed to execute this command.").queue();
      } else {
        //Meter paginacion por parametros en un futuro con sublist(begin*page, end*page)
        String message = bot.getGuilds().stream()
            .map(guild -> guild.getName() + ":" + guild.getId())
            .collect(Collectors.joining("\n"));
        event.getHook().sendMessage(message).queue();
      }
    }
  }

  private boolean isAdmin(String userId) {
    return config.getAdminIds().stream().anyMatch(id -> id.equals(userId));
  }


}
