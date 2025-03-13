package com.desierto.Ranky.infrastructure.service;

import com.desierto.Ranky.domain.entity.Ranking;
import com.desierto.Ranky.domain.exception.ConfigChannelNotFoundException;
import com.desierto.Ranky.domain.exception.ranking.RankingNotFoundException;
import com.desierto.Ranky.infrastructure.configuration.ConfigLoader;
import com.desierto.Ranky.infrastructure.repository.ConfigChannelRankingRepository;
import com.google.gson.Gson;
import java.util.List;
import java.util.logging.Logger;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GetRankingsService {

  @Autowired
  private ConfigLoader config;

  @Autowired
  private Gson gson;

  public static final Logger log = Logger.getLogger("GetRankingsService.class");

  public List<Ranking> execute(Guild guild)
      throws ConfigChannelNotFoundException, RankingNotFoundException {
    ConfigChannelRankingRepository rankingRepository = new ConfigChannelRankingRepository(
        config,
        guild,
        gson
    );
    return rankingRepository.findAll();
  }

}
