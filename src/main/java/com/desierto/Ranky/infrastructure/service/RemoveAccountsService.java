package com.desierto.Ranky.infrastructure.service;

import static com.desierto.Ranky.infrastructure.utils.DiscordExceptionHandler.handleExceptionOnSlashCommandEvent;

import com.desierto.Ranky.domain.entity.Account;
import com.desierto.Ranky.domain.entity.Ranking;
import com.desierto.Ranky.domain.exception.ConfigChannelNotFoundException;
import com.desierto.Ranky.domain.exception.ranking.RankingNotFoundException;
import com.desierto.Ranky.infrastructure.configuration.ConfigLoader;
import com.desierto.Ranky.infrastructure.repository.ConfigChannelRankingRepository;
import com.desierto.Ranky.infrastructure.utils.DiscordOptionRetriever;
import com.google.gson.Gson;
import java.util.List;
import java.util.logging.Logger;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RemoveAccountsService {

  public static final Logger log = Logger.getLogger("RemoveAccountsService.class");

  @Autowired
  private ConfigLoader config;

  @Autowired
  private DiscordOptionRetriever discordOptionRetriever;

  @Autowired
  private Gson gson;

  public void execute(SlashCommandInteractionEvent event) {
    InteractionHook hook = event.getHook();
    String rankingName = discordOptionRetriever.fromEventGetRankingName(event);
    try {
      ConfigChannelRankingRepository rankingRepository = new ConfigChannelRankingRepository(
          config,
          event.getGuild(),
          gson
      );
      Ranking ranking = rankingRepository.read(rankingName);
      List<Account> accountsToRemove = discordOptionRetriever.fromEventGetAccountList(event)
          .stream()
          .filter(Account::isNotEmpty)
          .toList();
      accountsToRemove.forEach(ranking::removeAccount);
      rankingRepository.update(ranking);
      if (!accountsToRemove.isEmpty()) {
        hook.sendMessage("Accounts removed successfully!").queue();
      }
    } catch (ConfigChannelNotFoundException | RankingNotFoundException e) {
      handleExceptionOnSlashCommandEvent(e, event);
    }
  }
}
