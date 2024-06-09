package com.desierto.Ranky.infrastructure.service;

import static com.desierto.Ranky.infrastructure.utils.DiscordExceptionHandler.handleExceptionOnSlashCommandEvent;
import static com.desierto.Ranky.infrastructure.utils.DiscordMessages.EXECUTE_COMMAND_FROM_SERVER;
import static com.desierto.Ranky.infrastructure.utils.DiscordRankingToEmojiMapper.emojiFromTier;

import com.desierto.Ranky.domain.entity.Ranking;
import com.desierto.Ranky.domain.exception.ConfigChannelNotFoundException;
import com.desierto.Ranky.domain.exception.ranking.RankingNotFoundException;
import com.desierto.Ranky.domain.repository.RiotAccountRepository;
import com.desierto.Ranky.infrastructure.configuration.ConfigLoader;
import com.desierto.Ranky.infrastructure.dto.EntryDTO;
import com.desierto.Ranky.infrastructure.repository.ConfigChannelRankingRepository;
import com.desierto.Ranky.infrastructure.utils.DiscordOptionRetriever;
import com.desierto.Ranky.infrastructure.utils.DiscordProgressBar;
import com.desierto.Ranky.infrastructure.utils.DiscordRankingFormatter;
import com.google.gson.Gson;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
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

  @Autowired
  private DiscordRankingFormatter discordRankingFormatter;

  public void execute(SlashCommandInteractionEvent event) {
    if (event.isFromGuild()) {
      InteractionHook hook = event.getHook();
      String rankingName = discordOptionRetriever.fromEventGetRankingName(event);
      try {
        ConfigChannelRankingRepository rankingRepository = new ConfigChannelRankingRepository(
            config,
            event.getGuild(),
            gson
        );
        Ranking ranking = rankingRepository.read(rankingName);
        Message progressBar = hook.sendMessage(DiscordProgressBar.getProgress(0)).complete();
        AtomicInteger index = new AtomicInteger(1);
        AtomicInteger indexForEnrichment = new AtomicInteger(1);
        int numberOfAccounts = ranking.getAccounts().size();
        List<EntryDTO> rankingEntries = ranking.getAccounts().stream().map(account -> {
              progressBar.editMessage(
                      DiscordProgressBar.getProgress(
                          (indexForEnrichment.getAndIncrement() * 100 / numberOfAccounts) / 2))
                  .complete();
              return riotAccountRepository.enrichWithSoloQStats(account);
            })
            .sorted()
            .map(account -> {
                  progressBar.editMessage(
                          DiscordProgressBar.getProgress((index.get() * 100 / numberOfAccounts) / 2 + 50))
                      .complete();
                  return new EntryDTO(
                      index.getAndIncrement(),
                      account.getName(),
                      emojiFromTier(account.getRank().getTier()),
                      account.getRank().getDivision().toString(),
                      account.getRank().getLeaguePoints(),
                      account.getRank().getWinrate().getWins().toString(),
                      account.getRank().getWinrate().getLosses().toString(),
                      account.getRank().getWinrate().getPercentage().toString()
                  );
                }
            )
            .toList();
        String formattedRanking = discordRankingFormatter.formatRankingEntries(rankingEntries,
            rankingName);
        MessageCreateBuilder messageBuilder = new MessageCreateBuilder();
        messageBuilder.addContent(formattedRanking);
        Button button = Button.primary("public", "Make it public");
        messageBuilder.addActionRow(button);
        hook.sendMessage(messageBuilder.build()).queue();

      } catch (ConfigChannelNotFoundException | RankingNotFoundException e) {
        handleExceptionOnSlashCommandEvent(e, event);
      }

    } else {
      event.getHook().sendMessage(EXECUTE_COMMAND_FROM_SERVER.getMessage()).queue();
    }
  }
}
