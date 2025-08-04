package com.desierto.ranky.application.service;

import com.desierto.ranky.domain.service.IGuildsService;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GuildsService implements IGuildsService {

  private final JDA jda;

  @Autowired
  public GuildsService(JDA jda) {
    this.jda = jda;
  }


  @Override
  public Optional<Guild> get(String guildId, String userId) {
    loadGuilds(jda, userId);
    List<Guild> guilds = jda.getMutualGuilds(jda.retrieveUserById(userId).complete());
    return guilds.stream()
        .filter(guild -> guild.getId().equalsIgnoreCase(guildId)).findFirst();
  }

  @Override
  public List<Guild> getAll(String userId) {
    loadGuilds(jda, userId);
    return jda.getMutualGuilds(jda.retrieveUserById(userId).complete());
  }

  private void loadGuilds(JDA bot, String id) {
    bot.getGuilds().forEach(guild -> {
      try {
        guild.retrieveMemberById(id).complete();
      } catch (ErrorResponseException ignored) {
        log.debug("Not member of corresponding guild: " + guild.getName());
      }
    });
  }
}
