package com.desierto.ranky.application.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.desierto.ranky.application.fixtures.RankingFixtures;
import com.desierto.ranky.domain.entity.Ranking;
import com.desierto.ranky.domain.repository.RankingRepository;
import net.dv8tion.jda.api.entities.Guild;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RankingPublisherServiceTest {

  @InjectMocks
  RankingPublisherService cut;

  @Mock
  RankingRepository rankingRepository;

  @Test
  void makesRankingPublic() {
    Ranking ranking = RankingFixtures.aRanking();
    Guild guild = mock(Guild.class);
    when(rankingRepository.read(ranking.getId(), guild)).thenReturn(ranking);

    assertFalse(ranking.getIsPublic());
    cut.publish(ranking.getId(), guild);
    assertTrue(ranking.getIsPublic());
    verify(rankingRepository, times(1)).update(ranking, guild);
  }
}