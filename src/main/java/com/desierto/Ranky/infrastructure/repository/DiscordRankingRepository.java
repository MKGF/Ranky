package com.desierto.Ranky.infrastructure.repository;

import com.desierto.Ranky.domain.entity.Ranking;
import com.desierto.Ranky.domain.repository.RankingRepository;
import com.desierto.Ranky.infrastructure.configuration.ConfigLoader;
import com.google.gson.Gson;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
@NoArgsConstructor
public class DiscordRankingRepository implements RankingRepository {

  @Autowired
  private ConfigLoader config;

  @Autowired
  private Gson gson;

  @Override
  public Ranking update(Ranking ranking, Guild guild) {
    ConfigChannelRankingRepository rankingRepository = new ConfigChannelRankingRepository(
        config,
        guild,
        gson
    );
    return rankingRepository.update(ranking);
  }

  @Override
  public Ranking create(Ranking ranking, Guild guild) {
    ConfigChannelRankingRepository rankingRepository = new ConfigChannelRankingRepository(
        config,
        guild,
        gson
    );
    return rankingRepository.create(ranking);
  }

  @Override
  public boolean delete(String rankingId, Guild guild) {
    ConfigChannelRankingRepository rankingRepository = new ConfigChannelRankingRepository(
        config,
        guild,
        gson
    );
    return rankingRepository.delete(rankingId);
  }

  @Override
  public Ranking read(String rankingId, Guild guild) {
    ConfigChannelRankingRepository rankingRepository = new ConfigChannelRankingRepository(
        config,
        guild,
        gson
    );
    return rankingRepository.read(rankingId);
  }

  @Override
  public List<Ranking> findAll(Guild guild) {
    ConfigChannelRankingRepository rankingRepository = new ConfigChannelRankingRepository(
        config,
        guild,
        gson
    );
    return rankingRepository.findAll();
  }
}
