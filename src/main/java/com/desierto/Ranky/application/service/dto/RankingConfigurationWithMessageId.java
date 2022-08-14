package com.desierto.Ranky.application.service.dto;

import com.desierto.Ranky.domain.valueobject.RankingConfiguration;
import com.google.gson.Gson;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.Message;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RankingConfigurationWithMessageId {

  RankingConfiguration rankingConfiguration;

  String messageId;

  public static RankingConfigurationWithMessageId fromMessage(Message message) {
    Gson gson = new Gson();
    RankingConfiguration ranking = gson
        .fromJson(message.getContentRaw(), RankingConfiguration.class);
    return new RankingConfigurationWithMessageId(ranking, message.getId());
  }

  public void addAccount(String account) {
    rankingConfiguration.addAccount(account);
  }

  public void addAccounts(List<String> accounts) {
    rankingConfiguration.addAccounts(accounts);
  }
}
