package com.desierto.Ranky.infrastructure.service;

import static com.desierto.Ranky.infrastructure.utils.DiscordExceptionHandler.handleExceptionOnSlashCommandEvent;
import static com.desierto.Ranky.infrastructure.utils.DiscordMessages.EXECUTE_COMMAND_FROM_SERVER;

import com.desierto.Ranky.domain.exception.ConfigChannelNotFoundException;
import com.desierto.Ranky.domain.exception.LastCommandNotFoundException;
import com.desierto.Ranky.domain.exception.ranking.RankingNotFoundException;
import com.desierto.Ranky.infrastructure.configuration.ConfigLoader;
import com.desierto.Ranky.infrastructure.dto.LastCommandDTO;
import com.desierto.Ranky.infrastructure.repository.CommandsChannelLastCommandRepository;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RepeatCommandService {

  @Autowired
  private Gson gson;

  @Autowired
  private ConfigLoader config;

  public void execute(SlashCommandInteractionEvent event) {
    //dado un evento, necesito saber quien lo ha lanzado y si tiene un ultimo comando, ejecutarlo
    if (event.isFromGuild()) {
      try {
        CommandsChannelLastCommandRepository commandRepository = new CommandsChannelLastCommandRepository(
            config,
            event.getGuild(),
            gson
        );
        try {
          LastCommandDTO lastCommandDTO = commandRepository.find(event.getUser().getId());
          
        } catch (LastCommandNotFoundException e) {
          handleExceptionOnSlashCommandEvent(e, event);
        }
      } catch (ConfigChannelNotFoundException | RankingNotFoundException e) {
        handleExceptionOnSlashCommandEvent(e, event);
      }
    } else {
      event.getHook().sendMessage(EXECUTE_COMMAND_FROM_SERVER.getMessage()).queue();
    }
  }
}
