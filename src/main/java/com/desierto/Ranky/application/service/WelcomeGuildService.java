package com.desierto.Ranky.application.service;


import static com.desierto.Ranky.domain.utils.FileReader.read;

import com.desierto.Ranky.infrastructure.configuration.ConfigLoader;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.DefaultGuildChannelUnion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class WelcomeGuildService {

  @Autowired
  public ConfigLoader config;

  public void execute(Guild guild, String welcomeEmbedMessage,
      String nonRiotEndorsementMessage) {
    String welcomeMessage = String.format(read(
            "src/main/java/com/desierto/Ranky/infrastructure/commands/onGuildJoinMessageToGuild.txt"),
        guild.getName());

    DefaultGuildChannelUnion textChannel = guild.getDefaultChannel();
    if (textChannel == null) {
      if (guild.getSystemChannel() != null) {
        sendWelcomeMessageToGuild(guild.getSystemChannel(), welcomeMessage,
            welcomeEmbedMessage, nonRiotEndorsementMessage);
      } else {
        sendWelcomeMessageToGuild(guild.getDefaultChannel().asTextChannel(),
            welcomeMessage, welcomeEmbedMessage, nonRiotEndorsementMessage);
      }
    } else {
      sendWelcomeMessageToGuild(guild.getTextChannels().get(0),
          welcomeMessage, welcomeEmbedMessage, nonRiotEndorsementMessage);
    }
  }

  private void sendWelcomeMessageToGuild(TextChannel channel, String welcomeMessage,
      String welcomeEmbedMessage, String nonRiotEndorsementMessage) {
    channel.sendMessage(welcomeMessage).complete();
    EmbedBuilder message = new EmbedBuilder();
    message.setDescription(welcomeEmbedMessage);
    channel.sendMessageEmbeds(message.build()).complete();
    channel.sendMessage(nonRiotEndorsementMessage).complete();
  }
}
