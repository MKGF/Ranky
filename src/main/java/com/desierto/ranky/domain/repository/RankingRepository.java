package com.desierto.ranky.domain.repository;

import com.desierto.ranky.domain.entity.Ranking;
import java.util.List;
import net.dv8tion.jda.api.entities.Guild;

public interface RankingRepository {

  Ranking update(Ranking ranking, Guild guild);

  Ranking create(Ranking ranking, Guild guild);

  boolean delete(String rankingId, Guild guild);

  Ranking read(String rankingId, Guild guild);

  List<Ranking> findAll(Guild guild);
}
