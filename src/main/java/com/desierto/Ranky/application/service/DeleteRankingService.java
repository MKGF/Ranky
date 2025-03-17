package com.desierto.Ranky.application.service;

import com.desierto.Ranky.application.AccountsCache;
import com.desierto.Ranky.domain.repository.RankingRepository;
import com.desierto.Ranky.domain.service.IDeleteRankingService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class DeleteRankingService implements IDeleteRankingService {

  @Autowired
  private RankingRepository rankingRepository;

  @Autowired
  private AccountsCache accountsCache;

  @Override
  public boolean execute(String id, Guild guild) {
    accountsCache.delete(id, guild.getId());
    return rankingRepository.delete(id, guild);
  }
}
