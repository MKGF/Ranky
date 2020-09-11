package com.desierto.LoLRankingMaker.infrastructure.repository.dto;

import lombok.AllArgsConstructor;
import lombok.Value;

@AllArgsConstructor
@Value
public class SummonerDTO {

  String id;
  String accountId;
  String puuid;
  String name;
  Integer profileIconId;
  Long revisionDate;
  Integer summonerLevel;
}
