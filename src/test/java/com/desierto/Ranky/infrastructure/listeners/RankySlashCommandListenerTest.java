package com.desierto.Ranky.infrastructure.listeners;

import static com.desierto.Ranky.infrastructure.commands.Command.ADD_ACCOUNTS;
import static com.desierto.Ranky.infrastructure.commands.Command.CREATE;
import static com.desierto.Ranky.infrastructure.commands.Command.DELETE;
import static com.desierto.Ranky.infrastructure.commands.Command.EXISTS_CONFIG_CHANNEL;
import static com.desierto.Ranky.infrastructure.commands.Command.GET_ENROLLED_USERS;
import static com.desierto.Ranky.infrastructure.commands.Command.GET_GUILDS;
import static com.desierto.Ranky.infrastructure.commands.Command.HELP;
import static com.desierto.Ranky.infrastructure.commands.Command.RANKING;
import static com.desierto.Ranky.infrastructure.commands.Command.REMOVE_ACCOUNTS;
import static com.desierto.Ranky.infrastructure.commands.Command.RETRIEVE_CONFIG_CHANNEL_CONTENT;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.desierto.Ranky.infrastructure.service.AddAccountsService;
import com.desierto.Ranky.infrastructure.service.CreateRankingService;
import com.desierto.Ranky.infrastructure.service.DeleteRankingService;
import com.desierto.Ranky.infrastructure.service.GetRankingService;
import com.desierto.Ranky.infrastructure.service.HelpService;
import com.desierto.Ranky.infrastructure.service.RemoveAccountsService;
import com.desierto.Ranky.infrastructure.service.admin.ConfigChannelChecker;
import com.desierto.Ranky.infrastructure.service.admin.ConfigChannelContentRetriever;
import com.desierto.Ranky.infrastructure.service.admin.EnrolledUsersRetriever;
import com.desierto.Ranky.infrastructure.service.admin.GuildRetriever;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
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
  HelpService helpService;

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

  @Mock
  GuildRetriever guildRetriever;

  @Mock
  EnrolledUsersRetriever enrolledUsersRetriever;

  @Mock
  ConfigChannelChecker configChannelChecker;

  @Mock
  ConfigChannelContentRetriever configChannelContentRetriever;


  @BeforeAll
  public void setUp() {

    cut = new RankySlashCommandListener(helpService,
        getRankingService,
        createRankingService,
        deleteRankingService,
        addAccountsService,
        removeAccountsService,
        guildRetriever,
        enrolledUsersRetriever,
        configChannelChecker,
        configChannelContentRetriever,
        bot
    );
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
  @Disabled(value = "Multithreading broke the test")
  public void onHelpCommand_callsHelpService() {
    SlashCommandInteractionEvent event = getSlashCommandInteractionEvent();
    when(event.getCommandString()).thenReturn("/" + HELP.getCommandId());

    cut.onSlashCommandInteraction(event);

    verify(helpService, times(1)).execute(event);
  }

  @Test
  @Disabled(value = "Multithreading broke the test")
  public void onRankingCommand_callsGetRankingService() {
    SlashCommandInteractionEvent event = getSlashCommandInteractionEvent();
    when(event.getCommandString()).thenReturn("/" + RANKING.getCommandId());

    cut.onSlashCommandInteraction(event);

    verify(getRankingService, times(1)).execute(event);
  }

  @Test
  @Disabled(value = "Multithreading broke the test")
  public void onCreateCommand_callsCreateRankingService() {
    SlashCommandInteractionEvent event = getSlashCommandInteractionEvent();
    when(event.getCommandString()).thenReturn("/" + CREATE.getCommandId());

    cut.onSlashCommandInteraction(event);

    verify(createRankingService, times(1)).execute(event);
  }

  @Test
  @Disabled(value = "Multithreading broke the test")
  public void onDeleteCommand_callsDeleteRankingService() {
    SlashCommandInteractionEvent event = getSlashCommandInteractionEvent();
    when(event.getCommandString()).thenReturn("/" + DELETE.getCommandId());

    cut.onSlashCommandInteraction(event);

    verify(deleteRankingService, times(1)).execute(event);
  }

  @Test
  @Disabled(value = "Multithreading broke the test")
  public void onAddAccountsCommand_callsAddAccountsService() {
    SlashCommandInteractionEvent event = getSlashCommandInteractionEvent();
    when(event.getCommandString()).thenReturn("/" + ADD_ACCOUNTS.getCommandId());

    cut.onSlashCommandInteraction(event);

    verify(addAccountsService, times(1)).execute(event);
  }

  @Test
  @Disabled(value = "Multithreading broke the test")
  public void onRemoveAccountsCommand_callsRemoveAccountsService() {
    SlashCommandInteractionEvent event = getSlashCommandInteractionEvent();
    when(event.getCommandString()).thenReturn("/" + REMOVE_ACCOUNTS.getCommandId());

    cut.onSlashCommandInteraction(event);

    verify(removeAccountsService, times(1)).execute(event);
  }

  @Test
  @Disabled(value = "Multithreading broke the test")
  public void onGetGuildsCommand_callsGuildRetriever() {
    SlashCommandInteractionEvent event = getSlashCommandInteractionEvent();
    when(event.getCommandString()).thenReturn("/" + GET_GUILDS.getCommandId());

    cut.onSlashCommandInteraction(event);

    verify(guildRetriever, times(1)).execute(event);
  }

  @Test
  @Disabled(value = "Multithreading broke the test")
  public void onGetEnrolledUsersCommand_callsEnrolledAccountRetriever() {
    SlashCommandInteractionEvent event = getSlashCommandInteractionEvent();
    when(event.getCommandString()).thenReturn("/" + GET_ENROLLED_USERS.getCommandId());

    cut.onSlashCommandInteraction(event);

    verify(enrolledUsersRetriever, times(1)).execute(event);
  }

  @Test
  @Disabled(value = "Multithreading broke the test")
  public void onExistsConfigChannelCommand_callsConfigChannelChecker() {
    SlashCommandInteractionEvent event = getSlashCommandInteractionEvent();
    when(event.getCommandString()).thenReturn("/" + EXISTS_CONFIG_CHANNEL.getCommandId());

    cut.onSlashCommandInteraction(event);

    verify(configChannelChecker, times(1)).execute(event);
  }

  @Test
  @Disabled(value = "Multithreading broke the test")
  public void onRetrieveConfigChannelContentCommand_callsConfigChannelContentRetriever() {
    SlashCommandInteractionEvent event = getSlashCommandInteractionEvent();
    when(event.getCommandString()).thenReturn("/" + RETRIEVE_CONFIG_CHANNEL_CONTENT.getCommandId());

    cut.onSlashCommandInteraction(event);

    verify(configChannelContentRetriever, times(1)).execute(event);
  }
}
