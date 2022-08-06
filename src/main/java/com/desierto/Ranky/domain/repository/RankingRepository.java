package com.desierto.Ranky.domain.repository;

import com.desierto.Ranky.domain.entity.Ranking;
import java.util.Optional;

public interface RankingRepository {

  Optional<Ranking> findById(long rankingId);

  Ranking save(Ranking ranking);
}
