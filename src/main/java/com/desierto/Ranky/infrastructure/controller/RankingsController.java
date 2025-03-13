package com.desierto.Ranky.infrastructure.controller;

import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;

import com.desierto.Ranky.domain.RankingService;
import com.desierto.Ranky.domain.entity.Ranking;
import com.desierto.Ranky.infrastructure.controller.dto.RankingApi;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
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
public class RankingsController {

  @Autowired
  private JDA jda;

  @Autowired
  private RankingService rankingService;

  @GetMapping("/mutualWith/{userId}")
  public ResponseEntity<String> getMutualGuilds(@PathVariable String userId) {
    loadGuilds(jda, Long.parseLong(userId));
    List<Guild> guilds = jda.getMutualGuilds(jda.retrieveUserById(userId).complete());
    return ok(guilds.toString());
  }

  @GetMapping("/fromGuild/{guildId}/forUser/{userId}")
  public ResponseEntity<List<RankingApi>> getRankings(@PathVariable String guildId,
      @PathVariable String userId) {
    loadGuilds(jda, Long.parseLong(userId));
    List<Guild> guilds = jda.getMutualGuilds(jda.retrieveUserById(userId).complete());
    Optional<Guild> match = guilds.stream()
        .filter(guild -> guild.getId().equalsIgnoreCase(guildId)).findFirst();
    return match.map(guild -> ok(
            rankingService.getAll(guild).stream().map(RankingApi::fromDomain)
                .collect(
                    Collectors.toList())))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @GetMapping("/fromGuild/{guildId}/forUser/{userId}/ranking/{ranking}")
  public ResponseEntity<Ranking> getRanking(@PathVariable String guildId,
      @PathVariable String userId, @PathVariable String ranking) {
    loadGuilds(jda, Long.parseLong(userId));
    List<Guild> guilds = jda.getMutualGuilds(jda.retrieveUserById(userId).complete());
    Optional<Guild> match = guilds.stream()
        .filter(guild -> guild.getId().equalsIgnoreCase(guildId)).findFirst();
    return match.map(guild -> ResponseEntity.ok(rankingService.get(ranking, guild)))
        .orElseGet(() -> notFound().build());
  }

  private void loadGuilds(JDA bot, long id) {
    bot.getGuilds().forEach(guild -> guild.retrieveMemberById(id).complete());
  }
}
