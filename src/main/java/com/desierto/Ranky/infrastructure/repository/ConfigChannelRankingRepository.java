package com.desierto.Ranky.infrastructure.repository;

import com.desierto.Ranky.domain.entity.Ranking;
import com.desierto.Ranky.domain.exception.ConfigChannelNotFoundException;
import com.desierto.Ranky.domain.exception.RankingAlreadyExistsException;
import com.desierto.Ranky.domain.repository.RankingRepository;
import com.desierto.Ranky.infrastructure.configuration.ConfigLoader;
import com.google.gson.Gson;
import java.util.Optional;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class ConfigChannelRankingRepository implements RankingRepository {

  private final TextChannel configChannel;

  private final ConfigLoader config;

  private final Gson gson;

  public ConfigChannelRankingRepository(
      Guild guild,
      ConfigLoader config,
      Gson gson
  ) {
    this.config = config;
    this.configChannel = getConfigChannel(guild);
    this.gson = gson;
  }

  @Override
  public Ranking save(Ranking ranking) throws RankingAlreadyExistsException {
    if (rankingExists(ranking)) {
      throw new RankingAlreadyExistsException();
    }
    configChannel.sendMessage(gson.toJson(ranking)).queue();
    return ranking;
  }

  private TextChannel getConfigChannel(Guild guild) {
    return guild.getTextChannels().stream()
        .filter(textChannel -> textChannel.getName().equalsIgnoreCase(config.getConfigChannel()))
        .findFirst().orElseThrow(
            ConfigChannelNotFoundException::new);
  }

  private boolean rankingExists(Ranking ranking) {
    return configChannel.getHistory().retrievePast(config.getRankingLimit()).complete().stream()
        .anyMatch(message -> {
          Optional<Ranking> optionalRanking = fromMessageIfPossible(message);
          return optionalRanking.map(r -> r.getId()
              .equalsIgnoreCase(ranking.getId())).orElse(false);
        });
  }

  private Optional<Ranking> fromMessageIfPossible(Message message) {
    try {
      return Optional.of(gson.fromJson(message.getContentRaw(), Ranking.class));
    } catch (Exception e) {
      return Optional.empty();
    }
  }
}
