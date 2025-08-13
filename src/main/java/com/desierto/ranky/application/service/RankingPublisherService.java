package com.desierto.ranky.application.service;

import com.desierto.ranky.domain.entity.Ranking;
import com.desierto.ranky.domain.repository.RankingRepository;
import com.desierto.ranky.domain.service.IRankingPublisherService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RankingPublisherService implements IRankingPublisherService {

  private RankingRepository rankingRepository;

  @Autowired
  public RankingPublisherService(RankingRepository rankingRepository) {
    this.rankingRepository = rankingRepository;
  }

  @Override
  public void publish(String rankingId, Guild guild) {
    Ranking published = rankingRepository.read(rankingId, guild);
    published.makePublic();
    rankingRepository.update(published, guild);
  }
}
