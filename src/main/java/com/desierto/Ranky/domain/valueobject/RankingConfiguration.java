package com.desierto.Ranky.domain.valueobject;

import com.desierto.Ranky.domain.entity.Account;
import com.desierto.Ranky.domain.exception.account.AccountNotFoundException;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.Message;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RankingConfiguration {

  String name;

  List<AccountWithStream> accounts;

//  LocalDateTime deadline;

  public RankingConfiguration(String name) {
    this.name = name;
    this.accounts = new ArrayList<>();
//    this.deadline = null;
  }

  private static RankingConfiguration fromMessage(Message message) {
    Gson gson = new Gson();
    return gson.fromJson(message.getContentRaw(), RankingConfiguration.class);
  }

  public static List<RankingConfiguration> fromMessages(List<Message> messages) {
    Gson gson = new Gson();
    return messages.stream().map(RankingConfiguration::fromMessage).collect(Collectors.toList());
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
    accounts.add(new AccountWithStream(account, ""));
  }

  public void addAccounts(List<String> accounts) {
    this.accounts.addAll(
        accounts.stream().map(a -> new AccountWithStream(a, "")).collect(Collectors.toList()));
  }

  public void removeAccountNamed(String account) {
    accounts.removeIf(
        accountWithStream -> accountWithStream.getAccountId().equalsIgnoreCase(account));
  }

  @Deprecated
  public void setAccounts(List<String> accounts) {
//    this.accounts = accounts;
  }

  public void addStreamChannelToAccount(String streamChannel, String account)
      throws AccountNotFoundException {
    AccountWithStream accountWithStream = accounts.stream()
        .filter(a -> a.getAccountId().equalsIgnoreCase(account)).findFirst().orElseThrow(() ->
            new AccountNotFoundException(account));
    accountWithStream.setStreamChannel(streamChannel);
  }

  public boolean hasAccountNamed(Account account) {
    return this.getAccounts().stream()
        .anyMatch(accountWithStream -> accountWithStream.getAccountId()
            .equalsIgnoreCase(account.getId()));
  }

//  public void setDeadline(LocalDateTime deadline) {
//    this.deadline = deadline;
//  }
}
