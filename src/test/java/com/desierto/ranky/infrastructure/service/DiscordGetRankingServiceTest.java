package com.desierto.ranky.infrastructure.service;

import static com.desierto.ranky.infrastructure.utils.DiscordMessages.EXECUTE_COMMAND_FROM_SERVER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.desierto.ranky.application.AccountsCache;
import com.desierto.ranky.application.fixtures.RankingFixtures;
import com.desierto.ranky.domain.entity.Account;
import com.desierto.ranky.domain.entity.Ranking;
import com.desierto.ranky.domain.repository.RankingRepository;
import com.desierto.ranky.domain.repository.RiotAccountRepository;
import com.desierto.ranky.domain.valueobject.Rank;
import com.desierto.ranky.domain.valueobject.RankedMode;
import com.desierto.ranky.infrastructure.configuration.ConfigLoader;
import com.desierto.ranky.infrastructure.utils.DiscordOptionRetriever;
import com.desierto.ranky.infrastructure.utils.DiscordRankingFormatter;
import java.util.Optional;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.restaction.MessageEditAction;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DiscordGetRankingServiceTest {

  @InjectMocks
  DiscordGetRankingService cut;

  @Mock
  ConfigLoader config;

  @Mock
  DiscordOptionRetriever discordOptionRetriever;

  @Mock
  RiotAccountRepository riotAccountRepository;

  @Mock
  DiscordRankingFormatter discordRankingFormatter;

  @Mock
  AccountsCache accountsCache;

  @Mock
  PrintRankingService printRankingService;

  @Mock
  RankingRepository rankingRepository;

  @BeforeEach
  void setAccountLimitUp() {
    lenient().when(config.getAccountLimit()).thenReturn(1);
  }

  @Test
  void onEvent_whenNotFromGuild_doesNothingAndInforms() {
    SlashCommandInteractionEvent event = getAMockedEventNotFromAGuild();
    Ranking ranking = new Ranking("");
    mockDiscordRepo(ranking, event.getGuild());
    cut.execute(event, false);

    verify(event.getHook(), times(1)).sendMessage(EXECUTE_COMMAND_FROM_SERVER.getMessage());
  }

  @Test
  void onEvent_withSinglePageRanking_printsSinglePage() {
    SlashCommandInteractionEvent event = getAMockedEvent();
    Ranking ranking = new Ranking("id");
    mockDiscordRepo(ranking, event.getGuild());
    when(discordOptionRetriever.fromEventGetObjectName(event)).thenReturn("id");

    cut.execute(event, false);

    verify(printRankingService, times(1)).printSinglePage(eq(event), eq(ranking.getId()), anyList(),
        any());
  }

  @Test
  void onEvent_withMultiPageRanking_printsMultiPage() {
    SlashCommandInteractionEvent event = getAMockedEvent();
    Ranking ranking = new Ranking("id");
    Account acc1 = new Account("id1", "name1", "tagLine1");
    acc1.updateRank(Rank.unranked());
    Account acc2 = new Account("id2", "name2", "tagLine2");
    acc2.updateRank(Rank.unranked());
    ranking.addAccount(acc1);
    ranking.addAccount(acc2);
    mockDiscordRepo(ranking, event.getGuild());
    when(accountsCache.find(anyString(), anyString())).thenReturn(
        Optional.of(ranking.getAccounts()));
    when(discordOptionRetriever.fromEventGetObjectName(event)).thenReturn("id");
    lenient().when(discordRankingFormatter.formatRankingEntries(any()))
        .thenReturn("formattedRanking");

    cut.execute(event, false);

    verify(printRankingService, times(1)).printMultiPage(eq(event), eq(ranking.getId()), anyList(),
        any());
  }

  @Test
  void onEvent_withForceRefresh_skipsCacheCall() {
    SlashCommandInteractionEvent event = getAMockedEvent();
    Ranking ranking = RankingFixtures.aRanking();
    mockDiscordRepo(ranking, event.getGuild());
    when(rankingRepository.read(anyString(), any())).thenReturn(ranking);
    when(discordOptionRetriever.fromEventGetObjectName(event)).thenReturn(ranking.getId());
    when(accountsCache.find(any(), anyString())).thenReturn(
        Optional.of(RankingFixtures.aRanking().getAccounts()));

    cut.execute(event, true);

    verify(riotAccountRepository, times(ranking.getAccounts().size()))
        .enrichAccountsWithRankedStats(anyList(), eq(RankedMode.RANKED_SOLO_5x5));
  }

  private SlashCommandInteractionEvent getAMockedEventNotFromAGuild() {
    SlashCommandInteractionEvent event = mock(SlashCommandInteractionEvent.class);
    InteractionHook hook = mock(InteractionHook.class);
    WebhookMessageCreateAction wmca = mock(WebhookMessageCreateAction.class);
    when(event.isFromGuild()).thenReturn(false);
    when(event.getHook()).thenReturn(hook);
    when(hook.sendMessage(anyString())).thenReturn(wmca);
    return event;
  }

  private SlashCommandInteractionEvent getAMockedEvent() {
    SlashCommandInteractionEvent event = mock(SlashCommandInteractionEvent.class);
    InteractionHook hook = mock(InteractionHook.class);
    Interaction interaction = mock(Interaction.class);
    WebhookMessageCreateAction wmca = mock(WebhookMessageCreateAction.class);
    Guild guild = mock(Guild.class);
    Message message = mock(Message.class);
    MessageEditAction mea = mock(MessageEditAction.class);
    MessageCreateData mcd = mock(MessageCreateData.class);
    lenient().when(hook.getInteraction()).thenReturn(interaction);
    when(event.isFromGuild()).thenReturn(true);
    when(event.getGuild()).thenReturn(guild);
    lenient().when(interaction.getGuild()).thenReturn(guild);
    when(guild.getId()).thenReturn("guildId");
    lenient().when(event.getHook()).thenReturn(hook);
    lenient().when(hook.sendMessage(anyString())).thenReturn(wmca);
    lenient().when(hook.sendMessage(eq(mcd))).thenReturn(wmca);
    lenient().when(wmca.complete()).thenReturn(message);
    lenient().when(message.editMessage(anyString())).thenReturn(mea);
    lenient().when(mea.complete()).thenReturn(message);
    return event;
  }

  private void mockDiscordRepo(Ranking ranking, Guild guild) {
    lenient().when(rankingRepository.update(ranking, guild)).thenReturn(ranking);
    lenient().when(rankingRepository.read(ranking.getId(), guild)).thenReturn(ranking);
  }
}
