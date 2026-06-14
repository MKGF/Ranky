package com.desierto.ranky.infrastructure.listeners;

import static com.desierto.ranky.infrastructure.commands.Command.ADD_ACCOUNTS;
import static com.desierto.ranky.infrastructure.commands.Command.CREATE;
import static com.desierto.ranky.infrastructure.commands.Command.DELETE;
import static com.desierto.ranky.infrastructure.commands.Command.EXISTS_CONFIG_CHANNEL;
import static com.desierto.ranky.infrastructure.commands.Command.GET_ENROLLED_USERS;
import static com.desierto.ranky.infrastructure.commands.Command.GET_GUILDS;
import static com.desierto.ranky.infrastructure.commands.Command.HELP;
import static com.desierto.ranky.infrastructure.commands.Command.MAKE_PUBLIC;
import static com.desierto.ranky.infrastructure.commands.Command.RANKING;
import static com.desierto.ranky.infrastructure.commands.Command.REMOVE_ACCOUNTS;
import static com.desierto.ranky.infrastructure.commands.Command.RETRIEVE_CONFIG_CHANNEL_CONTENT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.desierto.ranky.infrastructure.service.DiscordAddAccountsService;
import com.desierto.ranky.infrastructure.service.DiscordCreateRankingService;
import com.desierto.ranky.infrastructure.service.DiscordDeleteRankingService;
import com.desierto.ranky.infrastructure.service.DiscordGetRankingService;
import com.desierto.ranky.infrastructure.service.DiscordRankingPublisherService;
import com.desierto.ranky.infrastructure.service.DiscordRemoveAccountsService;
import com.desierto.ranky.infrastructure.service.HelpService;
import com.desierto.ranky.infrastructure.service.admin.ConfigChannelChecker;
import com.desierto.ranky.infrastructure.service.admin.ConfigChannelContentRetriever;
import com.desierto.ranky.infrastructure.service.admin.EnrolledUsersRetriever;
import com.desierto.ranky.infrastructure.service.admin.GuildRetriever;
import java.util.concurrent.ExecutorService;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RankySlashCommandListenerTest {

  RankySlashCommandListener cut;

  @Mock
  JDA bot;

  @Mock
  HelpService helpService;

  @Mock
  DiscordGetRankingService discordGetRankingService;

  @Mock
  DiscordCreateRankingService discordCreateRankingService;

  @Mock
  DiscordDeleteRankingService discordDeleteRankingService;

  @Mock
  DiscordAddAccountsService discordAddAccountsService;

  @Mock
  DiscordRemoveAccountsService discordRemoveAccountsService;

  @Mock
  GuildRetriever guildRetriever;

  @Mock
  EnrolledUsersRetriever enrolledUsersRetriever;

  @Mock
  ConfigChannelChecker configChannelChecker;

  @Mock
  ConfigChannelContentRetriever configChannelContentRetriever;

  @Mock
  DiscordRankingPublisherService discordRankingPublisherService;

  @Mock
  ExecutorService executorService;


  @BeforeEach
  void setUp() {

    cut = new RankySlashCommandListener(helpService,
        discordGetRankingService,
        discordCreateRankingService,
        discordDeleteRankingService,
        discordAddAccountsService,
        discordRemoveAccountsService,
        guildRetriever,
        enrolledUsersRetriever,
        configChannelChecker,
        configChannelContentRetriever,
        discordRankingPublisherService,
        executorService,
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
  void onHelpCommand_opensThread() {
    SlashCommandInteractionEvent event = getSlashCommandInteractionEvent();
    when(event.getCommandString()).thenReturn("/" + HELP.getCommandId());

    cut.onSlashCommandInteraction(event);

    verify(executorService, times(1)).execute(any());
  }

  @Test
  void onRankingCommand_opensThread() {
    SlashCommandInteractionEvent event = getSlashCommandInteractionEvent();
    when(event.getCommandString()).thenReturn("/" + RANKING.getCommandId());

    cut.onSlashCommandInteraction(event);

    verify(executorService, times(1)).execute(any());
  }

  @Test
  void onCreateCommand_opensThread() {
    SlashCommandInteractionEvent event = getSlashCommandInteractionEvent();
    when(event.getCommandString()).thenReturn("/" + CREATE.getCommandId());

    cut.onSlashCommandInteraction(event);

    verify(executorService, times(1)).execute(any());
  }

  @Test
  void onDeleteCommand_opensThread() {
    SlashCommandInteractionEvent event = getSlashCommandInteractionEvent();
    when(event.getCommandString()).thenReturn("/" + DELETE.getCommandId());

    cut.onSlashCommandInteraction(event);

    verify(executorService, times(1)).execute(any());
  }

  @Test
  void onAddAccountsCommand_opensThread() {
    SlashCommandInteractionEvent event = getSlashCommandInteractionEvent();
    when(event.getCommandString()).thenReturn("/" + ADD_ACCOUNTS.getCommandId());

    cut.onSlashCommandInteraction(event);

    verify(executorService, times(1)).execute(any());
  }

  @Test
  void onRemoveAccountsCommand_opensThread() {
    SlashCommandInteractionEvent event = getSlashCommandInteractionEvent();
    when(event.getCommandString()).thenReturn("/" + REMOVE_ACCOUNTS.getCommandId());

    cut.onSlashCommandInteraction(event);

    verify(executorService, times(1)).execute(any());
  }

  @Test
  void onGetGuildsCommand_opensThread() {
    SlashCommandInteractionEvent event = getSlashCommandInteractionEvent();
    when(event.getCommandString()).thenReturn("/" + GET_GUILDS.getCommandId());

    cut.onSlashCommandInteraction(event);

    verify(executorService, times(1)).execute(any());
  }

  @Test
  void onGetEnrolledUsersCommand_opensThread() {
    SlashCommandInteractionEvent event = getSlashCommandInteractionEvent();
    when(event.getCommandString()).thenReturn("/" + GET_ENROLLED_USERS.getCommandId());

    cut.onSlashCommandInteraction(event);

    verify(executorService, times(1)).execute(any());
  }

  @Test
  void onExistsConfigChannelCommand_opensThread() {
    SlashCommandInteractionEvent event = getSlashCommandInteractionEvent();
    when(event.getCommandString()).thenReturn("/" + EXISTS_CONFIG_CHANNEL.getCommandId());

    cut.onSlashCommandInteraction(event);

    verify(executorService, times(1)).execute(any());
  }

  @Test
  void onRetrieveConfigChannelContentCommand_opensThread() {
    SlashCommandInteractionEvent event = getSlashCommandInteractionEvent();
    when(event.getCommandString()).thenReturn("/" + RETRIEVE_CONFIG_CHANNEL_CONTENT.getCommandId());

    cut.onSlashCommandInteraction(event);

    verify(executorService, times(1)).execute(any());
  }

  @Test
  void onMakePublicCommand_opensThread() {
    SlashCommandInteractionEvent event = getSlashCommandInteractionEvent();
    when(event.getCommandString()).thenReturn("/" + MAKE_PUBLIC.getCommandId());

    cut.onSlashCommandInteraction(event);

    verify(executorService, times(1)).execute(any());
  }

  @Test
  void onUnknownCommand_opensNoThreads() {
    SlashCommandInteractionEvent event = getSlashCommandInteractionEvent();
    when(event.getCommandString()).thenReturn("Unknown command");

    cut.onSlashCommandInteraction(event);

    verify(executorService, times(0)).execute(any());
  }
}
