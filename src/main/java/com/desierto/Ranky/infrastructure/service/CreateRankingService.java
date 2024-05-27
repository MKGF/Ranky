package com.desierto.Ranky.infrastructure.service;

import com.desierto.Ranky.domain.exception.ConfigChannelNotFoundException;
import com.desierto.Ranky.domain.exception.RankingAlreadyExistsException;
import com.desierto.Ranky.domain.valueobject.RankingConfiguration;
import com.desierto.Ranky.infrastructure.configuration.ConfigLoader;
import com.google.gson.Gson;
import java.util.Optional;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CreateRankingService {

  @Autowired
  private ConfigLoader config;

  @Autowired
  private Gson gson;

  public void execute(SlashCommandInteractionEvent event) {
    TextChannel channel = null;
    try {
      channel = getConfigChannel(event.getGuild());
    } catch (ConfigChannelNotFoundException e) {
      rethrowExceptionAfterNoticingTheServer(event, e);
    }
    String rankingName = event.getOptions().stream().findFirst().get().getAsString();
    try {
      if (channel != null && rankingExists(channel, rankingName)) {
        throw new RankingAlreadyExistsException();
      }
    } catch (RankingAlreadyExistsException e) {
      rethrowExceptionAfterNoticingTheServer(event, e);
    }
    if (channel != null) {
      String json = gson
          .toJson(new RankingConfiguration(rankingName));
      channel.sendMessage(json).queue();
      event.getHook().sendMessage("Ranking created successfully!").queue();
    }

  }

  private TextChannel getConfigChannel(Guild guild) {
    return guild.getTextChannels().stream()
        .filter(textChannel -> textChannel.getName().equalsIgnoreCase(config.getConfigChannel()))
        .findFirst().orElseThrow(
            ConfigChannelNotFoundException::new);
  }

  private void rethrowExceptionAfterNoticingTheServer(SlashCommandInteractionEvent event,
      RuntimeException e) throws ConfigChannelNotFoundException, RankingAlreadyExistsException {
    event.getHook().sendMessage(e.getMessage()).queue();
    throw e;
  }

  private boolean rankingExists(TextChannel channel, String rankingName) {
    return channel.getHistory().retrievePast(config.getRankingLimit()).complete().stream()
        .anyMatch(message -> {
          Optional<RankingConfiguration> optionalRanking = RankingConfiguration
              .fromMessageIfPossible(message);
          return optionalRanking.map(rankingConfiguration -> rankingConfiguration.getName()
              .equalsIgnoreCase(rankingName)).orElse(false);
        });
  }

}
