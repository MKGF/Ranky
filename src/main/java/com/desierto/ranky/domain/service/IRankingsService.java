package com.desierto.ranky.domain.service;

import com.desierto.ranky.domain.entity.Ranking;
import java.util.List;
import net.dv8tion.jda.api.entities.Guild;

public interface IRankingsService {

  List<Ranking> getAll(Guild guild);

  Ranking soloQ(String rankingId, Guild guild);

  Ranking flexQ(String rankingId, Guild guild);
}
