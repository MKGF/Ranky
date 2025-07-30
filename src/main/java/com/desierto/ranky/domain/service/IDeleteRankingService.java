package com.desierto.ranky.domain.service;

import net.dv8tion.jda.api.entities.Guild;

public interface IDeleteRankingService {

  boolean execute(String id, Guild guild);
}
