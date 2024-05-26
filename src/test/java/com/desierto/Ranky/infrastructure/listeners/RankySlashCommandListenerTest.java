package com.desierto.Ranky.infrastructure.listeners;

import static com.desierto.Ranky.infrastructure.commands.Command.ADD_ACCOUNTS;
import static com.desierto.Ranky.infrastructure.commands.Command.CREATE;
import static com.desierto.Ranky.infrastructure.commands.Command.DELETE;
import static com.desierto.Ranky.infrastructure.commands.Command.HELP_RANKY;
import static com.desierto.Ranky.infrastructure.commands.Command.RANKING;
import static com.desierto.Ranky.infrastructure.commands.Command.REMOVE_ACCOUNTS;
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
import net.dv8tion.jda.api.interactions.InteractionHook;
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
    InteractionHook hook = Mockito.mock(InteractionHook.class);
    when(event.deferReply(anyBoolean())).thenReturn(replyCallbackAction);
    doNothing().when(replyCallbackAction).queue();
    when(event.getHook()).thenReturn(hook);
    return event;
  }

  @Test
  public void onHelpRankyCommand_callsHelpRankyService() {
    SlashCommandInteractionEvent event = getSlashCommandInteractionEvent();
    when(event.getCommandString()).thenReturn("/" + HELP_RANKY.getCommandId());

    cut.onSlashCommandInteraction(event);

    verify(helpRankyService, times(1)).execute(event.getHook());
  }

  @Test
  public void onRankingCommand_callsGetRankingService() {
    SlashCommandInteractionEvent event = getSlashCommandInteractionEvent();
    when(event.getCommandString()).thenReturn("/" + RANKING.getCommandId());

    cut.onSlashCommandInteraction(event);

    verify(getRankingService, times(1)).execute(event.getHook());
  }

  @Test
  public void onCreateCommand_callsCreateRankingService() {
    SlashCommandInteractionEvent event = getSlashCommandInteractionEvent();
    when(event.getCommandString()).thenReturn("/" + CREATE.getCommandId());

    cut.onSlashCommandInteraction(event);

    verify(createRankingService, times(1)).execute(event.getHook());
  }

  @Test
  public void onDeleteCommand_callsDeleteRankingService() {
    SlashCommandInteractionEvent event = getSlashCommandInteractionEvent();
    when(event.getCommandString()).thenReturn("/" + DELETE.getCommandId());

    cut.onSlashCommandInteraction(event);

    verify(deleteRankingService, times(1)).execute(event.getHook());
  }

  @Test
  public void onAddAccountsCommand_callsAddAccountsService() {
    SlashCommandInteractionEvent event = getSlashCommandInteractionEvent();
    when(event.getCommandString()).thenReturn("/" + ADD_ACCOUNTS.getCommandId());

    cut.onSlashCommandInteraction(event);

    verify(addAccountsService, times(1)).execute(event.getHook());
  }

  @Test
  public void onRemoveAccountsCommand_callsRemoveAccountsService() {
    SlashCommandInteractionEvent event = getSlashCommandInteractionEvent();
    when(event.getCommandString()).thenReturn("/" + REMOVE_ACCOUNTS.getCommandId());

    cut.onSlashCommandInteraction(event);

    verify(removeAccountsService, times(1)).execute(event.getHook());
  }
}
