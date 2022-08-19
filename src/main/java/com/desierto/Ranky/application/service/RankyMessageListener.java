package com.desierto.Ranky.application.service;

import com.desierto.Ranky.application.service.dto.RankingConfigurationWithMessageId;
import com.desierto.Ranky.domain.entity.Account;
import com.desierto.Ranky.domain.exception.ConfigChannelNotFoundException;
import com.desierto.Ranky.domain.exception.RankingAlreadyExistsException;
import com.desierto.Ranky.domain.exception.ranking.RankingNotFoundException;
import com.desierto.Ranky.domain.repository.RiotAccountRepository;
import com.desierto.Ranky.domain.valueobject.RankingConfiguration;
import com.desierto.Ranky.infrastructure.Ranky;
import com.google.gson.Gson;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
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
  public static final String RANKING_COMMAND = "/ranking";
  public static final String HELP_COMMAND = "/helpRanky";
  public static final String PRIVATE_CONFIG_CHANNEL = "config-channel";
  public static final String RANKY_USER_ROLE = "Ranky user";
  public static final int RANKING_LIMIT = 100;

  @Autowired
  private RiotAccountRepository riotAccountRepository;

  @Override
  @SneakyThrows
  public void onMessageReceived(MessageReceivedEvent event) {

    Gson gson = new Gson();
    JDA bot = event.getJDA();
    Guild guild = event.getGuild();
    User user = event.getAuthor();
    Member member = guild.getMember(user);

    if (event.getMessage().getContentRaw().startsWith(Ranky.prefix)) {
      String command = event.getMessage().getContentRaw();
      if (command.contains(HELP_COMMAND)) {
        help(event);
      } else if (command.contains(CREATE_COMMAND) && member != null && member
          .getRoles().stream()
          .anyMatch(role -> role.getName().equalsIgnoreCase(RANKY_USER_ROLE))) {
        createRanking(event, gson, bot, command);
      } else
//      if (command.contains(DEADLINE_COMMAND)) {
//        setDeadline(event, gson, bot, command);
//      }
        if (command.contains(ADD_ACCOUNT_COMMAND) && member != null && member
            .getRoles().stream()
            .anyMatch(role -> role.getName().equalsIgnoreCase(RANKY_USER_ROLE))) {
          addAccount(event, gson, bot, command);
        } else if (command.contains(ADD_MULTIPLE_COMMAND) && member != null && member
            .getRoles().stream()
            .anyMatch(role -> role.getName().equalsIgnoreCase(RANKY_USER_ROLE))) {
          addAccounts(event, gson, bot, command);
        } else if (command.contains(REMOVE_ACCOUNT_COMMAND) && member != null && member
            .getRoles().stream()
            .anyMatch(role -> role.getName().equalsIgnoreCase(RANKY_USER_ROLE))) {
          removeAccount(event, gson, bot, command);
        } else if (command.contains(RANKING_COMMAND)) {
          queryRanking(event, bot, command);
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
            "- /ranking \"RANKINGNAME\" gives the soloQ information of the accounts in the ranking ordered by rank.";
    EmbedBuilder embed = new EmbedBuilder();
    embed.setTitle("RANKY HELP");
    embed.setDescription(helpMessage);
    embed.addField("Creator", "Maiky", false);
    embed.setColor(0x000000);
    event.getChannel().sendMessageEmbeds(embed.build()).queue();
    embed.clear();
  }

  protected void createRanking(MessageReceivedEvent event, Gson gson, JDA bot, String command) {
    TextChannel channel = null;
    try {
      channel = getConfigChannel(bot);
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

  protected void queryRanking(MessageReceivedEvent event, JDA bot, String command) {
    String rankingName = getRankingName(command);
    TextChannel configChannel = getConfigChannel(bot);
    if (rankingExists(configChannel, rankingName)) {
      RankingConfiguration rankingConfiguration = getRanking(configChannel, rankingName);
      List<Optional<Account>> optionals = rankingConfiguration.getAccounts().stream()
          .map(s -> riotAccountRepository.getAccount(s)).collect(
              Collectors.toList());
      List<Account> accounts = optionals.stream().filter(Optional::isPresent).map(Optional::get)
          .sorted().collect(
              Collectors.toList());
      EmbedBuilder ranking = new EmbedBuilder();
      ranking.setTitle("\uD83D\uDC51 RANKING " + rankingName.toUpperCase() + " \uD83D\uDC51");
      String accountsToText = accounts.stream().map(Account::getFormattedForRanking).collect(
          Collectors.joining("\n"));
      ranking.setDescription(accountsToText);
      ranking.addField("Creator", "Maiky", false);
      ranking.setColor(0x000000);
      event.getChannel().sendMessageEmbeds(ranking.build()).queue();
      ranking.clear();
    }
  }

  protected void setDeadline(MessageReceivedEvent event, Gson gson, JDA bot, String command) {
    String rankingName = getRankingName(command);
    String deadlineString = getParameter(command, rankingName);
    TextChannel configChannel = getConfigChannel(bot);
    if (rankingExists(configChannel, rankingName)) {
      RankingConfigurationWithMessageId ranking = getRankingWithMessageId(configChannel,
          rankingName);
      try {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate deadlineLocalDate = LocalDate.parse(deadlineString, df);
        LocalDateTime deadline = deadlineLocalDate.atTime(23, 59, 59);
        ranking.setDeadline(deadline);
        configChannel
            .editMessageById(ranking.getMessageId(), gson.toJson(ranking.getRankingConfiguration()))
            .queue();
        event.getChannel().sendMessage("Account successfully added to the ranking.").queue();
      } catch (DateTimeParseException e) {
        rethrowExceptionAfterNoticingTheServer(event, e);
      }
    }
  }

  protected void addAccount(MessageReceivedEvent event, Gson gson, JDA bot, String command) {
    String rankingName = getRankingName(command);
    String accountToAdd = getParameter(command, rankingName);
    TextChannel configChannel = getConfigChannel(bot);
    if (rankingExists(configChannel, rankingName)) {
      RankingConfigurationWithMessageId ranking = getRankingWithMessageId(configChannel,
          rankingName);
      ranking.addAccount(accountToAdd);
      configChannel
          .editMessageById(ranking.getMessageId(), gson.toJson(ranking.getRankingConfiguration()))
          .queue();
      event.getChannel().sendMessage("Account successfully added to the ranking.").queue();
    }
  }

  protected void addAccounts(MessageReceivedEvent event, Gson gson, JDA bot, String command) {
    String rankingName = getRankingName(command);
    List<String> accountsToAdd = getAccountsToAdd(command, rankingName);
    TextChannel configChannel = getConfigChannel(bot);
    if (rankingExists(configChannel, rankingName)) {
      RankingConfigurationWithMessageId ranking = getRankingWithMessageId(configChannel,
          rankingName);
      ranking.addAccounts(accountsToAdd);
      configChannel
          .editMessageById(ranking.getMessageId(), gson.toJson(ranking.getRankingConfiguration()))
          .queue();
      event.getChannel().sendMessage("Accounts successfully added to the ranking.").queue();
    }
  }

  protected void removeAccount(MessageReceivedEvent event, Gson gson, JDA bot, String command) {
    String rankingName = getRankingName(command);
    String accountToRemove = getParameter(command, rankingName);
    TextChannel configChannel = getConfigChannel(bot);
    if (rankingExists(configChannel, rankingName)) {
      RankingConfigurationWithMessageId ranking = getRankingWithMessageId(configChannel,
          rankingName);
      ranking.removeAccount(accountToRemove);
      configChannel
          .editMessageById(ranking.getMessageId(), gson.toJson(ranking.getRankingConfiguration()))
          .queue();
      event.getChannel().sendMessage("Account successfully removed from the ranking.").queue();
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

  protected RankingConfiguration getRanking(TextChannel channel, String rankingName) {
    return RankingConfiguration
        .fromMessage(channel.getHistory().retrievePast(RANKING_LIMIT).complete().stream()
            .filter(message -> {
              Optional<RankingConfiguration> optionalRanking = RankingConfiguration
                  .fromMessageIfPossible(message);
              if (optionalRanking.isPresent()) {
                return optionalRanking.get().getName()
                    .equalsIgnoreCase(rankingName);
              }
              return false;
            }).findFirst()
            .orElseThrow(
                RankingNotFoundException::new));
  }

  protected RankingConfigurationWithMessageId getRankingWithMessageId(TextChannel channel,
      String rankingName) {
    return RankingConfigurationWithMessageId
        .fromMessage(channel.getHistory().retrievePast(RANKING_LIMIT).complete().stream()
            .filter(message -> {
              Optional<RankingConfiguration> optionalRanking = RankingConfiguration
                  .fromMessageIfPossible(message);
              if (optionalRanking.isPresent()) {
                return optionalRanking.get().getName()
                    .equalsIgnoreCase(rankingName);
              }
              return false;
            }).findFirst()
            .orElseThrow(
                RankingNotFoundException::new));
  }

  protected String getRankingName(String command) {
    String[] words = command.split("\"");
    return words[1];
  }

  protected String getParameter(String command, String rankingName) {
    return command
        .substring(command.indexOf(rankingName) + rankingName.length() + 2);
  }

  protected List<String> getAccountsToAdd(String command, String rankingName) {
    String concatedAccounts = command
        .substring(command.indexOf(rankingName) + rankingName.length() + 2);
    String[] accounts = concatedAccounts.split(",");
    return Arrays.asList(accounts);
  }

  protected TextChannel getConfigChannel(JDA bot) {
    return bot.getTextChannelsByName(PRIVATE_CONFIG_CHANNEL, true).stream()
        .findFirst().orElseThrow(
            ConfigChannelNotFoundException::new);
  }

}

