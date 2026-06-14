package com.desierto.ranky.application.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.desierto.ranky.application.AccountsCache;
import com.desierto.ranky.domain.repository.RankingRepository;
import net.dv8tion.jda.api.entities.Guild;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeleteRankingServiceTest {

  @Mock
  RankingRepository rankingRepository;

  @Mock
  AccountsCache accountsCache;

  @InjectMocks
  DeleteRankingService cut;

  @Test
  void deletesFromRepoAndCache() {
    Guild guild = mock(Guild.class);
    when(guild.getId()).thenReturn("guildId");
    cut.execute("id", guild);
    verify(accountsCache, times(1)).delete("id", "guildId");
    verify(rankingRepository, times(1)).delete("id", guild);
  }

}