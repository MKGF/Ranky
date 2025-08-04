package com.desierto.ranky.application.service;

import static com.desierto.ranky.domain.valueobject.RankedMode.RANKED_SOLO_5x5;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.desierto.ranky.application.AccountsCache;
import com.desierto.ranky.application.fixtures.AccountFixtures;
import com.desierto.ranky.application.fixtures.RankingFixtures;
import com.desierto.ranky.domain.repository.RankingRepository;
import com.desierto.ranky.domain.repository.RiotAccountRepository;
import java.util.List;
import java.util.Optional;
import net.dv8tion.jda.api.entities.Guild;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RankingsServiceTest {

  @Mock
  private RankingRepository rankingRepository;

  @Mock
  private RiotAccountRepository riotAccountRepository;

  @Mock
  private AccountsCache accountsCache;

  @InjectMocks
  private RankingsService cut;

  @Test
  void getAllGuilds_callsRepository() {
    cut.getAll(mock(Guild.class));
    verify(rankingRepository, times(1)).findAll(any());
  }

  @Test
  void getGuild_callsRepo_and_retrievesAccountInfoFromRiot() {
    String rankingId = "rankingId";
    Guild guild = mock(Guild.class);
    when(guild.getId()).thenReturn("guildId");
    when(rankingRepository.read(rankingId, guild)).thenReturn(RankingFixtures.aRanking());
    when(accountsCache.find(guild.getId(), rankingId)).thenReturn(Optional.empty());
    when(riotAccountRepository.enrichWithRankedStats(any(), eq(RANKED_SOLO_5x5))).thenReturn(
        AccountFixtures.anAccount());

    assertEquals(cut.get(rankingId, guild), RankingFixtures.aRanking());

    verify(accountsCache, times(1)).save(guild.getId(), rankingId,
        List.of(AccountFixtures.anAccount()));
  }

  @Test
  void getGuild_callsRepo_and_interactsWithCache() {
    String rankingId = "rankingId";
    Guild guild = mock(Guild.class);
    when(guild.getId()).thenReturn("guildId");
    when(rankingRepository.read(rankingId, guild)).thenReturn(RankingFixtures.aRanking());
    when(accountsCache.find(guild.getId(), rankingId)).thenReturn(
        Optional.of(List.of(AccountFixtures.anAccount())));

    assertEquals(cut.get(rankingId, guild), RankingFixtures.aRanking());

    verify(accountsCache, never()).save(guild.getId(), rankingId,
        List.of(AccountFixtures.anAccount()));
  }
}