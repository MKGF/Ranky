package com.desierto.ranky.infrastructure.service;

import static com.desierto.ranky.infrastructure.utils.DiscordMessages.COMMAND_NOT_ALLOWED;
import static com.desierto.ranky.infrastructure.utils.DiscordMessages.EXECUTE_COMMAND_FROM_SERVER;

import com.desierto.ranky.domain.service.IRankingPublisherService;
import com.desierto.ranky.infrastructure.configuration.ConfigLoader;
import com.desierto.ranky.infrastructure.utils.DiscordOptionRetriever;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DiscordRankingPublisherService {

  private ConfigLoader config;

  private IRankingPublisherService rankingPublisherService;

  private DiscordOptionRetriever discordOptionRetriever;

  @Autowired
  public DiscordRankingPublisherService(IRankingPublisherService rankingPublisherService,
      ConfigLoader config, DiscordOptionRetriever discordOptionRetriever) {
    this.rankingPublisherService = rankingPublisherService;
    this.config = config;
    this.discordOptionRetriever = discordOptionRetriever;
  }

  public void execute(SlashCommandInteractionEvent event) {
    if (event.getMember().getRoles().stream()
        .anyMatch(role -> role.getName().equalsIgnoreCase(config.getRankyUserRole()))) {
      if (event.isFromGuild()) {
        String rankingName = discordOptionRetriever.fromEventGetObjectName(event);
        rankingPublisherService.publish(rankingName, event.getGuild());
        String url = String.format("%s#/servers/%s/ranking/%s",
            config.getRankyHomepage(),
            event.getGuild().getId(), rankingName.replace(" ", "%20"));
        event.getHook().sendMessage(
            String.format("Ranking published successfully! You can access it in %s", url)).queue();
      } else {
        event.getHook().sendMessage(EXECUTE_COMMAND_FROM_SERVER.getMessage()).queue();
      }
    } else {
      event.getHook().sendMessage(COMMAND_NOT_ALLOWED.getMessage()).queue();
    }
  }
}
