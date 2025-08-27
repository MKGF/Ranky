package com.desierto.ranky.infrastructure.listeners;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.desierto.ranky.application.fixtures.RankingFixtures;
import com.desierto.ranky.domain.entity.Ranking;
import com.desierto.ranky.infrastructure.repository.DiscordRankingRepository;
import java.util.List;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.AutoCompleteQuery;
import net.dv8tion.jda.api.interactions.commands.Command.Choice;
import net.dv8tion.jda.api.requests.restaction.interactions.AutoCompleteCallbackAction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RankyAutoCompleteListenerTest {

  @Mock
  JDA jda;

  @Mock
  DiscordRankingRepository rankingRepository;

  @InjectMocks
  RankyAutoCompleteListener cut;

  @Test
  void forEvent_retrievesOptions() {
    List<Ranking> rankings = List.of(RankingFixtures.aRanking(), RankingFixtures.anotherRanking());
    Guild guild = mock(Guild.class);
    CommandAutoCompleteInteractionEvent event = mock(CommandAutoCompleteInteractionEvent.class);
    when(event.getGuild()).thenReturn(guild);
    AutoCompleteQuery acq = mock(AutoCompleteQuery.class);
    AutoCompleteCallbackAction acca = mock(AutoCompleteCallbackAction.class);
    when(acq.getValue()).thenReturn("r");
    when(event.getFocusedOption()).thenReturn(acq);
    when(rankingRepository.findAll(guild)).thenReturn(rankings);
    when(event.replyChoices(anyList())).thenReturn(acca);
    cut.onCommandAutoCompleteInteraction(event);
    verify(event.replyChoices(List.of(new Choice("rankingId", "rankingId"))), times(1)).queue();
  }
}