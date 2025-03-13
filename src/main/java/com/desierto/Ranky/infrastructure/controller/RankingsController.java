package com.desierto.Ranky.infrastructure.controller;

import static org.springframework.http.ResponseEntity.ok;

import com.desierto.Ranky.domain.entity.Ranking;
import com.desierto.Ranky.infrastructure.service.GetRankingsService;
import java.util.List;
import java.util.Optional;
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
  private GetRankingsService getRankingsService;

  @GetMapping("/mutualWith/{userId}")
  public ResponseEntity<String> getMutualGuilds(@PathVariable String userId) {
    loadGuilds(jda, Long.parseLong(userId));
    List<Guild> guilds = jda.getMutualGuilds(jda.retrieveUserById(userId).complete());
    return ok(guilds.toString());
  }

  @GetMapping("/fromGuild/{guildId}/forUser/{userId}")
  public ResponseEntity<List<Ranking>> getRankings(@PathVariable String guildId,
      @PathVariable String userId) {
    loadGuilds(jda, Long.parseLong(userId));
    List<Guild> guilds = jda.getMutualGuilds(jda.retrieveUserById(userId).complete());
    Optional<Guild> match = guilds.stream()
        .filter(guild -> guild.getId().equalsIgnoreCase(guildId)).findFirst();
    return match.map(guild -> ok(getRankingsService.execute(guild)))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  private void loadGuilds(JDA bot, long id) {
    bot.getGuilds().forEach(guild -> guild.retrieveMemberById(id).complete());
  }
}
