package com.desierto.ranky.infrastructure.service;

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
import com.desierto.ranky.domain.valueobject.RankedMode;
import com.desierto.ranky.infrastructure.configuration.ConfigLoader;
import com.desierto.ranky.infrastructure.dto.EntryDto;
import com.desierto.ranky.infrastructure.exceptions.ConfigChannelNotFoundException;
import com.desierto.ranky.infrastructure.utils.DiscordOptionRetriever;
import com.desierto.ranky.infrastructure.utils.DiscordRankingFormatter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class DiscordGetRankingService {

  private final ConfigLoader config;

  private final DiscordOptionRetriever discordOptionRetriever;

  private final RiotAccountRepository riotAccountRepository;

  private final DiscordRankingFormatter discordRankingFormatter;

  private final AccountsCache accountsCache;

  private final PrintRankingService printRankingService;

  private final RankingRepository rankingRepository;

  public void execute(SlashCommandInteractionEvent event, boolean forceRefresh,
      RankedMode rankedMode) {
    if (event.isFromGuild()) {
      String rankingName = discordOptionRetriever.fromEventGetObjectName(event);
      try {
        Ranking ranking = rankingRepository.read(rankingName, event.getGuild());
        List<EntryDto> rankingEntries = getRankedAccounts(event,
            rankingName, ranking, forceRefresh, rankedMode);
        printRankingAsResponseToUserCommand(event, rankingName, rankingEntries, rankedMode);
      } catch (ConfigChannelNotFoundException | RankingNotFoundException e) {
        handleExceptionOnSlashCommandEvent(e, event);
      }

    } else {
      event.getHook().sendMessage(EXECUTE_COMMAND_FROM_SERVER.getMessage()).queue();
    }
  }

  private void printRankingAsResponseToUserCommand(SlashCommandInteractionEvent event,
      String rankingName,
      List<EntryDto> rankingEntries,
      RankedMode rankedMode
  ) {
    if (rankingEntries.size() <= config.getAccountLimit()) {
      printRankingService.printSinglePage(event, rankingName, rankingEntries,
          getSinglePagePrintingFunction());
    } else {
      printRankingService.printMultiPage(event, rankingName, rankingEntries,
          getMultiPagePrintingFunction(rankingName), rankedMode);
    }
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
      public void printEnding(GenericEvent genericEvent, String formattedRanking,
          RankedMode rankedMode) {
        SlashCommandInteractionEvent specificEvent = (SlashCommandInteractionEvent) genericEvent;
        MessageCreateBuilder messageBuilder = new MessageCreateBuilder();
        Button pageButton = Button.primary(FINAL_PAGE.getId(), "Make page public");
        messageBuilder.addActionRow(pageButton);
        String finalMessage = formattedRanking + discordRankingFormatter.footer();
        messageBuilder.addContent(finalMessage);
        Button rankingButton = Button.primary(rankingName + rankedMode.formatForDiscordButton(),
            "Make whole ranking public");
        messageBuilder.addActionRow(rankingButton);
        specificEvent.getHook().sendMessage(messageBuilder.build()).queue();
      }
    };
  }

  private List<EntryDto> getRankedAccounts(
      SlashCommandInteractionEvent event,
      String rankingName,
      Ranking ranking,
      boolean forceRefresh,
      RankedMode rankedMode
  ) {
    Optional<List<Account>> cachedAccounts = accountsCache.find(
        event.getGuild().getId(), rankingName, rankedMode);
    List<Account> rankingAccounts;
    if (cachedAccounts.isEmpty() || forceRefresh) {
      rankingAccounts = getRankingEntries(ranking, event.getHook(), rankedMode);
    } else {
      rankingAccounts = cachedAccounts.get();
    }
    return toEntryDtos(rankingAccounts);
  }

  private List<EntryDto> toEntryDtos(List<Account> rankingAccounts) {
    AtomicInteger index = new AtomicInteger(1);
    return rankingAccounts.stream()
        .sorted()
        .map(account ->
            new EntryDto(
                index.getAndIncrement(),
                account.getName(),
                emojiFromTier(account.getRank().getTier()),
                account.getRank().getDivision().toString(),
                account.getRank().getLeaguePoints(),
                account.getRank().getWinrate().getWins().toString(),
                account.getRank().getWinrate().getLosses().toString(),
                account.getRank().getWinrate().getPercentage().toString()
            )
        )
        .toList();
  }

  private List<Account> getRankingEntries(Ranking ranking, InteractionHook hook,
      RankedMode rankedMode) {
    List<Account> accounts = riotAccountRepository.enrichAccountsWithRankedStats(
        ranking.getAccounts(), rankedMode);
    accountsCache.save(hook.getInteraction().getGuild().getId(), ranking.getId(), accounts,
        rankedMode);
    return accounts;
  }
}
