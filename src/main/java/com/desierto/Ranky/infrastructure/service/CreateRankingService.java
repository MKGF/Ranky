package com.desierto.Ranky.infrastructure.service;

import static com.desierto.Ranky.infrastructure.utils.DiscordExceptionHandler.handleExceptionOnSlashCommandEvent;

import com.desierto.Ranky.domain.entity.Ranking;
import com.desierto.Ranky.domain.exception.ConfigChannelNotFoundException;
import com.desierto.Ranky.domain.exception.RankingAlreadyExistsException;
import com.desierto.Ranky.infrastructure.configuration.ConfigLoader;
import com.desierto.Ranky.infrastructure.repository.ConfigChannelRankingRepository;
import com.google.gson.Gson;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreateRankingService {

  private final ConfigLoader config;

  private final Gson gson;

  @Autowired
  public CreateRankingService(ConfigLoader config, Gson gson) {
    this.config = config;
    this.gson = gson;
  }

  public void execute(SlashCommandInteractionEvent event) {
    if (event.isFromGuild()) {
      try {
        ConfigChannelRankingRepository rankingRepository = new ConfigChannelRankingRepository(
            event.getGuild(),
            config,
            gson
        );
        String rankingName = event.getOptions().stream().findFirst().get().getAsString();
        rankingRepository.save(new Ranking(rankingName));
        event.getHook().sendMessage("Ranking created successfully!").queue();
      } catch (ConfigChannelNotFoundException | RankingAlreadyExistsException e) {
        handleExceptionOnSlashCommandEvent(e, event);
      }
    }
  }
}
