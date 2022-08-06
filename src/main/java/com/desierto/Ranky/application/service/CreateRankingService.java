package com.desierto.Ranky.application.service;

import com.desierto.Ranky.domain.entity.Ranking;
import com.desierto.Ranky.domain.repository.RankingRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class CreateRankingService {

  private final RankingRepository rankingRepository;

  public Ranking execute() {
    return rankingRepository.save(new Ranking());
  }
}
