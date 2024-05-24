package com.desierto.Ranky.infrastructure.listeners;

import static com.desierto.Ranky.infrastructure.commands.Command.HELP_RANKY;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.desierto.Ranky.infrastructure.service.AddAccountsService;
import com.desierto.Ranky.infrastructure.service.CreateRankingService;
import com.desierto.Ranky.infrastructure.service.DeleteRankingService;
import com.desierto.Ranky.infrastructure.service.GetRankingService;
import com.desierto.Ranky.infrastructure.service.HelpRankyService;
import com.desierto.Ranky.infrastructure.service.RemoveAccountsService;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class RankySlashCommandListenerTest {

  RankySlashCommandListener cut;

  @Mock
  JDA bot;

  @Mock
  HelpRankyService helpRankyService;

  @Mock
  GetRankingService getRankingService;

  @Mock
  CreateRankingService createRankingService;

  @Mock
  DeleteRankingService deleteRankingService;

  @Mock
  AddAccountsService addAccountsService;

  @Mock
  RemoveAccountsService removeAccountsService;


  @BeforeAll
  public void setUp() {

    cut = new RankySlashCommandListener(helpRankyService,
        getRankingService,
        createRankingService,
        deleteRankingService,
        addAccountsService,
        removeAccountsService,
        bot);
  }

  @NotNull
  private SlashCommandInteractionEvent getSlashCommandInteractionEvent() {
    SlashCommandInteractionEvent event = Mockito.mock(SlashCommandInteractionEvent.class);
    ReplyCallbackAction replyCallbackAction = Mockito.mock(ReplyCallbackAction.class);
    when(event.deferReply(anyBoolean())).thenReturn(replyCallbackAction);
    doNothing().when(replyCallbackAction).queue();
    return event;
  }

  @Test
  public void onHelpRankyCommand_callsHelpRankyService() {
    SlashCommandInteractionEvent event = getSlashCommandInteractionEvent();
    when(event.getCommandString()).thenReturn("/" + HELP_RANKY.getCommandId());

    cut.onSlashCommandInteraction(event);

    verify(helpRankyService, times(1)).execute(event);
  }
}
