package com.desierto.Ranky.infrastructure.repository;

import com.desierto.Ranky.domain.entity.Ranking;
import com.desierto.Ranky.domain.exception.ConfigChannelNotFoundException;
import com.desierto.Ranky.domain.exception.RankingAlreadyExistsException;
import com.desierto.Ranky.domain.exception.ranking.RankingNotFoundException;
import com.desierto.Ranky.domain.repository.RankingRepository;
import com.desierto.Ranky.infrastructure.configuration.ConfigLoader;
import com.google.gson.Gson;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class ConfigChannelRankingRepository implements RankingRepository {

  private final TextChannel configChannel;

  private final ConfigLoader config;

  private final Gson gson;

  public ConfigChannelRankingRepository(
      ConfigLoader config,
      Guild guild,
      Gson gson
  ) {
    this.config = config;
    this.configChannel = getConfigChannel(guild);
    this.gson = gson;
  }

  @Override
  public Ranking create(Ranking ranking) throws RankingAlreadyExistsException {
    if (rankingWithIdExists(ranking.getId())) {
      throw new RankingAlreadyExistsException();
    }
    configChannel.sendMessage(gson.toJson(ranking)).complete();
    return ranking;
  }

  @Override
  public Ranking update(Ranking ranking) throws RankingNotFoundException {
    Optional<Message> rankingMessage = retrieveMessageOfRanking(ranking.getId());
    if (rankingMessage.isEmpty()) {
      throw new RankingNotFoundException(ranking.getId());
    } else {
      rankingMessage.get().editMessage(gson.toJson(ranking)).complete();
      return ranking;
    }
  }

  @Override
  public boolean delete(String rankingId) {
    if (!rankingWithIdExists(rankingId)) {
      throw new RankingNotFoundException(rankingId);
    }
    return removeMessageOfRanking(rankingId);
  }

  @Override
  public Ranking read(String rankingId) {
    Optional<Message> messageOfRanking = retrieveMessageOfRanking(rankingId);
    if (messageOfRanking.isEmpty()) {
      throw new RankingNotFoundException(rankingId);
    }
    return fromMessage(messageOfRanking.get());
  }

  private TextChannel getConfigChannel(Guild guild) {
    return guild.getTextChannels().stream()
        .filter(textChannel -> textChannel.getName().equalsIgnoreCase(config.getConfigChannel()))
        .findFirst().orElseThrow(
            ConfigChannelNotFoundException::new);
  }

  private boolean rankingWithIdExists(String rankingId) {
    return configChannel.getHistory().retrievePast(config.getRankingLimit()).complete().stream()
        .anyMatch(message -> {
          Ranking ranking = fromMessage(message);
          return ranking.getId().equalsIgnoreCase(rankingId);
        });
  }

  private boolean removeMessageOfRanking(String rankingId) {
    AtomicReference<Boolean> removedSuccessfully = new AtomicReference<>(false);
    configChannel.getHistory().retrievePast(config.getRankingLimit()).complete().stream()
        .filter(message -> {
          Ranking ranking = fromMessage(message);
          return ranking.getId().equalsIgnoreCase(rankingId);
        })
        .forEach(message -> {
          message.delete().complete();
          removedSuccessfully.set(true);
        });
    return removedSuccessfully.get();
  }

  private Optional<Message> retrieveMessageOfRanking(String rankingId) {
    return configChannel.getHistory().retrievePast(config.getRankingLimit()).complete().stream()
        .filter(message -> {
          Ranking ranking = fromMessage(message);
          return ranking.getId().equalsIgnoreCase(rankingId);
        }).findFirst();
  }

  private Ranking fromMessage(Message message) {
    return gson.fromJson(message.getContentRaw(), Ranking.class);
  }
}
