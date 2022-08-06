package com.desierto.Ranky.infrastructure.repository;

import com.desierto.Ranky.domain.entity.Ranking;
import com.desierto.Ranky.domain.repository.RankingRepository;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface CrudRankingRepository extends CrudRepository<Ranking, Long>, RankingRepository {

  Optional<Ranking> findById(Long rankingId);

  Ranking save(Ranking ranking);
}
