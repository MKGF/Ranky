package com.desierto.ranky.infrastructure.service;

import static com.desierto.ranky.infrastructure.utils.DiscordMessages.COMMAND_NOT_ALLOWED;
import static com.desierto.ranky.infrastructure.utils.DiscordMessages.EXECUTE_COMMAND_FROM_SERVER;

import com.desierto.ranky.domain.entity.Account;
import com.desierto.ranky.domain.service.IAccountsService;
import com.desierto.ranky.infrastructure.configuration.ConfigLoader;
import com.desierto.ranky.infrastructure.utils.DiscordOptionRetriever;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class DiscordAddAccountsService {

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
        String rankingName = discordOptionRetriever.fromEventGetObjectName(event);
        List<Account> accountsToAdd = discordOptionRetriever.fromEventGetAccountList(event)
            .stream()
            .filter(Account::isNotEmpty)
            .toList();
        if (!accountsToAdd.isEmpty()) {
          accountsService
              .addAccounts(rankingName, event.getGuild(), accountsToAdd);
          event.getHook().sendMessage("Accounts added successfully!").queue();
        }
      } else {
        event.getHook().sendMessage(EXECUTE_COMMAND_FROM_SERVER.getMessage()).queue();
      }
    } else {
      event.getHook().sendMessage(COMMAND_NOT_ALLOWED.getMessage()).queue();
    }
  }
}
