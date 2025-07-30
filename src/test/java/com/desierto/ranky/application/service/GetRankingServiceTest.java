package com.desierto.ranky.application.service;

import static com.desierto.ranky.domain.valueobject.RankedMode.RANKED_SOLO_5x5;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
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
import java.util.List;
import java.util.Optional;
import net.dv8tion.jda.api.entities.Guild;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class GetRankingServiceTest {

  @Mock
  RankingRepository rankingRepository;

  @Mock
  RiotAccountRepository riotAccountRepository;

  @Mock
  AccountsCache accountsCache;

  GetRankingService cut;

  @BeforeEach
  public void setUp() {
    cut = new GetRankingService(rankingRepository, riotAccountRepository, accountsCache);
  }

  @Test
  public void retrievesFromCache() {
    String rankingId = "ranking";
    Guild guild = mock(Guild.class);
    Account account = Account.builder().build();
    when(guild.getId()).thenReturn("guildId");
    Ranking ranking = new Ranking(rankingId);
    when(rankingRepository.read(rankingId, guild)).thenReturn(ranking);
    when(accountsCache.find(guild.getId(), rankingId)).thenReturn(
        Optional.of(List.of(account)));
    assertEquals(cut.get(rankingId, guild).getAccounts().get(0), account);
    verify(accountsCache, never()).save(anyString(), anyString(), anyList());
    verify(riotAccountRepository, never()).enrichWithRankedStats(any(), RANKED_SOLO_5x5);
  }

  @Test
  public void whenCacheEmptyRetrievesFromRiot() {
    String rankingId = "ranking";
    Guild guild = mock(Guild.class);
    Account account = Account.builder().build();
    when(guild.getId()).thenReturn("guildId");
    Ranking ranking = new Ranking(rankingId);
    ranking.addAccount(account);
    when(rankingRepository.read(rankingId, guild)).thenReturn(ranking);
    when(riotAccountRepository.enrichWithRankedStats(account, RANKED_SOLO_5x5)).thenReturn(account);
    when(accountsCache.find(guild.getId(), rankingId)).thenReturn(
        Optional.empty());
    assertEquals(cut.get(rankingId, guild).getAccounts().get(0), account);
    verify(accountsCache, times(1)).save(anyString(), anyString(), anyList());
    verify(riotAccountRepository, times(1)).enrichWithRankedStats(any(), RANKED_SOLO_5x5);
  }

}
