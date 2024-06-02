package com.desierto.Ranky.infrastructure.utils;

import com.desierto.Ranky.domain.entity.Account;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.springframework.stereotype.Component;

@Component
public class DiscordOptionRetriever {

  public String fromEventGetRankingName(SlashCommandInteractionEvent event) {
    return fromSlashCommandInteractionEvent(event).get(0);
  }

  public List<Account> fromEventGetAccountList(SlashCommandInteractionEvent event) {
    String rawAccounts = fromSlashCommandInteractionEvent(event).get(1);
    return Arrays.stream(rawAccounts.split(",")).map(s -> {
      String[] strings = s.split("#");
      if (strings.length == 1) {
        event.getHook().sendMessage("Account is missing summonerName#tagLine format: " + strings[0])
            .queue();
        return new Account();
      }
      return new Account(strings[0], strings[1]);
    }).collect(Collectors.toList());
  }

  private static List<String> fromSlashCommandInteractionEvent(SlashCommandInteractionEvent event) {
    return event.getOptions().stream().map(OptionMapping::getAsString).collect(
        Collectors.toList());
  }
}
