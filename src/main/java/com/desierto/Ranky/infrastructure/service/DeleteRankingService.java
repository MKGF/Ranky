package com.desierto.Ranky.infrastructure.service;

import static com.desierto.Ranky.infrastructure.utils.DiscordExceptionHandler.handleExceptionOnSlashCommandEvent;
import static com.desierto.Ranky.infrastructure.utils.DiscordMessages.COMMAND_NOT_ALLOWED;
import static com.desierto.Ranky.infrastructure.utils.DiscordMessages.EXECUTE_COMMAND_FROM_SERVER;

import com.desierto.Ranky.domain.exception.ConfigChannelNotFoundException;
import com.desierto.Ranky.domain.exception.ranking.RankingCouldNotBeDeletedException;
import com.desierto.Ranky.domain.exception.ranking.RankingNotFoundException;
import com.desierto.Ranky.infrastructure.configuration.ConfigLoader;
import com.desierto.Ranky.infrastructure.repository.ConfigChannelRankingRepository;
import com.google.gson.Gson;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeleteRankingService {

  private final ConfigLoader config;

  private final Gson gson;

  @Autowired
  public DeleteRankingService(ConfigLoader config, Gson gson) {
    this.config = config;
    this.gson = gson;
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
          String rankingName = event.getOptions().stream().findFirst().get().getAsString();
          if (rankingRepository.delete(rankingName)) {
            event.getHook().sendMessage("Ranking deleted successfully!").queue();
          } else {
            handleExceptionOnSlashCommandEvent(new RankingCouldNotBeDeletedException(), event);
          }
        } catch (ConfigChannelNotFoundException | RankingNotFoundException e) {
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
