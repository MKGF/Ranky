package com.desierto.Ranky.application.service.dto;

import com.desierto.Ranky.domain.exception.account.AccountNotFoundException;
import com.desierto.Ranky.domain.valueobject.RankingConfiguration;
import com.google.gson.Gson;
import java.util.List;
import java.util.stream.Collectors;
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

  private static RankingConfigurationWithMessageId fromMessage(Message message) {
    Gson gson = new Gson();
    RankingConfiguration ranking = gson
        .fromJson(message.getContentRaw(), RankingConfiguration.class);
    return new RankingConfigurationWithMessageId(ranking, message.getId());
  }

  public static List<RankingConfigurationWithMessageId> fromMessages(List<Message> messages) {
    return messages.stream().map(RankingConfigurationWithMessageId::fromMessage)
        .collect(Collectors.toList());
  }

  public void addAccount(String account) {
    rankingConfiguration.addAccount(account);
  }

  public void addAccounts(List<String> accounts) {
    rankingConfiguration.addAccounts(accounts);
  }

  public void removeAccount(String account) {
    rankingConfiguration.removeAccountNamed(account);
  }

  public void addStreamChannelToAccount(String streamChannel, String account)
      throws AccountNotFoundException {
    rankingConfiguration.addStreamChannelToAccount(streamChannel, account);
  }

//  public void setDeadline(LocalDateTime deadline) {
//    rankingConfiguration.setDeadline(deadline);
//  }
}
