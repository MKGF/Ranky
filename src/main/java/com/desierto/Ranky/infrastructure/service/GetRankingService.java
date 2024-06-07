package com.desierto.Ranky.infrastructure.service;

import static com.desierto.Ranky.infrastructure.utils.DiscordExceptionHandler.handleExceptionOnSlashCommandEvent;

import com.desierto.Ranky.domain.entity.Ranking;
import com.desierto.Ranky.domain.exception.ConfigChannelNotFoundException;
import com.desierto.Ranky.domain.exception.ranking.RankingNotFoundException;
import com.desierto.Ranky.domain.repository.RiotAccountRepository;
import com.desierto.Ranky.infrastructure.configuration.ConfigLoader;
import com.desierto.Ranky.infrastructure.repository.ConfigChannelRankingRepository;
import com.desierto.Ranky.infrastructure.utils.DiscordOptionRetriever;
import com.google.gson.Gson;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GetRankingService {

  public static final Logger log = Logger.getLogger("RemoveAccountsService.class");

  @Autowired
  private ConfigLoader config;

  @Autowired
  private DiscordOptionRetriever discordOptionRetriever;

  @Autowired
  private Gson gson;

  @Autowired
  private RiotAccountRepository riotAccountRepository;

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
      AtomicInteger index = new AtomicInteger(1);
      String formattedRanking = riotAccountRepository.enrichWithSoloQStats(ranking.getAccounts())
          .stream()
          .sorted()
          .map(account -> account.getFormattedForRanking(index.getAndIncrement())
          )
          .reduce("", (formatted, accountAsString) -> formatted + "\n" + accountAsString);
      hook.sendMessage(formattedRanking).queue();
    } catch (ConfigChannelNotFoundException | RankingNotFoundException e) {
      handleExceptionOnSlashCommandEvent(e, event);
    }

  }
}
