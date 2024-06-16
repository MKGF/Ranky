package com.desierto.Ranky.infrastructure.service;


import static com.desierto.Ranky.domain.utils.FileReader.read;

import com.desierto.Ranky.infrastructure.configuration.ConfigLoader;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class WelcomeOwnerService {

  @Autowired
  public ConfigLoader config;

  public void execute(Guild guild, Member owner, String welcomeEmbedMessage,
      String nonRiotEndorsementMessage) {
    if (owner != null) {
      String ownerMessage = String.format(read(
              config.getPathToOwnerPresentationMessage()),
          owner.getUser().getName(),
          guild.getName()
      );
      sendMessage(owner.getUser(), ownerMessage, welcomeEmbedMessage,
          nonRiotEndorsementMessage);
    }
  }

  private void sendMessage(User user, String content, String welcomeEmbedMessage,
      String nonRiotEndorsementMessage) {
    user.openPrivateChannel().queue(channel -> {
      channel.sendMessage(content).queue();
      EmbedBuilder message = new EmbedBuilder();
      message.setDescription(welcomeEmbedMessage);
      channel.sendMessageEmbeds(message.build()).queue();
      channel.sendMessage(nonRiotEndorsementMessage).queue();
    });
  }
}
