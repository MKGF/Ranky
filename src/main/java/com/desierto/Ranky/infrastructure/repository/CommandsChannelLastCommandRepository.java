package com.desierto.Ranky.infrastructure.repository;

import com.desierto.Ranky.domain.exception.CommandsChannelNotFoundException;
import com.desierto.Ranky.domain.exception.LastCommandNotFoundException;
import com.desierto.Ranky.infrastructure.configuration.ConfigLoader;
import com.desierto.Ranky.infrastructure.dto.LastCommandDTO;
import com.google.gson.Gson;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class CommandsChannelLastCommandRepository {

  private final TextChannel configChannel;

  private final ConfigLoader config;

  private final Gson gson;

  public CommandsChannelLastCommandRepository(
      ConfigLoader config,
      Guild guild,
      Gson gson
  ) {
    this.config = config;
    this.configChannel = getCommandsChannel(guild);
    this.gson = gson;
  }

  private TextChannel getCommandsChannel(Guild guild) {
    return guild.getTextChannels().stream()
        .filter(textChannel -> textChannel.getName().equalsIgnoreCase(config.getCommandsChannel()))
        .findFirst().orElseThrow(
            CommandsChannelNotFoundException::new);
  }

  public LastCommandDTO find(String userId) {
    return fromMessage(retrieveLastCommandOfUser(userId));
  }

  private boolean commandWithIdExists(String userId) {
    return configChannel.getHistory().retrievePast(config.getRankingLimit()).complete().stream()
        .anyMatch(message -> {
          LastCommandDTO lastCommandDTO = fromMessage(message);
          return lastCommandDTO.getUserId().equalsIgnoreCase(userId);
        });
  }

  private Message retrieveLastCommandOfUser(String userId) {
    return configChannel.getHistory().retrievePast(config.getRankingLimit()).complete().stream()
        .filter(message -> {
          LastCommandDTO lastCommandDTO = fromMessage(message);
          return lastCommandDTO.getUserId().equalsIgnoreCase(userId);
        }).findFirst().orElseThrow(LastCommandNotFoundException::new);
  }

  private LastCommandDTO fromMessage(Message message) {
    return gson.fromJson(message.getContentRaw(), LastCommandDTO.class);
  }
}
