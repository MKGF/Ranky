package com.desierto.ranky.infrastructure.controller;

import com.desierto.ranky.domain.entity.Ranking;
import com.desierto.ranky.domain.exception.NotFoundException;
import com.desierto.ranky.domain.exception.ranking.RankingNotFoundException;
import com.desierto.ranky.domain.service.IGuildsService;
import com.desierto.ranky.domain.service.IRankingsService;
import com.desierto.ranky.infrastructure.controller.dto.RankingApi;
import com.desierto.ranky.infrastructure.mappers.RankingsMapper;
import com.desierto.ranky.infrastructure.service.auth.UserSession;
import com.desierto.ranky.infrastructure.web.annotation.CurrentUser;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rankings")
@Slf4j
public class RankingsController {

  private IRankingsService rankingsService;

  private IGuildsService guildsService;

  private RankingsMapper mapper;

  @Autowired
  public RankingsController(IRankingsService rankingsService,
      IGuildsService guildsService, RankingsMapper mapper) {
    this.rankingsService = rankingsService;
    this.guildsService = guildsService;
    this.mapper = mapper;
  }

  @GetMapping("/mutual")
  public ResponseEntity<String> getMutualGuilds(@CurrentUser UserSession session) {
    log.info("Entered getMutualGuilds");
    log.info("Session: {}", session.toString());
    return mapper.mapGuilds(guildsService.getAll(session.userId()));
  }

  @GetMapping("/fromGuild/{guildId}")
  public ResponseEntity<List<RankingApi>> getRankings(@CurrentUser UserSession session,
      @PathVariable String guildId) {
    try {
      log.info("Entered getRankings");
      log.info("Session: {}", session.toString());
      return mapper.mapRankings(rankingsService.getAll(
          guildsService.get(guildId, session.userId()).orElseThrow(
              () -> new NotFoundException(String.format("Guild %s was not found.", guildId)))));
    } catch (NotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @GetMapping("/fromGuild/{guildId}/ranking/{ranking}")
  public ResponseEntity<Ranking> getRanking(@CurrentUser UserSession session,
      @PathVariable String guildId, @PathVariable String ranking) {
    log.info("Entered getRanking");
    log.info("Session: {}", session.toString());
    return mapper.mapSingle(
        rankingsService.get(ranking, guildsService.get(guildId, session.userId())
            .orElseThrow(() -> new RankingNotFoundException(ranking))));
  }
}
