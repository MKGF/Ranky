package com.desierto.Ranky.infrastructure.service;

import static com.desierto.Ranky.infrastructure.utils.DiscordMessages.COMMAND_NOT_ALLOWED;
import static com.desierto.Ranky.infrastructure.utils.DiscordMessages.EXECUTE_COMMAND_FROM_SERVER;

import com.desierto.Ranky.domain.entity.Account;
import com.desierto.Ranky.domain.service.IAccountsService;
import com.desierto.Ranky.infrastructure.configuration.ConfigLoader;
import com.desierto.Ranky.infrastructure.utils.DiscordOptionRetriever;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class DiscordRemoveAccountsService {

  @Autowired
  private ConfigLoader config;

  @Autowired
  private DiscordOptionRetriever discordOptionRetriever;

  @Autowired
  private IAccountsService accountsService;

  public void execute(SlashCommandInteractionEvent event) {
    if (event.getMember().getRoles().stream()
        .anyMatch(role -> role.getName().equalsIgnoreCase(config.getRankyUserRole()))) {
      if (event.isFromGuild()) {
        String rankingId = discordOptionRetriever.fromEventGetObjectName(event);
        List<Account> accountsToRemove = discordOptionRetriever.fromEventGetAccountList(event)
            .stream()
            .filter(Account::isNotEmpty)
            .toList();
        if (!accountsToRemove.isEmpty()) {
          accountsService.removeAccounts(rankingId, event.getGuild(), accountsToRemove);
          event.getHook().sendMessage("Accounts removed successfully!").queue();
        }
      } else {
        event.getHook().sendMessage(EXECUTE_COMMAND_FROM_SERVER.getMessage()).queue();
      }
    } else {
      event.getHook().sendMessage(COMMAND_NOT_ALLOWED.getMessage()).queue();
    }
  }
}
