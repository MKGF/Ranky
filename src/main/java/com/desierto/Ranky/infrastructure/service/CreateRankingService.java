package com.desierto.Ranky.infrastructure.service;

import static com.desierto.Ranky.infrastructure.utils.DiscordExceptionHandler.handleExceptionOnSlashCommandEvent;
import static com.desierto.Ranky.infrastructure.utils.DiscordMessages.COMMAND_NOT_ALLOWED;
import static com.desierto.Ranky.infrastructure.utils.DiscordMessages.EXECUTE_COMMAND_FROM_SERVER;

import com.desierto.Ranky.domain.entity.Ranking;
import com.desierto.Ranky.domain.exception.ConfigChannelNotFoundException;
import com.desierto.Ranky.domain.exception.RankingAlreadyExistsException;
import com.desierto.Ranky.infrastructure.configuration.ConfigLoader;
import com.desierto.Ranky.infrastructure.repository.ConfigChannelRankingRepository;
import com.desierto.Ranky.infrastructure.utils.DiscordOptionRetriever;
import com.google.gson.Gson;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreateRankingService {

  private final ConfigLoader config;

  private final Gson gson;

  private final DiscordOptionRetriever discordOptionRetriever;

  @Autowired
  public CreateRankingService(ConfigLoader config, Gson gson,
      DiscordOptionRetriever discordOptionRetriever) {
    this.config = config;
    this.gson = gson;
    this.discordOptionRetriever = discordOptionRetriever;
  }

  public void execute(SlashCommandInteractionEvent event) {
    if (event.getMember().getRoles().stream()
        .anyMatch(role -> role.getName().equalsIgnoreCase(config.getRankyUserRole()))) {
      if (event.isFromGuild()) {
        try {
          ConfigChannelRankingRepository rankingRepository = new ConfigChannelRankingRepository(
              config,
              event.getGuild(),
              gson
          );
          String rankingName = discordOptionRetriever.fromEventGetObjectName(event);
          rankingRepository.create(new Ranking(rankingName));
          event.getHook().sendMessage("Ranking created successfully!").queue();
        } catch (ConfigChannelNotFoundException | RankingAlreadyExistsException e) {
          handleExceptionOnSlashCommandEvent(e, event);
        }
      } else {
        event.getHook().sendMessage(EXECUTE_COMMAND_FROM_SERVER.getMessage()).queue();
      }
    } else {
      event.getHook().sendMessage(COMMAND_NOT_ALLOWED.getMessage()).queue();
    }
  }
}
