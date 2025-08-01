package com.desierto.ranky.infrastructure.dto;

import com.desierto.ranky.domain.entity.Ranking;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RankingDto {

  String id;
  List<AccountDto> accounts;

  public static RankingDto fromDomain(Ranking ranking) {
    return new RankingDto(ranking.getId(),
        ranking.getAccounts().stream().map(AccountDto::fromDomain).collect(
            Collectors.toList()));
  }

  public Ranking toDomain() {
    return new Ranking(id, accounts.stream().map(AccountDto::toDomain).collect(
        Collectors.toList()));
  }

  public void addAccounts(List<AccountDto> accounts) {
    this.accounts.addAll(accounts);
  }
}
