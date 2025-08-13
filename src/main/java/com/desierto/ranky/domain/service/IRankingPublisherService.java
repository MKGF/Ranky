package com.desierto.ranky.domain.service;

import net.dv8tion.jda.api.entities.Guild;

public interface IRankingPublisherService {

  void publish(String rankingId, Guild guild);
}
