package com.desierto.Ranky.infrastructure.service;

import com.desierto.Ranky.infrastructure.configuration.ConfigLoader;
import com.desierto.Ranky.infrastructure.dto.EntryDTO;
import com.desierto.Ranky.infrastructure.utils.DiscordRankingFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.events.GenericEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PrintRankingService {

  @Autowired
  private ConfigLoader config;

  @Autowired
  private DiscordRankingFormatter discordRankingFormatter;

  public void printSinglePage(GenericEvent event, String rankingName,
      List<EntryDTO> rankingEntries, SinglePagePrintingFunction function) {
    String formattedRanking = discordRankingFormatter.formatRankingEntries(rankingEntries);
    String finalMessage = discordRankingFormatter.title(rankingName) + formattedRanking
        + discordRankingFormatter.footer();
    function.print(event, finalMessage);
  }

  public void printMultiPage(GenericEvent event, String title,
      List<EntryDTO> rankingEntries,
      MultiPagePrintingFunction function) {
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
      String formattedRanking = discordRankingFormatter.formatRankingEntries(fractions.get(i));
      String finalRanking =
          !title.isEmpty() ? discordRankingFormatter.title(title, String.valueOf(i + 1))
              + formattedRanking : formattedRanking;
      if (i == 0) {
        function.printBeginning(event, finalRanking);
      } else if (i != fractions.size() - 1) {
        function.printGeneric(event, finalRanking);
      } else {
        function.printEnding(event, finalRanking);
      }
    }
  }

}
