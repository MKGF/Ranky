package com.desierto.Ranky.application.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.desierto.Ranky.domain.entity.Ranking;
import com.desierto.Ranky.domain.repository.RankingRepository;
import net.dv8tion.jda.api.entities.Guild;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class CreateRankingServiceTest {

  @Mock
  RankingRepository rankingRepository;

  CreateRankingService cut;

  @BeforeEach
  public void setUp() {
    cut = new CreateRankingService(rankingRepository);
  }

  @Test
  public void creates() {
    String rankingId = "ranking";
    Guild guild = mock(Guild.class);
    cut.execute(rankingId, guild);
    verify(rankingRepository, times(1)).create(new Ranking(rankingId), guild);
  }
}
