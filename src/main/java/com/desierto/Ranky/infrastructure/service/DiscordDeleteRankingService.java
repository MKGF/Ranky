package com.desierto.Ranky.infrastructure.service;

import static com.desierto.Ranky.infrastructure.utils.DiscordExceptionHandler.handleExceptionOnSlashCommandEvent;
import static com.desierto.Ranky.infrastructure.utils.DiscordMessages.COMMAND_NOT_ALLOWED;
import static com.desierto.Ranky.infrastructure.utils.DiscordMessages.EXECUTE_COMMAND_FROM_SERVER;

import com.desierto.Ranky.domain.exception.ranking.RankingCouldNotBeDeletedException;
import com.desierto.Ranky.domain.service.IDeleteRankingService;
import com.desierto.Ranky.infrastructure.configuration.ConfigLoader;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DiscordDeleteRankingService {

  private final ConfigLoader config;

  private final IDeleteRankingService deleteRankingService;

  @Autowired
  public DiscordDeleteRankingService(ConfigLoader config,
      IDeleteRankingService deleteRankingService) {
    this.config = config;
    this.deleteRankingService = deleteRankingService;
  }

  public void execute(SlashCommandInteractionEvent event) {
    if (event.getMember().getRoles().stream()
        .anyMatch(role -> role.getName().equalsIgnoreCase(config.getRankyUserRole()))) {
      if (event.isFromGuild()) {

        String rankingId = event.getOptions().stream().findFirst().get().getAsString();
        if (deleteRankingService.execute(rankingId, event.getGuild())) {
          event.getHook().sendMessage("Ranking deleted successfully!").queue();
        } else {
          handleExceptionOnSlashCommandEvent(new RankingCouldNotBeDeletedException(), event);
        }
      } else {
        event.getHook().sendMessage(EXECUTE_COMMAND_FROM_SERVER.getMessage()).queue();
      }
    } else {
      event.getHook().sendMessage(COMMAND_NOT_ALLOWED.getMessage()).queue();
    }

  }

}
