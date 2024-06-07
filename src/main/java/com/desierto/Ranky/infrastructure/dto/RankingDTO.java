package com.desierto.Ranky.infrastructure.dto;

import com.desierto.Ranky.domain.entity.Ranking;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RankingDTO {

  String id;
  List<AccountDTO> accounts;

  public static RankingDTO fromDomain(Ranking ranking) {
    return new RankingDTO(ranking.getId(),
        ranking.getAccounts().stream().map(AccountDTO::fromDomain).collect(
            Collectors.toList()));
  }

  public Ranking toDomain() {
    return new Ranking(id, accounts.stream().map(AccountDTO::toDomain).collect(
        Collectors.toList()));
  }
}
