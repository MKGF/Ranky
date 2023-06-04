package com.desierto.Ranky.application.service;

import com.desierto.Ranky.application.service.dto.RankingConfigurationWithMessageId;
import com.desierto.Ranky.domain.entity.Account;
import com.desierto.Ranky.domain.exception.ConfigChannelNotFoundException;
import com.desierto.Ranky.domain.exception.ExcessiveParamsException;
import com.desierto.Ranky.domain.exception.RankingAlreadyExistsException;
import com.desierto.Ranky.domain.exception.account.AccountNotFoundException;
import com.desierto.Ranky.domain.exception.ranking.RankingNotFoundException;
import com.desierto.Ranky.domain.repository.RiotAccountRepository;
import com.desierto.Ranky.domain.valueobject.AccountWithStream;
import com.desierto.Ranky.domain.valueobject.RankingConfiguration;
import com.desierto.Ranky.infrastructure.Ranky;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;

@AllArgsConstructor
public class RankyMessageListener extends ListenerAdapter {

  public static final Logger log = Logger.getLogger("RankyMessageListener.class");

  public static final String CREATE_COMMAND = "/create";

  public static final String DEADLINE_COMMAND = "/setDeadline";
  public static final String ADD_ACCOUNT_COMMAND = "/addAccount";
  public static final String ADD_MULTIPLE_COMMAND = "/addMultiple";
  public static final String REMOVE_ACCOUNT_COMMAND = "/removeAccount";

  public static final String ADD_STREAM_COMMAND = "/addStream";
  public static final String RANKING_COMMAND = "/ranking";
  public static final String MIGRATE_COMMAND = "/migrate";
  public static final String HELP_COMMAND = "/helpRanky";
  public static final String PRIVATE_CONFIG_CHANNEL = "config-channel";
  public static final String RANKY_USER_ROLE = "Ranky user";
  public static final int RANKING_LIMIT = 100;

  public static final int ACCOUNT_LIMIT = 10;

  @Autowired
  private RiotAccountRepository riotAccountRepository;

  @Override
  @SneakyThrows
  public void onMessageReceived(MessageReceivedEvent event) {
    if (event.getMessage().getContentRaw().startsWith(Ranky.prefix)) {
      Gson gson = new Gson();
      Guild guild = event.getGuild();
      User user = event.getAuthor();
      Member member = event.getMember();

      log.info("ARRIVED MESSAGE: " + event.getMessage().getContentRaw());
      log.info("FROM GUILD: " + guild.getName());
      log.info("FROM USER: " + user.getName() + "/" + user.getId());
      log.info("IS MEMBER NULL: " + (member == null));

      if (member != null) {
        log.info("HAS " + RANKY_USER_ROLE + " ROLE: " + member
            .getRoles().stream()
            .anyMatch(role -> role.getName().equalsIgnoreCase(RANKY_USER_ROLE)));
      }

      String command = event.getMessage().getContentRaw();

      if (command.contains(HELP_COMMAND)) {
        help(event);
      } else if (command.contains(MIGRATE_COMMAND) && member != null && member
          .getRoles().stream()
          .anyMatch(role -> role.getName().equalsIgnoreCase(RANKY_USER_ROLE))) {
        migrateRanking(event, gson, command);
      } else if (isCreateCommand(command) && member != null && member
          .getRoles().stream()
          .anyMatch(role -> role.getName().equalsIgnoreCase(RANKY_USER_ROLE))) {
        createRanking(event, gson, command);

      } else
//      if (command.contains(DEADLINE_COMMAND)) {
//        setDeadline(event, gson, command);
//      }
        if (isAddAccountCommand(command) && member != null && member
            .getRoles().stream()
            .anyMatch(role -> role.getName().equalsIgnoreCase(RANKY_USER_ROLE))) {
          addAccount(event, gson, command);
        } else if (isAddMultipleCommand(command) && member != null && member
            .getRoles().stream()
            .anyMatch(role -> role.getName().equalsIgnoreCase(RANKY_USER_ROLE))) {
          addAccounts(event, gson, command);
        } else if (isRemoveAccountCommand(command) && member != null && member
            .getRoles().stream()
            .anyMatch(role -> role.getName().equalsIgnoreCase(RANKY_USER_ROLE))) {
          removeAccount(event, gson, command);
        } else if (isAddStreamChannelCommand(command) && member != null && member.getRoles()
            .stream()
            .anyMatch(role -> role.getName().equalsIgnoreCase(RANKY_USER_ROLE))) {
          addStreamChannel(event, gson, command);
        } else if (command.contains(RANKING_COMMAND)) {
          queryRanking(event, command);
        }
    }
  }

  protected void help(MessageReceivedEvent event) {
    String helpMessage =
        "This Discord bot will use one of your channels as storage for different soloQ rankings made in the server.\n\n"
            +
            "This channel is referred to as #config-channel and only uses the last " + RANKING_LIMIT
            + " messages as storage. So please do not spam in it. If something escapes the threshold it won't be able to retrieve it anymore.\n\n"
            +
            "- /create \"RANKINGNAME\" creates a ranking with that name.\n"
            +
            "- /addAccount \"RANKINGNAME\" ACCOUNT adds the account to the ranking if it exists. Supports spaces in the name.\n"
            +
            "- /addMultiple \"RANKINGNAME\" ACCOUNT1,ACCOUNT2... adds all the accounts to the ranking if they exist.\n"
            +
            "- /removeAccount \"RANKINGNAME\" ACCOUNT removes the account from the ranking if it exists.\n"
            +
            "- /ranking \"RANKINGNAME\" gives the soloQ information of the accounts in the ranking ordered by rank,\nalong with a bubble that displays whether the player is in game or not and the link to the user's stream in case there is any.\n"
            +
            "- /addStream \"RANKINGNAME\" \"ACCOUNT\" LINKTOSTREAM adds a link that refers to the player's stream.";
    EmbedBuilder embed = new EmbedBuilder();
    embed.setTitle("RANKY HELP");
    embed.setDescription(helpMessage);
    embed.addField("Creator", "Maiky", false);
    embed.setColor(0x000000);
    event.getChannel().sendMessageEmbeds(embed.build()).queue();
    embed.clear();
  }

  protected void createRanking(MessageReceivedEvent event, Gson gson, String command) {
    TextChannel channel = null;
    try {
      channel = getConfigChannel(event.getGuild());
    } catch (ConfigChannelNotFoundException e) {
      rethrowExceptionAfterNoticingTheServer(event, e);
    }
    String rankingName = getRankingName(command);
    try {
      if (channel != null && rankingExists(channel, rankingName)) {
        throw new RankingAlreadyExistsException();
      }
    } catch (RankingAlreadyExistsException e) {
      rethrowExceptionAfterNoticingTheServer(event, e);
    }
    if (channel != null) {
      String json = gson
          .toJson(new RankingConfiguration(rankingName));
      channel.sendMessage(json).queue();
    }
  }

  @Deprecated
  protected void migrateRanking(MessageReceivedEvent event, Gson gson, String command) {
    //Does nothing
  }

  protected void queryRanking(MessageReceivedEvent event, String command) {
    String rankingName = getRankingName(command);

    TextChannel configChannel = getConfigChannel(event.getGuild());
    if (rankingExists(configChannel, rankingName)) {
      List<RankingConfiguration> rankingConfiguration = getRanking(configChannel, rankingName);
      if (rankingConfiguration.isEmpty()) {
        throw new RankingNotFoundException();
      }
      List<AccountWithStream> accountsWithStream = new ArrayList<>();
      rankingConfiguration.forEach(
          rankingConfiguration1 -> accountsWithStream.addAll(rankingConfiguration1.getAccounts()));
      List<Optional<Account>> optionals = accountsWithStream.stream()
          .map(s -> riotAccountRepository.getAccountById(s.getAccountId(), s.getStreamChannel()))
          .collect(
              Collectors.toList());
      List<Account> accounts = optionals.stream().filter(Optional::isPresent).map(Optional::get)
          .sorted().collect(
              Collectors.toList());
      EmbedBuilder ranking = new EmbedBuilder();
      ranking.setTitle("\uD83D\uDC51 RANKING " + rankingName.toUpperCase() + " \uD83D\uDC51");
      AtomicInteger index = new AtomicInteger(1);
      String accountsToText = accounts.stream()
          .map((account) -> account.getFormattedForRanking(index.getAndIncrement()))
          .collect(
              Collectors.joining("\n"));
      ranking.setDescription(accountsToText);
      ranking.addField("Creator", "Maiky | Twitter: @maikyelrenacido", false);
      ranking.setColor(0x000000);
      event.getChannel().sendMessageEmbeds(ranking.build()).queue();
      ranking.clear();
    }
  }

  protected void addAccount(MessageReceivedEvent event, Gson gson, String command) {
    String rankingName = getRankingName(command);
    String accountToAdd = getParameter(command, rankingName);
    TextChannel configChannel = getConfigChannel(event.getGuild());
    if (rankingExists(configChannel, rankingName)) {
      List<RankingConfigurationWithMessageId> ranking = getRankingWithMessageId(configChannel,
          rankingName);
      Account account = null;
      try {
        account = riotAccountRepository.getAccountByName(accountToAdd).orElseThrow(() ->
            new AccountNotFoundException(accountToAdd));
      } catch (AccountNotFoundException e) {
        rethrowExceptionAfterNoticingTheServer(event, e);
      }
      Optional<RankingConfigurationWithMessageId> messageWithRoom = ranking.stream().filter(
          rankingConfigurationWithMessageId ->
              rankingConfigurationWithMessageId.getRankingConfiguration().getAccounts().size()
                  < ACCOUNT_LIMIT).findFirst();
      if (messageWithRoom.isPresent()) {
        RankingConfigurationWithMessageId rankingMessageWithRoom = messageWithRoom.get();
        rankingMessageWithRoom.addAccount(Objects.requireNonNull(account).getId());
        configChannel
            .editMessageById(rankingMessageWithRoom.getMessageId(),
                gson.toJson(rankingMessageWithRoom.getRankingConfiguration()))
            .queue();
        event.getChannel().sendMessage("Account successfully added to the ranking.").queue();
      } else {
        RankingConfiguration newMessageForRanking = new RankingConfiguration(rankingName);
        newMessageForRanking.addAccount(Objects.requireNonNull(account).getId());
        String json = gson
            .toJson(newMessageForRanking);
        configChannel.sendMessage(json).queue();
      }
    }
  }

  protected void addAccounts(MessageReceivedEvent event, Gson gson, String command) {
    String rankingName = getRankingName(command);
    List<String> accountsToAdd = getAccountsToAdd(command, rankingName);
    TextChannel configChannel = getConfigChannel(event.getGuild());
    if (rankingExists(configChannel, rankingName)) {

      List<Account> accounts = new ArrayList<>();
      accountsToAdd.forEach(s -> {
        try {
          accounts.add(riotAccountRepository.getAccountByName(s).orElseThrow(() ->
              new AccountNotFoundException(s)));
        } catch (AccountNotFoundException e) {
          rethrowExceptionAfterNoticingTheServer(event, e);
        }
      });
      accounts.forEach(account -> {
            List<RankingConfigurationWithMessageId> ranking = getRankingWithMessageId(configChannel,
                rankingName);
            Optional<RankingConfigurationWithMessageId> messageWithRoom = ranking.stream().filter(
                rankingConfigurationWithMessageId ->
                    rankingConfigurationWithMessageId.getRankingConfiguration().getAccounts().size()
                        < ACCOUNT_LIMIT).findFirst();
            if (messageWithRoom.isPresent()) {
              RankingConfigurationWithMessageId rankingMessageWithRoom = messageWithRoom.get();
              rankingMessageWithRoom.addAccount(Objects.requireNonNull(account).getId());
              configChannel
                  .editMessageById(rankingMessageWithRoom.getMessageId(),
                      gson.toJson(rankingMessageWithRoom.getRankingConfiguration()))
                  .queue();
              event.getChannel().sendMessage("Account successfully added to the ranking.").queue();
            } else {
              RankingConfiguration newMessageForRanking = new RankingConfiguration(rankingName);
              newMessageForRanking.addAccount(Objects.requireNonNull(account).getId());
              String json = gson
                  .toJson(newMessageForRanking);
              configChannel.sendMessage(json).queue();
            }
          }
      );

    }
  }

  protected void removeAccount(MessageReceivedEvent event, Gson gson, String command) {
    String rankingName = getRankingName(command);
    String accountToRemove = getParameter(command, rankingName);
    TextChannel configChannel = getConfigChannel(event.getGuild());
    if (rankingExists(configChannel, rankingName)) {
      List<RankingConfigurationWithMessageId> rankingInMessages = getRankingWithMessageId(
          configChannel,
          rankingName);
      Account account = null;
      try {
        account = riotAccountRepository.getAccountByName(accountToRemove).orElseThrow(() ->
            new AccountNotFoundException(accountToRemove));
      } catch (AccountNotFoundException e) {
        rethrowExceptionAfterNoticingTheServer(event, e);
      }
      try {
        Account finalAccount = account;
        RankingConfigurationWithMessageId ranking = rankingInMessages.stream().filter(
                rankingConfigurationWithMessageId -> rankingConfigurationWithMessageId.getRankingConfiguration()
                    .hasAccountNamed(finalAccount)).findFirst()
            .orElseThrow(() -> new AccountNotFoundException(accountToRemove));

        ranking.removeAccount(Objects.requireNonNull(account).getId());
        configChannel
            .editMessageById(ranking.getMessageId(), gson.toJson(ranking.getRankingConfiguration()))
            .queue();
        event.getChannel().sendMessage("Account successfully removed from the ranking.").queue();
      } catch (AccountNotFoundException e) {
        rethrowExceptionAfterNoticingTheServer(event, e);
      }
    }
  }

  private void addStreamChannel(MessageReceivedEvent event, Gson gson, String command)
      throws AccountNotFoundException {
    String rankingName = getRankingName(command);
    String[] params = getWordsAfterRankingName(command, rankingName);
    if (params.length != 3) {
      rethrowExceptionAfterNoticingTheServer(event, new ExcessiveParamsException());
    }

    String account = params[1];
    String streamChannel = params[2].replace(" ", "") + " ";
    TextChannel configChannel = getConfigChannel(event.getGuild());
    if (rankingExists(configChannel, rankingName)) {
      List<RankingConfigurationWithMessageId> rankingInMessages = getRankingWithMessageId(
          configChannel,
          rankingName);

      Account riotAccount = riotAccountRepository.getAccountByName(account)
          .orElseThrow(() -> new AccountNotFoundException(account));
      RankingConfigurationWithMessageId ranking = rankingInMessages.stream().filter(
              rankingConfigurationWithMessageId -> rankingConfigurationWithMessageId.getRankingConfiguration()
                  .hasAccountNamed(riotAccount)).findFirst()
          .orElseThrow(() -> new AccountNotFoundException(account));
      ranking.addStreamChannelToAccount(streamChannel, riotAccount.getId());
      configChannel
          .editMessageById(ranking.getMessageId(), gson.toJson(ranking.getRankingConfiguration()))
          .queue();
      event.getChannel().sendMessage("Stream successfully updated for account " + account + "!")
          .queue();
    }
  }

  protected void rethrowExceptionAfterNoticingTheServer(MessageReceivedEvent event,
      RuntimeException e) throws ConfigChannelNotFoundException, RankingAlreadyExistsException {
    event.getChannel().sendMessage(e.getMessage()).queue();
    throw e;
  }

  protected boolean rankingExists(TextChannel channel, String rankingName) {
    return channel.getHistory().retrievePast(RANKING_LIMIT).complete().stream()
        .anyMatch(message -> {
          Optional<RankingConfiguration> optionalRanking = RankingConfiguration
              .fromMessageIfPossible(message);
          if (optionalRanking.isPresent()) {
            return optionalRanking.get().getName()
                .equalsIgnoreCase(rankingName);
          }
          return false;
        });
  }

  protected List<RankingConfiguration> getRanking(TextChannel channel, String rankingName) {
    return RankingConfiguration
        .fromMessages(channel.getHistory().retrievePast(RANKING_LIMIT).complete().stream()
            .filter(message -> {
              Optional<RankingConfiguration> optionalRanking = RankingConfiguration
                  .fromMessageIfPossible(message);
              if (optionalRanking.isPresent()) {
                return optionalRanking.get().getName()
                    .equalsIgnoreCase(rankingName);
              }
              return false;
            }).collect(Collectors.toList()));

  }

  protected List<RankingConfigurationWithMessageId> getRankingWithMessageId(TextChannel channel,
      String rankingName) {
    return RankingConfigurationWithMessageId
        .fromMessages(channel.getHistory().retrievePast(RANKING_LIMIT).complete().stream()
            .filter(message -> {
              Optional<RankingConfiguration> optionalRanking = RankingConfiguration
                  .fromMessageIfPossible(message);
              if (optionalRanking.isPresent()) {
                return optionalRanking.get().getName()
                    .equalsIgnoreCase(rankingName);
              }
              return false;
            }).collect(Collectors.toList()));

  }

  protected String getRankingName(String command) {
    String[] words = command.split("\"");
    return words[1];
  }

  protected String getParameter(String command, String rankingName) {
    return command
        .substring(command.indexOf(rankingName) + rankingName.length() + 2);
  }

  protected String[] getWordsAfterRankingName(String command, String rankingName) {
    return command.substring(command.indexOf(rankingName) + rankingName.length() + 2).split("\"");
  }

  protected List<String> getAccountsToAdd(String command, String rankingName) {
    String concatedAccounts = command
        .substring(command.indexOf(rankingName) + rankingName.length() + 2);
    String[] accounts = concatedAccounts.split(",");
    return Arrays.asList(accounts);
  }

  protected TextChannel getConfigChannel(Guild guild) {
    return guild.getTextChannels().stream()
        .filter(textChannel -> textChannel.getName().equalsIgnoreCase(PRIVATE_CONFIG_CHANNEL))
        .findFirst().orElseThrow(
            ConfigChannelNotFoundException::new);
  }

  protected boolean isCreateCommand(String command) {
    String[] words = command.split("\"");
    if (words.length > 2) {
      return false;
    }
    return command.startsWith(CREATE_COMMAND) && words.length == 2;
  }

  protected boolean isAddAccountCommand(String command) {
    log.info("COMMAND: " + command);
    String[] words = command.split("\"");
    log.info("SIZE OF WORDS: " + words.length);
    log.info("WORDS: " + String.join("|", words));
    if (words.length < 3) {
      return false;
    } else {
      if (words[2].contains(",")) {
        return false;
      }
      if (words.length > 3) {
        return false;
      }
      return command.startsWith(ADD_ACCOUNT_COMMAND);
    }
  }

  protected boolean isAddMultipleCommand(String command) {
    String[] words = command.split("\"");
    if (words.length < 3) {
      return false;
    } else {
      if (words.length > 3) {
        return false;
      }
      return command.startsWith(ADD_MULTIPLE_COMMAND);
    }
  }

  protected boolean isRemoveAccountCommand(String command) {
    String[] words = command.split("\"");
    if (words.length < 3) {
      return false;
    } else {
      if (words[2].contains(",")) {
        return false;
      }
      if (words.length > 3) {
        return false;
      }
      return command.startsWith(REMOVE_ACCOUNT_COMMAND);
    }
  }

  protected boolean isAddStreamChannelCommand(String command) {
    log.info("Looking if it's addStream command or not.");
    String[] words = command.split("\"");
    if (words.length != 5) {
      log.info("IT IS NOT.");
      return false;
    }
    log.info("IT IS.");
    return command.startsWith(ADD_STREAM_COMMAND);
  }

}

