package com.desierto.Ranky.infrastructure.repository;

import com.desierto.Ranky.domain.entity.Ranking;
import com.desierto.Ranky.domain.exception.ConfigChannelNotFoundException;
import com.desierto.Ranky.domain.exception.RankingAlreadyExistsException;
import com.desierto.Ranky.domain.exception.ranking.RankingNotFoundException;
import com.desierto.Ranky.domain.repository.RankingRepository;
import com.desierto.Ranky.infrastructure.configuration.ConfigLoader;
import com.desierto.Ranky.infrastructure.dto.RankingDTO;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
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
    configChannel.sendMessage(gson.toJson(RankingDTO.fromDomain(ranking))).complete();
    return ranking;
  }

  @Override
  public Ranking update(Ranking ranking) throws RankingNotFoundException {
    List<Message> rankingMessages = retrieveMessagesOfRanking(ranking.getId());
    if (rankingMessages.isEmpty()) {
      throw new RankingNotFoundException(ranking.getId());
    } else {
      if (ranking.getAccounts().isEmpty()) {
        configChannel.sendMessage(gson.toJson(RankingDTO.fromDomain(ranking))).complete();
      } else {
        int numberOfAccounts = ranking.getAccounts().size();
        int numberOfFractions = numberOfAccounts / config.getAccountLimit() + 1;
        List<Ranking> fractions = new ArrayList<>();
        for (int i = 0; i < numberOfFractions; i++) {
          int beginning = config.getAccountLimit() * i;
          int possibleEnd = (config.getAccountLimit() * (i + 1));
          int end = Math.min(possibleEnd, numberOfAccounts);
          fractions.add(
              new Ranking(ranking.getId(), ranking.getAccounts().subList(beginning, end)));
        }
        fractions.forEach(
            fraction -> configChannel.sendMessage(gson.toJson(RankingDTO.fromDomain(fraction)))
                .complete());
      }
      rankingMessages.forEach(message -> message.delete().complete());
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
    List<Message> messagesOfRanking = new ArrayList<>(retrieveMessagesOfRanking(rankingId));
    if (messagesOfRanking.isEmpty()) {
      throw new RankingNotFoundException(rankingId);
    }
    return fromMessages(messagesOfRanking).toDomain();
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
          RankingDTO rankingDTO = fromMessages(new ArrayList<>(List.of(message)));
          return rankingDTO.getId().equalsIgnoreCase(rankingId);
        });
  }

  private boolean removeMessageOfRanking(String rankingId) {
    AtomicReference<Boolean> removedSuccessfully = new AtomicReference<>(false);
    configChannel.getHistory().retrievePast(config.getRankingLimit()).complete().stream()
        .filter(message -> {
          RankingDTO rankingDTO = fromMessages(new ArrayList<>(List.of(message)));
          return rankingDTO.getId().equalsIgnoreCase(rankingId);
        })
        .forEach(message -> {
          message.delete().complete();
          removedSuccessfully.set(true);
        });
    return removedSuccessfully.get();
  }

  private List<Message> retrieveMessagesOfRanking(String rankingId) {
    return configChannel.getHistory().retrievePast(config.getRankingLimit()).complete().stream()
        .filter(message -> {
          RankingDTO rankingDTO = fromMessages(new ArrayList<>(List.of(message)));
          return rankingDTO.getId().equalsIgnoreCase(rankingId);
        }).toList();
  }

  private RankingDTO fromMessages(List<Message> messages) {
    RankingDTO grouped = gson.fromJson(messages.get(0).getContentRaw(), RankingDTO.class);
    messages.remove(0);
    messages.forEach(message -> grouped.getAccounts()
        .addAll(gson.fromJson(message.getContentRaw(), RankingDTO.class).getAccounts()));
    return grouped;
  }
}
