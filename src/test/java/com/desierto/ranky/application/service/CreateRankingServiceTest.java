package com.desierto.ranky.application.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.desierto.ranky.domain.entity.Ranking;
import com.desierto.ranky.domain.repository.RankingRepository;
import net.dv8tion.jda.api.entities.Guild;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateRankingServiceTest {

  @Mock
  RankingRepository rankingRepository;

  @InjectMocks
  CreateRankingService cut;

  @Test
  void creates() {
    String rankingId = "ranking";
    Guild guild = mock(Guild.class);
    cut.execute(rankingId, guild);
    verify(rankingRepository, times(1)).create(new Ranking(rankingId), guild);
  }
}
