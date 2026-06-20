package com.desierto.ranky.application.service;

import static com.desierto.ranky.domain.valueobject.RankedMode.RANKED_SOLO_5x5;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.desierto.ranky.application.AccountsCache;
import com.desierto.ranky.domain.entity.Account;
import com.desierto.ranky.domain.entity.Ranking;
import com.desierto.ranky.domain.repository.RankingRepository;
import com.desierto.ranky.domain.repository.RiotAccountRepository;
import com.desierto.ranky.domain.valueobject.RankedMode;
import java.util.List;
import java.util.Optional;
import net.dv8tion.jda.api.entities.Guild;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetRankingServiceTest {

  @Mock
  RankingRepository rankingRepository;

  @Mock
  RiotAccountRepository riotAccountRepository;

  @Mock
  AccountsCache accountsCache;

  @InjectMocks
  RankingsService cut;

  @Test
  void retrievesFromCache() {
    String rankingId = "ranking";
    Guild guild = mock(Guild.class);
    Account account = Account.builder().build();
    when(guild.getId()).thenReturn("guildId");
    Ranking ranking = new Ranking(rankingId);
    when(rankingRepository.read(rankingId, guild)).thenReturn(ranking);
    when(accountsCache.find(guild.getId(), rankingId, RANKED_SOLO_5x5)).thenReturn(
        Optional.of(List.of(account)));
    assertEquals(cut.soloQ(rankingId, guild).getAccounts().get(0), account);
    verify(accountsCache, never()).save(anyString(), anyString(), anyList(),
        eq(RankedMode.RANKED_SOLO_5x5));
    verify(riotAccountRepository, never()).enrichWithRankedStats(any(), eq(RANKED_SOLO_5x5));
  }

  @Test
  void whenCacheEmptyRetrievesFromRiot() {
    String rankingId = "ranking";
    Guild guild = mock(Guild.class);
    Account account = Account.builder().build();
    when(guild.getId()).thenReturn("guildId");
    Ranking ranking = new Ranking(rankingId);
    ranking.addAccount(account);
    when(rankingRepository.read(rankingId, guild)).thenReturn(ranking);
    when(riotAccountRepository.enrichAccountsWithRankedStats(List.of(account),
        RANKED_SOLO_5x5)).thenReturn(List.of(account));
    when(accountsCache.find(guild.getId(), rankingId, RANKED_SOLO_5x5)).thenReturn(
        Optional.empty());
    assertEquals(cut.soloQ(rankingId, guild).getAccounts().get(0), account);
    verify(accountsCache, times(1)).save(anyString(), anyString(), anyList(),
        eq(RankedMode.RANKED_SOLO_5x5));
    verify(riotAccountRepository, times(1)).enrichAccountsWithRankedStats(anyList(),
        eq(RANKED_SOLO_5x5));
  }

}
