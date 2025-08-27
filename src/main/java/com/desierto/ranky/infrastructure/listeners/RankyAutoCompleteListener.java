package com.desierto.ranky.infrastructure.listeners;

import com.desierto.ranky.infrastructure.repository.DiscordRankingRepository;
import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command.Choice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class RankyAutoCompleteListener extends ListenerAdapter {

  @Autowired
  private JDA bot;

  @Autowired
  private DiscordRankingRepository rankingRepository;

  @PostConstruct
  private void postConstruct() {
    bot.addEventListener(this);
    log.info(String.format("Added %s to the bot!", this.getClass().getName()));
  }

  @Override
  public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
    List<Choice> options = rankingRepository.findAll(event.getGuild()).stream()
        .filter(ranking -> ranking.getId().startsWith(event.getFocusedOption()
            .getValue()))
        .map(ranking -> new Choice(ranking.getId(),
            ranking.getId()))
        .toList();
    event.replyChoices(options).queue();
  }
}
