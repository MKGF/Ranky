package com.desierto.ranky.application.service;

import com.desierto.ranky.domain.exception.guild.GuildNotFoundException;
import com.desierto.ranky.domain.service.IGuildsService;
import java.util.List;
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
  public Guild get(String guildId, String userId) {
    loadGuilds(jda, userId);
    List<Guild> guilds = jda.getMutualGuilds(jda.retrieveUserById(userId).complete());
    return guilds.stream()
        .filter(guild -> guild.getId().equalsIgnoreCase(guildId)).findFirst()
        .orElseThrow(() -> new GuildNotFoundException(guildId));
  }

  @Override
  public List<Guild> getAll(String userId) {
    loadGuilds(jda, userId);
    List<Guild> commonGuilds = jda.getMutualGuilds(jda.retrieveUserById(userId).complete());
    if (commonGuilds.isEmpty()) {
      throw new GuildNotFoundException();
    }
    return commonGuilds;
  }

  @Override
  public Guild getGuild(String guildId) {
    Guild guild = jda.getGuildById(guildId);
    if (guild == null) {
      throw new GuildNotFoundException(guildId);
    }
    return guild;
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
