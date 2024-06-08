package com.desierto.Ranky.infrastructure.utils;

import com.desierto.Ranky.infrastructure.configuration.ConfigLoader;
import com.desierto.Ranky.infrastructure.dto.EntryDTO;
import java.util.List;
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
  private static final int NAME_LENGTH = 22;
  @Autowired
  private ConfigLoader config;

  public String formatRankingEntries(List<EntryDTO> entries, String title) {
    StringBuilder sb = new StringBuilder();
    sb.append(HEADER).append(title.toUpperCase());
    sb.append(LINE_BREAK);
    sb.append(LINE_BREAK);
    entries.forEach(entry -> {
      sb.append(TAB);
      sb.append(CODE_LINE);
      sb.append(entry.index());
      sb.append(DASH);
      sb.append(appendName(entry.name()));
      sb.append(CODE_LINE);
      sb.append(spaces(1));
      sb.append(entry.emoji());
      sb.append(spaces(1));
      sb.append(CODE_LINE);
      sb.append(entry.division());
      sb.append(spaces(1));
      sb.append(entry.leaguePoints());
      sb.append(LP);
      sb.append(SEPARATOR);
      sb.append(entry.winrate());
      sb.append(CODE_LINE);
      sb.append(LINE_BREAK);
    });
    sb.append(footer());
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
    int length = name.length();
    return name + spaces(NAME_LENGTH - length);
  }

  private String footer() {
    return LINE_BREAK + config.getCreator() /*+ LINE_BREAK + config.getSponsors()*/;
  }
}
