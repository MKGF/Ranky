package com.desierto.ranky.infrastructure.listeners;

import static com.desierto.ranky.infrastructure.utils.DiscordButtons.FINAL_PAGE;
import static com.desierto.ranky.infrastructure.utils.DiscordButtons.PAGE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.desierto.ranky.application.AccountsCache;
import com.desierto.ranky.domain.entity.Account;
import com.desierto.ranky.domain.valueobject.Rank;
import com.desierto.ranky.infrastructure.configuration.ConfigLoader;
import com.desierto.ranky.infrastructure.dto.EntryDto;
import com.desierto.ranky.infrastructure.service.PrintRankingService;
import com.desierto.ranky.infrastructure.utils.DiscordRankingFormatter;
import java.util.List;
import java.util.Optional;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RankyButtonClickListenerTest {

  @InjectMocks
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
  void mockFormatterAndConfig() {
    lenient().when(discordRankingFormatter.title(anyString())).thenReturn("");
    lenient().when(discordRankingFormatter.title(anyString(), anyString())).thenReturn("");
    lenient().when(discordRankingFormatter.footer()).thenReturn("");
    lenient().when(config.getAccountLimit()).thenReturn(1);
  }


  //Case we want to print a page
  @Test
  void onPageButtonPressedEvent_resendMessageToChannel() {
    ButtonInteractionEvent event = getPageButtonInteractionEvent();

    cut.onButtonInteraction(event);

    verify(event.getChannel(), times(1)).sendMessage("expected\n\n\nRequested by mention");
    verify(discordRankingFormatter, times(1)).footer();
  }

  //Case we want to print a final page
  @Test
  void onFinalPageButtonPressedEvent_resendMessageToChannel() {
    ButtonInteractionEvent event = getFinalPageButtonInteractionEvent();

    cut.onButtonInteraction(event);

    verify(event.getChannel(), times(1)).sendMessage("expected\n\nRequested by mention");
    verify(discordRankingFormatter, times(0)).footer();
  }

  //Case we want to print a whole single paged ranking
  @Test
  void onShareRankingButtonPressedEvent_printsSinglePage() {
    ButtonInteractionEvent event = getSpecificRankingButtonInteractionEvent();
    Account account = new Account("name", "tagLine");
    account.updateRank(Rank.unranked());
    when(accountsCache.find(anyString(), anyString())).thenReturn(Optional.of(List.of(account)));
    EntryDto entry = new EntryDto(1, "name", "<:Unranked:1248786000533262419>", "   ", 0, "0", "0",
        "0.00");

    cut.onButtonInteraction(event);

    verify(printRankingService, times(1)).printSinglePage(eq(event), eq("specificId"),
        eq(List.of(entry)), any());
  }

  //Case we want to print a whole several paged ranking
  @Test
  void onShareRankingButtonPressedEvent_printsMultiPage() {
    ButtonInteractionEvent event = getSpecificRankingButtonInteractionEvent();
    Account account1 = new Account("name1", "tagLine1");
    Account account2 = new Account("name2", "tagLine2");
    account1.updateRank(Rank.unranked());
    account2.updateRank(Rank.unranked());
    when(accountsCache.find(anyString(), anyString())).thenReturn(
        Optional.of(List.of(account1, account2)));
    EntryDto entry1 = new EntryDto(1, "name1", "<:Unranked:1248786000533262419>", "   ", 0, "0",
        "0",
        "0.00");
    EntryDto entry2 = new EntryDto(2, "name2", "<:Unranked:1248786000533262419>", "   ", 0, "0",
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
    User user = mock(User.class);
    when(event.getUser()).thenReturn(user);
    when(user.getAsMention()).thenReturn("mention");
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
    User user = mock(User.class);
    when(event.getUser()).thenReturn(user);
    when(user.getAsMention()).thenReturn("mention");
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
    User user = mock(User.class);
    when(guild.getId()).thenReturn("guildId");
    lenient().when(event.getUser()).thenReturn(user);
    lenient().when(user.getAsMention()).thenReturn("mention");
    lenient().when(event.getChannel()).thenReturn(channel);
    lenient().when(event.getMessage()).thenReturn(message);
    lenient().when(message.getContentRaw()).thenReturn(expected);
    lenient().when(channel.sendMessage(anyString())).thenReturn(mca);
    when(event.reply(anyString())).thenReturn(rca);
    when(rca.setEphemeral(anyBoolean())).thenReturn(rca);
    when(event.getButton()).thenReturn(button);
    when(button.getId()).thenReturn("specificId");
    when(event.getGuild()).thenReturn(guild);
    return event;
  }

}
