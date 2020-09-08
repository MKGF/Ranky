package com.desierto.LoLRankingMaker.infrastructure.controller;

import com.desierto.LoLRankingMaker.application.service.CreateRankingService;
import com.desierto.LoLRankingMaker.application.service.GetRankingService;
import com.desierto.LoLRankingMaker.domain.entity.Ranking;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ranking")
@AllArgsConstructor
public class RankingController {

  private final GetRankingService getRankingService;
  private final CreateRankingService createRankingService;

  @GetMapping("/{rankingId}")
  public Ranking getRanking(@PathVariable long rankingId) {
    return getRankingService.execute(rankingId);
  }

  @PostMapping
  public Ranking createRanking() {
    return createRankingService.execute();
  }
}
