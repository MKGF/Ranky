package com.desierto.ranky.infrastructure.service;

import static com.desierto.ranky.domain.valueobject.RankedMode.RANKED_SOLO_5x5;
import static com.desierto.ranky.infrastructure.utils.DiscordButtons.FINAL_PAGE;
import static com.desierto.ranky.infrastructure.utils.DiscordButtons.PAGE;
import static com.desierto.ranky.infrastructure.utils.DiscordExceptionHandler.handleExceptionOnSlashCommandEvent;
import static com.desierto.ranky.infrastructure.utils.DiscordMessages.EXECUTE_COMMAND_FROM_SERVER;
import static com.desierto.ranky.infrastructure.utils.DiscordRankingToEmojiMapper.emojiFromTier;

import com.desierto.ranky.application.AccountsCache;
import com.desierto.ranky.domain.entity.Account;
import com.desierto.ranky.domain.entity.Ranking;
import com.desierto.ranky.domain.exception.ranking.RankingNotFoundException;
import com.desierto.ranky.domain.repository.RankingRepository;
import com.desierto.ranky.domain.repository.RiotAccountRepository;
import com.desierto.ranky.infrastructure.configuration.ConfigLoader;
import com.desierto.ranky.infrastructure.dto.EntryDto;
import com.desierto.ranky.infrastructure.exceptions.ConfigChannelNotFoundException;
import com.desierto.ranky.infrastructure.utils.DiscordOptionRetriever;
import com.desierto.ranky.infrastructure.utils.DiscordProgressBar;
import com.desierto.ranky.infrastructure.utils.DiscordRankingFormatter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class DiscordGetRankingService {

  @Autowired
  private ConfigLoader config;

  @Autowired
  private DiscordOptionRetriever discordOptionRetriever;

  @Autowired
  private RiotAccountRepository riotAccountRepository;

  @Autowired
  private DiscordRankingFormatter discordRankingFormatter;

  @Autowired
  private AccountsCache accountsCache;

  @Autowired
  private PrintRankingService printRankingService;

  @Autowired
  private RankingRepository rankingRepository;

  public void execute(SlashCommandInteractionEvent event) {
    if (event.isFromGuild()) {
      String rankingName = discordOptionRetriever.fromEventGetObjectName(event);
      try {
        Ranking ranking = rankingRepository.read(rankingName, event.getGuild());
        List<EntryDto> rankingEntries = getRankedAccountsWithProgressBarAnimation(event,
            rankingName, ranking);
        printRankingAsResponseToUserCommand(event, rankingName, rankingEntries);
      } catch (ConfigChannelNotFoundException | RankingNotFoundException e) {
        handleExceptionOnSlashCommandEvent(e, event);
      }

    } else {
      event.getHook().sendMessage(EXECUTE_COMMAND_FROM_SERVER.getMessage()).queue();
    }
  }

  private void printRankingAsResponseToUserCommand(SlashCommandInteractionEvent event,
      String rankingName,
      List<EntryDto> rankingEntries) {
    if (rankingEntries.size() <= config.getAccountLimit()) {
      printRankingService.printSinglePage(event, rankingName, rankingEntries,
          getSinglePagePrintingFunction());
    } else {
      printRankingService.printMultiPage(event, rankingName, rankingEntries,
          getMultiPagePrintingFunction(rankingName));
    }
  }

  private List<EntryDto> getRankedAccountsWithProgressBarAnimation(
      SlashCommandInteractionEvent event,
      String rankingName, Ranking ranking) {
    Optional<List<Account>> cachedAccounts = accountsCache.find(
        event.getGuild().getId(), rankingName);
    List<Account> rankingAccounts;
    Message progressBar = null;
    if (cachedAccounts.isEmpty()) {
      progressBar = event.getHook().sendMessage(DiscordProgressBar.getProgress(0)).complete();
      rankingAccounts = getRankingEntries(ranking, event.getHook(), progressBar);
    } else {
      rankingAccounts = cachedAccounts.get();
    }
    return toEntryDtos(rankingAccounts,
        Optional.ofNullable(progressBar));
  }

  private static SinglePagePrintingFunction getSinglePagePrintingFunction() {
    return (genericEvent, formattedRanking) -> {
      SlashCommandInteractionEvent specificEvent = (SlashCommandInteractionEvent) genericEvent;
      MessageCreateBuilder messageBuilder = new MessageCreateBuilder();
      messageBuilder.addContent(formattedRanking);
      Button button = Button.primary(FINAL_PAGE.getId(), "Make it public");
      messageBuilder.addActionRow(button);
      specificEvent.getHook().sendMessage(messageBuilder.build()).queue();
    };
  }

  private MultiPagePrintingFunction getMultiPagePrintingFunction(String rankingName) {
    return new MultiPagePrintingFunction() {
      @Override
      public void printBeginning(GenericEvent genericEvent, String formattedRanking) {
        SlashCommandInteractionEvent specificEvent = (SlashCommandInteractionEvent) genericEvent;
        MessageCreateBuilder messageBuilder = new MessageCreateBuilder();
        Button pageButton = Button.primary(PAGE.getId(), "Make page public");
        messageBuilder.addActionRow(pageButton);
        messageBuilder.addContent(formattedRanking);
        specificEvent.getHook().sendMessage(messageBuilder.build()).queue();
      }

      @Override
      public void printGeneric(GenericEvent genericEvent, String formattedRanking) {
        printBeginning(genericEvent, formattedRanking);
      }

      @Override
      public void printEnding(GenericEvent genericEvent, String formattedRanking) {
        SlashCommandInteractionEvent specificEvent = (SlashCommandInteractionEvent) genericEvent;
        MessageCreateBuilder messageBuilder = new MessageCreateBuilder();
        Button pageButton = Button.primary(FINAL_PAGE.getId(), "Make page public");
        messageBuilder.addActionRow(pageButton);
        String finalMessage = formattedRanking + discordRankingFormatter.footer();
        messageBuilder.addContent(finalMessage);
        Button rankingButton = Button.primary(rankingName,
            "Make whole ranking public");
        messageBuilder.addActionRow(rankingButton);
        specificEvent.getHook().sendMessage(messageBuilder.build()).queue();
      }
    };
  }

  private List<EntryDto> toEntryDtos(List<Account> rankingAccounts, Optional<Message> progressBar) {
    AtomicInteger index = new AtomicInteger(1);
    return rankingAccounts.stream()
        .sorted()
        .map(account -> {
              progressBar.ifPresent(message -> message.editMessage(
                      DiscordProgressBar.getProgress(
                          50 + (index.get() * 100 / rankingAccounts.size()) / 2))
                  .complete());
              return new EntryDto(
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
  }

  private List<Account> getRankingEntries(Ranking ranking, InteractionHook hook,
      Message progressBar) {
    AtomicInteger indexForEnrichment = new AtomicInteger(1);
    int numberOfAccounts = ranking.getAccounts().size();
    List<Account> accounts = ranking.getAccounts().stream().map(account -> {
      progressBar.editMessage(
              DiscordProgressBar.getProgress(
                  (indexForEnrichment.getAndIncrement() * 100 / numberOfAccounts) / 2))
          .complete();
      return riotAccountRepository.enrichWithRankedStats(account, RANKED_SOLO_5x5);
    }).toList();

    accountsCache.save(hook.getInteraction().getGuild().getId(), ranking.getId(), accounts);

    return accounts;
  }
}
