package com.desierto.Ranky.infrastructure.service;

import static com.desierto.Ranky.infrastructure.utils.DiscordExceptionHandler.handleExceptionOnSlashCommandEvent;
import static com.desierto.Ranky.infrastructure.utils.DiscordMessages.COMMAND_NOT_ALLOWED;
import static com.desierto.Ranky.infrastructure.utils.DiscordMessages.EXECUTE_COMMAND_FROM_SERVER;

import com.desierto.Ranky.domain.entity.Account;
import com.desierto.Ranky.domain.entity.Ranking;
import com.desierto.Ranky.domain.exception.ConfigChannelNotFoundException;
import com.desierto.Ranky.domain.exception.ranking.RankingNotFoundException;
import com.desierto.Ranky.domain.repository.RiotAccountRepository;
import com.desierto.Ranky.infrastructure.configuration.ConfigLoader;
import com.desierto.Ranky.infrastructure.repository.ConfigChannelRankingRepository;
import com.desierto.Ranky.infrastructure.utils.DiscordOptionRetriever;
import com.google.gson.Gson;
import java.util.List;
import java.util.logging.Logger;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AddAccountsService {

  public static final Logger log = Logger.getLogger("AddAccountsService.class");

  @Autowired
  private ConfigLoader config;

  @Autowired
  private DiscordOptionRetriever discordOptionRetriever;

  @Autowired
  private Gson gson;

  @Autowired
  private RiotAccountRepository riotAccountRepository;

  public void execute(SlashCommandInteractionEvent event) {
    if (event.getMember().getRoles().stream()
        .anyMatch(role -> role.getName().equalsIgnoreCase(config.getRankyUserRole()))) {
      if (event.isFromGuild()) {
        InteractionHook hook = event.getHook();
        String rankingName = discordOptionRetriever.fromEventGetRankingName(event);
        try {
          ConfigChannelRankingRepository rankingRepository = new ConfigChannelRankingRepository(
              config,
              event.getGuild(),
              gson
          );
          Ranking ranking = rankingRepository.read(rankingName);
          List<Account> accountsToAdd = discordOptionRetriever.fromEventGetAccountList(event)
              .stream()
              .filter(Account::isNotEmpty)
              .map(account -> {
                log.info("INTO ENRICHMENT WITH ACCOUNT: " + account.getNameAndTagLine());
                return riotAccountRepository.enrichIdentification(account);
              })
              .filter(account -> {
                if (account.getId().isEmpty()) {
                  hook.sendMessage("Couldn't retrieve accountId for the following account: "
                      + account.getNameAndTagLine()).queue();
                  return false;
                }
                if (ranking.getAccounts().stream().map(Account::getName)
                    .anyMatch(gameName -> gameName.equalsIgnoreCase(account.getName()))) {
                  hook.sendMessage("This account is already present in the ranking.").queue();
                  return false;
                }
                return true;
              })
              .toList();
          accountsToAdd.forEach(ranking::addAccount);
          rankingRepository.update(ranking);
          if (!accountsToAdd.isEmpty()) {
            hook.sendMessage("Accounts added successfully!").queue();
          }
        } catch (ConfigChannelNotFoundException | RankingNotFoundException e) {
          handleExceptionOnSlashCommandEvent(e, event);
        }
      } else {
        event.getHook().sendMessage(EXECUTE_COMMAND_FROM_SERVER.getMessage()).queue();
      }
    } else {
      event.getHook().sendMessage(COMMAND_NOT_ALLOWED.getMessage()).queue();
    }
  }
}
