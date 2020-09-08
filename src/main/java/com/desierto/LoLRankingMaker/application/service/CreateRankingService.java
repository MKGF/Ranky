package com.desierto.LoLRankingMaker.application.service;

import com.desierto.LoLRankingMaker.domain.entity.Ranking;
import com.desierto.LoLRankingMaker.domain.repository.RankingRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class CreateRankingService {

  private final RankingRepository rankingRepository;

  public Ranking execute() {
    Ranking ranking = new Ranking();
    return rankingRepository.save(ranking);
  }
}
