package com.desierto.LoLRankingMaker.domain.repository;

import com.desierto.LoLRankingMaker.domain.entity.Ranking;
import java.util.Optional;

public interface RankingRepository {

  Optional<Ranking> findById(long rankingId);

  Ranking save(Ranking ranking);
}
