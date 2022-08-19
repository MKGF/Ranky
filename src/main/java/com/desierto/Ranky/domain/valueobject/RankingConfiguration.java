package com.desierto.Ranky.domain.valueobject;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.Message;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RankingConfiguration {

  String name;

  List<String> accounts;

//  LocalDateTime deadline;

  public RankingConfiguration(String name) {
    this.name = name;
    this.accounts = new ArrayList<>();
//    this.deadline = null;
  }

  public static RankingConfiguration fromMessage(Message message) {
    Gson gson = new Gson();
    return gson.fromJson(message.getContentRaw(), RankingConfiguration.class);
  }

  public static Optional<RankingConfiguration> fromMessageIfPossible(Message message) {
    Gson gson = new Gson();
    try {
      return Optional.of(gson.fromJson(message.getContentRaw(), RankingConfiguration.class));
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  public void addAccount(String account) {
    accounts.add(account);
  }

  public void addAccounts(List<String> accounts) {
    this.accounts.addAll(accounts);
  }

  public void removeAccount(String account) {
    accounts.remove(account);
  }

  public void setAccounts(List<String> accounts) {
    this.accounts = accounts;
  }

//  public void setDeadline(LocalDateTime deadline) {
//    this.deadline = deadline;
//  }
}
