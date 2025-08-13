package com.desierto.ranky.domain.service;

import java.util.List;
import net.dv8tion.jda.api.entities.Guild;

public interface IGuildsService {

  Guild get(String guildId, String userId);

  List<Guild> getAll(String userId);

  Guild getGuild(String guildId);
}
