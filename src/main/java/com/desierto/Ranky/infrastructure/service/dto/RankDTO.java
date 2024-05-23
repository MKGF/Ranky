package com.desierto.Ranky.infrastructure.service.dto;

import com.desierto.Ranky.domain.valueobject.Rank;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class RankDTO {

  private String tier; //Tier
  private int division; //Division

  public static RankDTO fromDomain(Rank rank) {
    return new RankDTO(rank.getTier().name(), rank.getDivision());
  }
}
