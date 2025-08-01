package com.desierto.ranky.infrastructure.controller;

import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;

import com.desierto.ranky.domain.entity.Ranking;
import com.desierto.ranky.domain.service.IGetRankingService;
import com.desierto.ranky.infrastructure.configuration.ConfigLoader;
import com.desierto.ranky.infrastructure.controller.dto.RankingApi;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rankings")
@AllArgsConstructor
@Slf4j
public class RankingsController {

  @Autowired
  private JDA jda;

  @Autowired
  private ConfigLoader config;

  @Autowired
  private IGetRankingService getRankingService;

  @GetMapping("/{adminKey}/mutualWith/{userId}")
  public ResponseEntity<String> getMutualGuilds(@PathVariable String adminKey,
      @PathVariable String userId) {
    log.info("Entered getMutualGuilds");
    if (adminKey.equals(config.getControllerAdminKey())) {
      log.info("Entered getMutualGuilds adminKey check");
      loadGuilds(jda, Long.parseLong(userId));
      List<Guild> guilds = jda.getMutualGuilds(jda.retrieveUserById(userId).complete());
      return ok(guilds.toString());
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @GetMapping("/{adminKey}/fromGuild/{guildId}/forUser/{userId}")
  public ResponseEntity<List<RankingApi>> getRankings(@PathVariable String adminKey,
      @PathVariable String guildId,
      @PathVariable String userId) {
    log.info("Entered getRankings");
    if (adminKey.equals(config.getControllerAdminKey())) {
      log.info("Entered getRankings adminKey check");
      loadGuilds(jda, Long.parseLong(userId));
      List<Guild> guilds = jda.getMutualGuilds(jda.retrieveUserById(userId).complete());
      Optional<Guild> match = guilds.stream()
          .filter(guild -> guild.getId().equalsIgnoreCase(guildId)).findFirst();
      return match.map(guild -> ok(
              getRankingService.getAll(guild).stream().map(RankingApi::fromDomain)
                  .collect(
                      Collectors.toList())))
          .orElseGet(() -> ResponseEntity.notFound().build());
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @GetMapping("/{adminKey}/fromGuild/{guildId}/forUser/{userId}/ranking/{ranking}")
  public ResponseEntity<Ranking> getRanking(@PathVariable String adminKey,
      @PathVariable String guildId,
      @PathVariable String userId, @PathVariable String ranking) {
    log.info("Entered getRanking");
    if (adminKey.equals(config.getControllerAdminKey())) {
      log.info("Entered getRankings adminKey check");
      loadGuilds(jda, Long.parseLong(userId));
      List<Guild> guilds = jda.getMutualGuilds(jda.retrieveUserById(userId).complete());
      Optional<Guild> match = guilds.stream()
          .filter(guild -> guild.getId().equalsIgnoreCase(guildId)).findFirst();
      return match.map(guild -> ResponseEntity.ok(getRankingService.get(ranking, guild)))
          .orElseGet(() -> notFound().build());
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  private void loadGuilds(JDA bot, long id) {
    bot.getGuilds().forEach(guild -> guild.retrieveMemberById(id).complete());
  }
}
