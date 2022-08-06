package com.desierto.Ranky.application.service;

import com.desierto.Ranky.domain.entity.Ranking;
import com.desierto.Ranky.domain.exception.ranking.RankingNotFoundException;
import com.desierto.Ranky.domain.repository.RankingRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class GetRankingService {

  private final RankingRepository rankingRepository;

  public Ranking execute(long rankingId) {
    return rankingRepository.findById(rankingId)
        .orElseThrow(() -> new RankingNotFoundException(rankingId));
  }
}
