package com.desierto.Ranky.infrastructure.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.desierto.Ranky.domain.entity.Account;
import java.util.List;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class DiscordOptionRetrieverTest {

  private DiscordOptionRetriever cut;

  @BeforeAll
  public void setUp() {
    cut = new DiscordOptionRetriever();
  }

  @Test
  public void onEvent_getRankingName() {
    SlashCommandInteractionEvent event = getMockedEventWithRankingName();
    assertEquals("A ranking", cut.fromEventGetRankingName(event));
  }

  @Test
  public void onEvent_getsAccountList() {
    SlashCommandInteractionEvent event = getMockedEventWithRankingNameAndAccounts();
    assertEquals(List.of(new Account("testAcc", "EUW"), new Account("testAcc2", "EUW")),
        cut.fromEventGetAccountList(event));
  }

  @Test
  public void onEventWithMalformedAccounts_getsEmptyAccountList() {
    SlashCommandInteractionEvent event = getMockedEventWithRankingNameAndMalformedAccounts();
    InteractionHook hook = mock(InteractionHook.class);
    when(event.getHook()).thenReturn(hook);
    WebhookMessageCreateAction wmca = mock(WebhookMessageCreateAction.class);
    when(hook.sendMessage(anyString())).thenReturn(wmca);
    assertEquals(List.of(new Account(), new Account()),
        cut.fromEventGetAccountList(event));
    verify(wmca, times(2)).queue();
  }

  private SlashCommandInteractionEvent getMockedEventWithRankingName() {
    SlashCommandInteractionEvent event = mock(SlashCommandInteractionEvent.class);
    OptionMapping option = mock(OptionMapping.class);
    when(event.getOptions()).thenReturn(List.of(option));
    when(option.getAsString()).thenReturn("A ranking");
    return event;
  }

  private SlashCommandInteractionEvent getMockedEventWithRankingNameAndAccounts() {
    SlashCommandInteractionEvent event = mock(SlashCommandInteractionEvent.class);
    OptionMapping rankingNameOption = mock(OptionMapping.class);
    OptionMapping accountsOption = mock(OptionMapping.class);
    when(event.getOptions()).thenReturn(List.of(rankingNameOption, accountsOption));
    when(rankingNameOption.getAsString()).thenReturn("A ranking");
    when(accountsOption.getAsString()).thenReturn("testAcc#EUW,testAcc2#EUW");
    return event;
  }

  private SlashCommandInteractionEvent getMockedEventWithRankingNameAndMalformedAccounts() {
    SlashCommandInteractionEvent event = mock(SlashCommandInteractionEvent.class);
    OptionMapping rankingNameOption = mock(OptionMapping.class);
    OptionMapping accountsOption = mock(OptionMapping.class);
    when(event.getOptions()).thenReturn(List.of(rankingNameOption, accountsOption));
    when(rankingNameOption.getAsString()).thenReturn("A ranking");
    when(accountsOption.getAsString()).thenReturn("testAcc,testAcc2");
    return event;
  }

}
