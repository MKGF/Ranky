package com.desierto.Ranky.infrastructure.service;

import static com.desierto.Ranky.infrastructure.utils.DiscordButtons.FINAL_PAGE;
import static com.desierto.Ranky.infrastructure.utils.DiscordButtons.PAGE;
import static com.desierto.Ranky.infrastructure.utils.DiscordExceptionHandler.handleExceptionOnSlashCommandEvent;
import static com.desierto.Ranky.infrastructure.utils.DiscordMessages.EXECUTE_COMMAND_FROM_SERVER;
import static com.desierto.Ranky.infrastructure.utils.DiscordRankingToEmojiMapper.emojiFromTier;

import com.desierto.Ranky.application.AccountsCache;
import com.desierto.Ranky.domain.entity.Account;
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
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import lombok.AllArgsConstructor;
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

  @Autowired
  private AccountsCache accountsCache;

  @Autowired
  private PrintRankingService printRankingService;

  public void execute(SlashCommandInteractionEvent event) {
    if (event.isFromGuild()) {
      InteractionHook hook = event.getHook();
      String rankingName = discordOptionRetriever.fromEventGetObjectName(event);
      try {
        ConfigChannelRankingRepository rankingRepository = new ConfigChannelRankingRepository(
            config,
            event.getGuild(),
            gson
        );
        Ranking ranking = rankingRepository.read(rankingName);
        Optional<List<Account>> cachedAccounts = accountsCache.find(
            event.getGuild().getId() + ":" + rankingName);
        List<Account> rankingAccounts;
        Message progressBar = null;
        if (cachedAccounts.isEmpty()) {
          progressBar = hook.sendMessage(DiscordProgressBar.getProgress(0)).complete();
          rankingAccounts = getRankingEntries(ranking, hook, progressBar);
        } else {
          rankingAccounts = cachedAccounts.get();
        }
        List<EntryDTO> rankingEntries = toEntryDtos(rankingAccounts,
            Optional.ofNullable(progressBar));
        if (rankingEntries.size() <= config.getAccountLimit()) {
          printRankingService.printSinglePage(event, rankingName, rankingEntries,
              getSinglePagePrintingFunction());
        } else {
          printRankingService.printMultiPage(event, rankingName, rankingEntries,
              getMultiPagePrintingFunction(rankingName));
        }
      } catch (ConfigChannelNotFoundException | RankingNotFoundException e) {
        handleExceptionOnSlashCommandEvent(e, event);
      }

    } else {
      event.getHook().sendMessage(EXECUTE_COMMAND_FROM_SERVER.getMessage()).queue();
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

  private List<EntryDTO> toEntryDtos(List<Account> rankingAccounts, Optional<Message> progressBar) {
    AtomicInteger index = new AtomicInteger(1);
    return rankingAccounts.stream()
        .sorted()
        .map(account -> {
              progressBar.ifPresent(message -> message.editMessage(
                      DiscordProgressBar.getProgress(
                          50 + (index.get() * 100 / rankingAccounts.size()) / 2))
                  .complete());
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
      return riotAccountRepository.enrichWithSoloQStats(account);
    }).toList();

    accountsCache.save(hook.getInteraction().getGuild().getId() + ":" + ranking.getId(), accounts);

    return accounts;
  }
}
