package com.desierto.Ranky.infrastructure.service;


import static com.desierto.Ranky.domain.utils.FileReader.read;

import com.desierto.Ranky.infrastructure.configuration.ConfigLoader;
import java.util.Optional;
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

  public static final String PATH_TO_MESSAGE_TO_GUILD_TXT = "src/main/resources/config/onGuildJoinMessageToGuild.txt";
  @Autowired
  public ConfigLoader config;

  public void execute(Guild guild, String welcomeEmbedMessage,
      String nonRiotEndorsementMessage) {
    String welcomeMessage = String.format(read(
            PATH_TO_MESSAGE_TO_GUILD_TXT),
        guild.getName());

    TextChannel systemChannel;
    DefaultGuildChannelUnion defaultChannel;
    Optional<TextChannel> firstTextChannel = guild.getTextChannels().stream().findFirst();
    if ((systemChannel = guild.getSystemChannel()) != null) {
      sendWelcomeMessageToGuild(systemChannel, welcomeMessage,
          welcomeEmbedMessage, nonRiotEndorsementMessage);
    } else if ((defaultChannel = guild.getDefaultChannel()) != null) {
      sendWelcomeMessageToGuild(defaultChannel.asTextChannel(),
          welcomeMessage, welcomeEmbedMessage, nonRiotEndorsementMessage);
    } else {
      firstTextChannel.ifPresent(textChannel -> sendWelcomeMessageToGuild(textChannel,
          welcomeMessage, welcomeEmbedMessage, nonRiotEndorsementMessage));
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
