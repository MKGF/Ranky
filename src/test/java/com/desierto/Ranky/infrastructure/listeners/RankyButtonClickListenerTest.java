package com.desierto.Ranky.infrastructure.listeners;

import static com.desierto.Ranky.infrastructure.utils.DiscordButtons.FINAL_PAGE;
import static com.desierto.Ranky.infrastructure.utils.DiscordButtons.PAGE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.desierto.Ranky.application.AccountsCache;
import com.desierto.Ranky.domain.entity.Account;
import com.desierto.Ranky.domain.valueobject.Rank;
import com.desierto.Ranky.infrastructure.configuration.ConfigLoader;
import com.desierto.Ranky.infrastructure.dto.EntryDTO;
import com.desierto.Ranky.infrastructure.service.PrintRankingService;
import com.desierto.Ranky.infrastructure.utils.DiscordRankingFormatter;
import java.util.List;
import java.util.Optional;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
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


  @BeforeEach
  public void setUp() {
    cut = new RankyButtonClickListener(
        bot,
        accountsCache,
        config,
        discordRankingFormatter,
        printRankingService
    );
    when(discordRankingFormatter.title(anyString())).thenReturn("");
    when(discordRankingFormatter.title(anyString(), anyString())).thenReturn("");
    when(discordRankingFormatter.footer()).thenReturn("");
    when(config.getAccountLimit()).thenReturn(1);
  }


  //Case we want to print a page
  @Test
  public void onPageButtonPressedEvent_resendMessageToChannel() {
    ButtonInteractionEvent event = getPageButtonInteractionEvent();

    cut.onButtonInteraction(event);

    verify(event.getChannel(), times(1)).sendMessage("expected\n");
    verify(discordRankingFormatter, times(1)).footer();
  }

  //Case we want to print a final page
  @Test
  public void onFinalPageButtonPressedEvent_resendMessageToChannel() {
    ButtonInteractionEvent event = getFinalPageButtonInteractionEvent();

    cut.onButtonInteraction(event);

    verify(event.getChannel(), times(1)).sendMessage("expected");
    verify(discordRankingFormatter, times(0)).footer();
  }

  //Case we want to print a whole single paged ranking
  @Test
  public void onShareRankingButtonPressedEvent_printsSinglePage() {
    ButtonInteractionEvent event = getSpecificRankingButtonInteractionEvent();
    Account account = new Account("name", "tagLine");
    account.updateRank(Rank.unranked());
    when(accountsCache.find(anyString())).thenReturn(Optional.of(List.of(account)));
    EntryDTO entry = new EntryDTO(1, "name", "<:Unranked:1248786000533262419>", "   ", 0, "0", "0",
        "0.00");

    cut.onButtonInteraction(event);

    verify(printRankingService, times(1)).printSinglePage(eq(event), eq("specificId"),
        eq(List.of(entry)), any());
  }

  //Case we want to print a whole several paged ranking
  @Test
  public void onShareRankingButtonPressedEvent_printsMultiPage() {
    ButtonInteractionEvent event = getSpecificRankingButtonInteractionEvent();
    Account account1 = new Account("name1", "tagLine1");
    Account account2 = new Account("name2", "tagLine2");
    account1.updateRank(Rank.unranked());
    account2.updateRank(Rank.unranked());
    when(accountsCache.find(anyString())).thenReturn(Optional.of(List.of(account1, account2)));
    EntryDTO entry1 = new EntryDTO(1, "name1", "<:Unranked:1248786000533262419>", "   ", 0, "0",
        "0",
        "0.00");
    EntryDTO entry2 = new EntryDTO(2, "name2", "<:Unranked:1248786000533262419>", "   ", 0, "0",
        "0",
        "0.00");

    cut.onButtonInteraction(event);

    verify(printRankingService, times(1)).printMultiPage(eq(event), eq(""),
        eq(List.of(entry1, entry2)), any());
  }

  @NotNull
  private ButtonInteractionEvent getPageButtonInteractionEvent() {
    ButtonInteractionEvent event = Mockito.mock(ButtonInteractionEvent.class);
    Button button = mock(Button.class);
    MessageChannelUnion channel = mock(MessageChannelUnion.class);
    Message message = mock(Message.class);
    MessageCreateAction mca = mock(MessageCreateAction.class);
    ReplyCallbackAction rca = mock(ReplyCallbackAction.class);
    String expected = "expected";
    when(event.getChannel()).thenReturn(channel);
    when(event.getMessage()).thenReturn(message);
    when(message.getContentRaw()).thenReturn(expected);
    when(channel.sendMessage(anyString())).thenReturn(mca);
    when(event.reply(anyString())).thenReturn(rca);
    when(rca.setEphemeral(anyBoolean())).thenReturn(rca);
    when(event.getButton()).thenReturn(button);
    when(button.getId()).thenReturn(PAGE.getId());
    return event;
  }

  @NotNull
  private ButtonInteractionEvent getFinalPageButtonInteractionEvent() {
    ButtonInteractionEvent event = Mockito.mock(ButtonInteractionEvent.class);
    Button button = mock(Button.class);
    MessageChannelUnion channel = mock(MessageChannelUnion.class);
    Message message = mock(Message.class);
    MessageCreateAction mca = mock(MessageCreateAction.class);
    ReplyCallbackAction rca = mock(ReplyCallbackAction.class);
    String expected = "expected";
    when(event.getChannel()).thenReturn(channel);
    when(event.getMessage()).thenReturn(message);
    when(message.getContentRaw()).thenReturn(expected);
    when(channel.sendMessage(anyString())).thenReturn(mca);
    when(event.reply(anyString())).thenReturn(rca);
    when(rca.setEphemeral(anyBoolean())).thenReturn(rca);
    when(event.getButton()).thenReturn(button);
    when(button.getId()).thenReturn(FINAL_PAGE.getId());
    return event;
  }

  @NotNull
  private ButtonInteractionEvent getSpecificRankingButtonInteractionEvent() {
    ButtonInteractionEvent event = Mockito.mock(ButtonInteractionEvent.class);
    Button button = mock(Button.class);
    MessageChannelUnion channel = mock(MessageChannelUnion.class);
    Message message = mock(Message.class);
    MessageCreateAction mca = mock(MessageCreateAction.class);
    ReplyCallbackAction rca = mock(ReplyCallbackAction.class);
    Guild guild = mock(Guild.class);
    String expected = "expected";
    when(event.getChannel()).thenReturn(channel);
    when(event.getMessage()).thenReturn(message);
    when(message.getContentRaw()).thenReturn(expected);
    when(channel.sendMessage(anyString())).thenReturn(mca);
    when(event.reply(anyString())).thenReturn(rca);
    when(rca.setEphemeral(anyBoolean())).thenReturn(rca);
    when(event.getButton()).thenReturn(button);
    when(button.getId()).thenReturn("specificId");
    when(event.getGuild()).thenReturn(guild);
    return event;
  }

}
