package com.desierto.Ranky.domain.service;

import com.desierto.Ranky.domain.entity.Ranking;
import net.dv8tion.jda.api.entities.Guild;

public interface ICreateRankingService {

  public Ranking execute(String name, Guild guild);
}
