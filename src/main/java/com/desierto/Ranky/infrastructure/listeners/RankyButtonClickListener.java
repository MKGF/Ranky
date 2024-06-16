package com.desierto.Ranky.infrastructure.listeners;

import static com.desierto.Ranky.infrastructure.utils.DiscordButtons.FINAL_PAGE;
import static com.desierto.Ranky.infrastructure.utils.DiscordButtons.PAGE;
import static com.desierto.Ranky.infrastructure.utils.DiscordRankingToEmojiMapper.emojiFromTier;

import com.desierto.Ranky.application.AccountsCache;
import com.desierto.Ranky.domain.entity.Account;
import com.desierto.Ranky.infrastructure.configuration.ConfigLoader;
import com.desierto.Ranky.infrastructure.dto.EntryDTO;
import com.desierto.Ranky.infrastructure.service.MultiPagePrintingFunction;
import com.desierto.Ranky.infrastructure.service.PrintRankingService;
import com.desierto.Ranky.infrastructure.service.SinglePagePrintingFunction;
import com.desierto.Ranky.infrastructure.utils.DiscordProgressBar;
import com.desierto.Ranky.infrastructure.utils.DiscordRankingFormatter;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RankyButtonClickListener extends ListenerAdapter {

  public static final Logger log = Logger.getLogger("RankyButtonClickListener.class");

  @Autowired
  private JDA bot;

  @Autowired
  private AccountsCache accountsCache;

  @Autowired
  private ConfigLoader config;

  @Autowired
  private DiscordRankingFormatter discordRankingFormatter;

  @Autowired
  private PrintRankingService printRankingService;

  @PostConstruct
  private void postConstruct() {
    bot.addEventListener(this);
    log.info(String.format("Added %s to the bot!", this.getClass().getName()));
  }

  @Override
  public void onButtonInteraction(ButtonInteractionEvent event) {
    log.info("ENTERED BUTTON INTERACTION LISTENER");
    if (isPageButton(
        event)) { //Case for a big ranking that needed to be paged, and we just want to show a fraction of the ranking
      event.getChannel()
          .sendMessage(event.getMessage().getContentRaw() + "\n" + discordRankingFormatter.footer())
          .complete();
      try {
        TimeUnit.SECONDS.sleep(1);
      } catch (InterruptedException ignored) {
      }
      event.reply("Shared successfully").setEphemeral(true).queue();
    } else if (isFinalPageButton(
        event)) { //Case for a big ranking that needed to be paged, and we want to show the latest fraction (doesn't need a footer)
      event.getChannel().sendMessage(event.getMessage().getContentRaw()).complete();
      try {
        TimeUnit.SECONDS.sleep(1);
      } catch (InterruptedException ignored) {
      }
      event.reply("Shared successfully").setEphemeral(true).queue();
    } else { //Case for whole rankings to be made public
      String rankingName = event.getButton().getId();
      Optional<List<Account>> accounts = accountsCache.find(
          event.getGuild().getId() + ":" + rankingName);
      if (accounts.isPresent()) {
        List<EntryDTO> rankingEntries = toEntryDtos(accounts.get(), Optional.empty());
        if (rankingEntries.size() <= config.getAccountLimit()) {
          printRankingService.printSinglePage(event, rankingName, rankingEntries,
              getSinglePagePrintingFunction(rankingName));
        } else {
          printRankingService.printMultiPage(event, "", rankingEntries,
              getMultiPagePrintingFunction(rankingName));
        }
        event.reply("Shared successfully").setEphemeral(true).queue();
      } else {
        event.reply("Action expired. Refresh the ranking to publish it.").setEphemeral(true)
            .queue();
      }
    }
  }

  @NotNull
  private SinglePagePrintingFunction getSinglePagePrintingFunction(String title) {
    return (genericEvent, formattedRanking) -> {
      ButtonInteractionEvent specificEvent = (ButtonInteractionEvent) genericEvent;
      MessageCreateBuilder messageBuilder = new MessageCreateBuilder();
      messageBuilder.addContent(discordRankingFormatter.title(title) + formattedRanking
          + discordRankingFormatter.footer());
      specificEvent.getChannel().sendMessage(messageBuilder.build()).complete();
    };
  }

  @NotNull
  private MultiPagePrintingFunction getMultiPagePrintingFunction(String rankingName) {
    return new MultiPagePrintingFunction() {
      @Override
      public void printBeginning(GenericEvent genericEvent, String formattedRanking) {
        ButtonInteractionEvent specificEvent = (ButtonInteractionEvent) genericEvent;
        MessageCreateBuilder messageBuilder = new MessageCreateBuilder();
        messageBuilder.addContent(
            discordRankingFormatter.title(rankingName) + formattedRanking);
        specificEvent.getChannel().sendMessage(messageBuilder.build()).complete();
      }

      @Override
      public void printGeneric(GenericEvent genericEvent, String formattedRanking) {
        ButtonInteractionEvent specificEvent = (ButtonInteractionEvent) genericEvent;
        MessageCreateBuilder messageBuilder = new MessageCreateBuilder();
        messageBuilder.addContent(formattedRanking);
        specificEvent.getChannel().sendMessage(messageBuilder.build()).complete();
      }

      @Override
      public void printEnding(GenericEvent genericEvent, String formattedRanking) {
        ButtonInteractionEvent specificEvent = (ButtonInteractionEvent) genericEvent;
        MessageCreateBuilder messageBuilder = new MessageCreateBuilder();
        messageBuilder.addContent(formattedRanking + discordRankingFormatter.footer());
        specificEvent.getChannel().sendMessage(messageBuilder.build()).complete();
      }
    };
  }

  private boolean isPageButton(ButtonInteractionEvent event) {
    return Objects.equals(event.getButton().getId(), PAGE.getId());
  }

  private boolean isFinalPageButton(ButtonInteractionEvent event) {
    return Objects.equals(event.getButton().getId(), FINAL_PAGE.getId());
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
}
