package com.desierto.ranky.application.service;

import com.desierto.ranky.domain.entity.Ranking;
import com.desierto.ranky.domain.repository.RankingRepository;
import com.desierto.ranky.domain.service.ICreateRankingService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class CreateRankingService implements ICreateRankingService {

  @Autowired
  private RankingRepository rankingRepository;

  @Override
  public Ranking execute(String id, Guild guild) {
    return rankingRepository.create(new Ranking(id), guild);
  }
}
