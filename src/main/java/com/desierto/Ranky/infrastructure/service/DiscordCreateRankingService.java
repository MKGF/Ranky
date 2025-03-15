package com.desierto.Ranky.infrastructure.service;

import static com.desierto.Ranky.infrastructure.utils.DiscordMessages.COMMAND_NOT_ALLOWED;
import static com.desierto.Ranky.infrastructure.utils.DiscordMessages.EXECUTE_COMMAND_FROM_SERVER;

import com.desierto.Ranky.domain.service.ICreateRankingService;
import com.desierto.Ranky.infrastructure.configuration.ConfigLoader;
import com.desierto.Ranky.infrastructure.utils.DiscordOptionRetriever;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DiscordCreateRankingService {

  private final ConfigLoader config;

  private final DiscordOptionRetriever discordOptionRetriever;

  private final ICreateRankingService createRankingService;

  @Autowired
  public DiscordCreateRankingService(ConfigLoader config,
      DiscordOptionRetriever discordOptionRetriever, ICreateRankingService createRankingService) {
    this.config = config;
    this.discordOptionRetriever = discordOptionRetriever;
    this.createRankingService = createRankingService;
  }

  public void execute(SlashCommandInteractionEvent event) {
    if (event.getMember().getRoles().stream()
        .anyMatch(role -> role.getName().equalsIgnoreCase(config.getRankyUserRole()))) {
      if (event.isFromGuild()) {
        String rankingName = discordOptionRetriever.fromEventGetObjectName(event);
        createRankingService.execute(rankingName, event.getGuild());
        event.getHook().sendMessage("Ranking created successfully!").queue();
      } else {
        event.getHook().sendMessage(EXECUTE_COMMAND_FROM_SERVER.getMessage()).queue();
      }
    } else {
      event.getHook().sendMessage(COMMAND_NOT_ALLOWED.getMessage()).queue();
    }
  }
}
