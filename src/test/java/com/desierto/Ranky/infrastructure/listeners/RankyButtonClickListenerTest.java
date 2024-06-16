package com.desierto.Ranky.infrastructure.listeners;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.desierto.Ranky.application.AccountsCache;
import com.desierto.Ranky.infrastructure.configuration.ConfigLoader;
import com.desierto.Ranky.infrastructure.service.PrintRankingService;
import com.desierto.Ranky.infrastructure.utils.DiscordRankingFormatter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class RankyButtonClickListenerTest {

  RankyButtonClickListener cut;

  @Mock
  JDA bot;

  @Mock
  AccountsCache accountsCache;

  @Mock
  ConfigLoader config;

  @Mock
  DiscordRankingFormatter discordRankingFormatter;

  @Mock
  PrintRankingService printRankingService;


  @BeforeAll
  public void setUp() {

    cut = new RankyButtonClickListener(
        bot,
        accountsCache,
        config,
        discordRankingFormatter,
        printRankingService
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

  //Case we want to print a page

  //Case we want to print a final page

  //Case we want to print a whole single paged ranking

  //Case we want to print a whole several paged ranking

}
