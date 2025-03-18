package com.desierto.Ranky.application.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.desierto.Ranky.application.AccountsCache;
import com.desierto.Ranky.domain.repository.RankingRepository;
import net.dv8tion.jda.api.entities.Guild;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class DeleteRankingServiceTest {

  @Mock
  RankingRepository rankingRepository;

  @Mock
  AccountsCache accountsCache;

  DeleteRankingService cut;

  @BeforeEach
  public void setUp() {
    cut = new DeleteRankingService(rankingRepository, accountsCache);
  }

  @Test
  public void deletesFromRepoAndCache() {
    Guild guild = mock(Guild.class);
    when(guild.getId()).thenReturn("guildId");
    cut.execute("id", guild);
    verify(accountsCache, times(1)).delete("id", "guildId");
    verify(rankingRepository, times(1)).delete("id", guild);
  }

}