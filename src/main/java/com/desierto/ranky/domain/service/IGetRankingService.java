package com.desierto.ranky.domain.service;

import com.desierto.ranky.domain.entity.Ranking;
import java.util.List;
import net.dv8tion.jda.api.entities.Guild;

public interface IGetRankingService {

  List<Ranking> getAll(Guild guild);

  Ranking get(String rankingId, Guild guild);
}
