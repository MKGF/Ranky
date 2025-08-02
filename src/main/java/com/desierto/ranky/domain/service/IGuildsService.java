package com.desierto.ranky.domain.service;

import java.util.List;
import java.util.Optional;
import net.dv8tion.jda.api.entities.Guild;

public interface IGuildsService {

  Optional<Guild> get(String guildId, String userId);

  List<Guild> getAll(String userId);
}
