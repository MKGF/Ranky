package com.desierto.ranky.infrastructure.controller;

import static org.springframework.http.ResponseEntity.ok;

import com.desierto.ranky.domain.entity.Ranking;
import com.desierto.ranky.domain.exception.ranking.RankingNotFoundException;
import com.desierto.ranky.domain.service.IAccountsService;
import com.desierto.ranky.domain.service.ICreateRankingService;
import com.desierto.ranky.domain.service.IDeleteRankingService;
import com.desierto.ranky.domain.service.IGuildsService;
import com.desierto.ranky.domain.service.IRankingsService;
import com.desierto.ranky.infrastructure.controller.dto.AccountApi;
import com.desierto.ranky.infrastructure.controller.dto.RankingApi;
import com.desierto.ranky.infrastructure.dto.GuildDto;
import com.desierto.ranky.infrastructure.exceptions.UnauthorizedException;
import com.desierto.ranky.infrastructure.mappers.RankingsMapper;
import com.desierto.ranky.infrastructure.service.auth.UserPowerChecker;
import com.desierto.ranky.infrastructure.service.auth.UserSession;
import com.desierto.ranky.infrastructure.web.annotation.CurrentUser;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rankings")
@Slf4j
public class RankingsController {

  private IRankingsService rankingsService;

  private IGuildsService guildsService;

  private ICreateRankingService createRankingService;

  private IAccountsService accountsService;

  private IDeleteRankingService deleteRankingService;

  private UserPowerChecker userPowerChecker;

  private RankingsMapper mapper;

  @Autowired
  public RankingsController(IRankingsService rankingsService,
      IGuildsService guildsService, ICreateRankingService createRankingService,
      IAccountsService accountsService, IDeleteRankingService deleteRankingService,
      UserPowerChecker userPowerChecker, RankingsMapper mapper) {
    this.rankingsService = rankingsService;
    this.guildsService = guildsService;
    this.createRankingService = createRankingService;
    this.accountsService = accountsService;
    this.deleteRankingService = deleteRankingService;
    this.userPowerChecker = userPowerChecker;
    this.mapper = mapper;
  }

  @GetMapping("/mutual")
  public ResponseEntity<List<GuildDto>> getMutualGuilds(@CurrentUser UserSession session) {
    log.info("Entered getMutualGuilds");
    log.info("Session: {}", session.toString());
    return mapper.mapGuilds(guildsService.getAll(session.userId()));
  }

  @GetMapping("/fromGuild/{guildId}")
  public ResponseEntity<List<RankingApi>> getRankings(@CurrentUser UserSession session,
      @PathVariable String guildId) {
    log.info("Entered getRankings");
    log.info("Session: {}", session.toString());
    return mapper.mapRankings(rankingsService.getAll(
        guildsService.get(guildId, session.userId())));

  }

  @GetMapping("/fromGuild/{guildId}/ranking/{ranking}")
  public ResponseEntity<Ranking> getRanking(@CurrentUser UserSession session,
      @PathVariable String guildId, @PathVariable("ranking") String rankingId) {
    log.info("Entered getRanking");
    log.info("Session: {}", session);
    if (session != null) {
      return mapper.mapSingle(
          rankingsService.get(rankingId, guildsService.get(guildId, session.userId())));
    } else {
      try {
        Guild guild = guildsService.getGuild(guildId);
        Ranking ranking = rankingsService.get(rankingId, guild);
        if (ranking.shouldBeVisible()) {
          return mapper.mapSingle(ranking);
        } else {
          throw new UnauthorizedException();
        }
      } catch (NumberFormatException ignored) {
        throw new RankingNotFoundException(rankingId);
      }
    }
  }

  @PostMapping("/forGuild/{guildId}/name/{name}")
  public ResponseEntity<Ranking> createRanking(@CurrentUser UserSession session,
      @PathVariable String guildId, @PathVariable String name) {
    log.info("Entered createRanking");
    log.info("Session: {}", session.toString());
    Guild guild = guildsService.get(guildId, session.userId());
    userPowerChecker.check(session, guild);
    return mapper.mapSingle(createRankingService.execute(name, guild));
  }

  @DeleteMapping("/forGuild/{guildId}/name/{name}")
  public ResponseEntity<Boolean> deleteRanking(@CurrentUser UserSession session,
      @PathVariable String guildId, @PathVariable String name) {
    log.info("Entered createRanking");
    log.info("Session: {}", session.toString());
    Guild guild = guildsService.get(guildId, session.userId());
    userPowerChecker.check(session, guild);
    return ok(deleteRankingService.execute(name, guild));
  }

  @PostMapping("/forGuild/{guildId}/forRanking/{ranking}/add")
  public ResponseEntity<Ranking> addAccounts(@CurrentUser UserSession session,
      @PathVariable String guildId, @PathVariable String rankingId, List<AccountApi> accounts) {
    log.info("Entered addAccounts");
    log.info("Session: {}", session.toString());
    Guild guild = guildsService.get(guildId, session.userId());
    userPowerChecker.check(session, guild);
    return mapper.mapSingle(accountsService.addAccounts(rankingId, guild,
        accounts.stream().map(AccountApi::toDomain).toList()));
  }

  @PostMapping("/forGuild/{guildId}/forRanking/{ranking}/remove")
  public ResponseEntity<Ranking> removeAccounts(@CurrentUser UserSession session,
      @PathVariable String guildId, @PathVariable String rankingId, List<AccountApi> accounts) {
    log.info("Entered removeAccounts");
    log.info("Session: {}", session.toString());
    Guild guild = guildsService.get(guildId, session.userId());
    userPowerChecker.check(session, guild);
    return mapper.mapSingle(accountsService.removeAccounts(rankingId, guild,
        accounts.stream().map(AccountApi::toDomain).toList()));
  }
}
