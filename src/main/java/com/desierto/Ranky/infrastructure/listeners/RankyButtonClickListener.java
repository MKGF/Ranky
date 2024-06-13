package com.desierto.Ranky.infrastructure.listeners;

import static com.desierto.Ranky.infrastructure.utils.DiscordButtons.FINAL_PAGE;
import static com.desierto.Ranky.infrastructure.utils.DiscordButtons.PAGE;
import static com.desierto.Ranky.infrastructure.utils.DiscordRankingToEmojiMapper.emojiFromTier;

import com.desierto.Ranky.application.AccountsCache;
import com.desierto.Ranky.domain.entity.Account;
import com.desierto.Ranky.infrastructure.configuration.ConfigLoader;
import com.desierto.Ranky.infrastructure.dto.EntryDTO;
import com.desierto.Ranky.infrastructure.utils.DiscordProgressBar;
import com.desierto.Ranky.infrastructure.utils.DiscordRankingFormatter;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
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

  @PostConstruct
  private void postConstruct() {
    bot.addEventListener(this);
    log.info(String.format("Added %s to the bot!", this.getClass().getName()));
  }

  @Override
  public void onButtonInteraction(ButtonInteractionEvent event) {
    log.info("ENTERED BUTTON INTERACTION LISTENER");
    if (isPageButton(event)) {
      event.getChannel()
          .sendMessage(event.getMessage().getContentRaw() + "\n" + discordRankingFormatter.footer())
          .complete();
      try {
        TimeUnit.SECONDS.sleep(1);
      } catch (InterruptedException ignored) {
      }
      event.reply("Shared successfully").setEphemeral(true).queue();
    } else if (isFinalPageButton(event)) {
      event.getChannel().sendMessage(event.getMessage().getContentRaw()).complete();
      try {
        TimeUnit.SECONDS.sleep(1);
      } catch (InterruptedException ignored) {
      }
      event.reply("Shared successfully").setEphemeral(true).queue();
    } else {
      String rankingName = event.getButton().getId();
      Optional<List<Account>> accounts = accountsCache.find(
          event.getGuild().getId() + ":" + rankingName);
      if (accounts.isPresent()) {
        List<EntryDTO> rankingEntries = toEntryDtos(accounts.get(), Optional.empty());
        if (rankingEntries.size() <= config.getAccountLimit()) {
          handleSinglePageRanking(event, rankingName, rankingEntries);
        } else {
          handleMultiPageRanking(event, rankingName, rankingEntries);
        }
        event.reply("Shared successfully").setEphemeral(true).queue();
      } else {
        event.reply("Action expired. Refresh the ranking to publish it.").setEphemeral(true)
            .queue();
      }
    }
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

  private void handleMultiPageRanking(ButtonInteractionEvent event, String rankingName,
      List<EntryDTO> rankingEntries) {
    int numberOfEntries = rankingEntries.size();
    int numberOfFractions = numberOfEntries / config.getAccountLimit() + 1;
    List<List<EntryDTO>> fractions = new ArrayList<>();
    for (int i = 0; i < numberOfFractions; i++) {
      int beginning = config.getAccountLimit() * i;
      int possibleEnd = (config.getAccountLimit() * (i + 1));
      int end = Math.min(possibleEnd, numberOfEntries);
      fractions.add(rankingEntries.subList(beginning, end));
    }
    for (int i = 0; i < fractions.size(); i++) {
      MessageCreateBuilder messageBuilder = new MessageCreateBuilder();
      String formattedRanking =
          discordRankingFormatter.formatRankingEntries(fractions.get(i));
      if (i == 0) {
        messageBuilder.addContent(discordRankingFormatter.title(rankingName) + formattedRanking);
      } else if (i != fractions.size() - 1) {
        messageBuilder.addContent(formattedRanking);
      } else {
        messageBuilder.addContent(formattedRanking + discordRankingFormatter.footer());
      }
      event.getChannel().sendMessage(messageBuilder.build()).complete();
    }
  }

  private void handleSinglePageRanking(ButtonInteractionEvent event, String rankingName,
      List<EntryDTO> rankingEntries) {
    String formattedRanking = discordRankingFormatter.formatRankingEntries(rankingEntries);
    String finalMessage = discordRankingFormatter.title(rankingName, "1") + formattedRanking
        + discordRankingFormatter.footer();
    MessageCreateBuilder messageBuilder = new MessageCreateBuilder();
    messageBuilder.addContent(finalMessage);
    event.getChannel().sendMessage(messageBuilder.build()).complete();
  }
}
