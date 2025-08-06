package com.desierto.ranky.infrastructure.mappers;

import static org.springframework.http.ResponseEntity.ok;

import com.desierto.ranky.domain.entity.Ranking;
import com.desierto.ranky.infrastructure.controller.dto.RankingApi;
import com.desierto.ranky.infrastructure.dto.GuildDto;
import java.util.List;
import net.dv8tion.jda.api.entities.Guild;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class RankingsMapper {


  public ResponseEntity<List<GuildDto>> mapGuilds(List<Guild> guilds) {
    return ok(guilds.stream().map(GuildDto::fromDomain).toList());
  }

  public ResponseEntity<List<RankingApi>> mapRankings(List<Ranking> rankings) {
    return
        ok(rankings.stream().map(RankingApi::fromDomain).toList());
  }

  public ResponseEntity<Ranking> mapSingle(Ranking ranking) {
    return ok(ranking);
  }
}
