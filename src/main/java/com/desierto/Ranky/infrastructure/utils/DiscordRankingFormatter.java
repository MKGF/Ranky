package com.desierto.Ranky.infrastructure.utils;

import com.desierto.Ranky.infrastructure.configuration.ConfigLoader;
import com.desierto.Ranky.infrastructure.dto.EntryDTO;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DiscordRankingFormatter {

  public static final String DASH = " - ";
  public static final String LP = "lp";
  public static final String LINE_BREAK = "\n";
  public static final String TAB = "\t";
  public static final String CODE_LINE = "`";
  public static final String UNDERLINE = "__";
  public static final String BOLD = "**";
  public static final String HEADER = "# ";
  private static final String SEPARATOR = " | ";
  private static final String SPACE = " ";
  private static final int NAME_LENGTH = 25;
  public static final double PERCENTAGE_OF_PIXELS_BIGGER = 0.571428;
  private static final int LP_LENGTH = 4;
  private static final int WINS_LOSSES_LENGTH = 6;
  private static final int WINRATE_LENGTH = 5;
  @Autowired
  private ConfigLoader config;

  public String formatRankingEntries(List<EntryDTO> entries) {
    StringBuilder sb = new StringBuilder();
    entries.forEach(entry -> {
      sb.append(CODE_LINE);
      sb.append(appendName(entry.index() + DASH + entry.name()));
      sb.append(CODE_LINE);
      sb.append(spaces(1));
      sb.append(entry.emoji());
      sb.append(spaces(1));
      sb.append(CODE_LINE);
      sb.append(entry.division());
      sb.append(spaces(1));
      sb.append(appendLeaguePoints(entry.leaguePoints()));
      sb.append(SEPARATOR);
      sb.append(appendWins(entry.wins()));
      sb.append(appendLosses(entry.losses()));
      sb.append(SEPARATOR);
      sb.append(appendWinrate(entry.winrate()));
      sb.append(CODE_LINE);
      sb.append(LINE_BREAK);
    });
    if (!entries.isEmpty()) {
      return sb.toString();
    }
    return "";
  }

  private String spaces(int numberOfSpaces) {
    String spaces = "";
    if (numberOfSpaces > 0) {
      for (int i = 0; i < numberOfSpaces; i++) {
        spaces = spaces.concat(SPACE);
      }
    }
    return spaces;
  }

  private String appendName(String name) {
    double length = name.length();
    AtomicInteger amountOfIdeographicChars = new AtomicInteger(0);
    name.codePoints().forEach(codePoint -> {
      if (Character.getType(codePoint) == 5) { //If it's korean, chinese, vietnamese... character
        amountOfIdeographicChars.getAndIncrement();
      }
    });
    length += PERCENTAGE_OF_PIXELS_BIGGER * amountOfIdeographicChars.get();
    return name + spaces(NAME_LENGTH - Double.valueOf(length).intValue());
  }

  private String appendLeaguePoints(int lp) {
    int length = String.valueOf(lp).length();
    return lp + LP + spaces(LP_LENGTH - length);
  }

  private String appendWins(String wins) {
    int length = wins.length();
    return "Wins: " + wins + spaces(WINS_LOSSES_LENGTH - length);
  }

  private String appendLosses(String losses) {
    int length = losses.length();
    return "Losses: " + losses + spaces(WINS_LOSSES_LENGTH - length);
  }

  private String appendWinrate(String winrate) {
    int length = winrate.length();
    return "Winrate: " + winrate + "%" + spaces(WINRATE_LENGTH - length);
  }

  public String footer() {
    return LINE_BREAK /*+ config.getCreator() + LINE_BREAK + config.getSponsors()*/;
  }

  public String title(String title, String page) {
    return HEADER + title.toUpperCase() + " / PAGE " + page + LINE_BREAK + LINE_BREAK;
  }

  public String title(String title) {
    return HEADER + title.toUpperCase() + LINE_BREAK + LINE_BREAK;
  }
}
