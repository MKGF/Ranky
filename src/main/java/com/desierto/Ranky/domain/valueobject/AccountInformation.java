package com.desierto.Ranky.domain.valueobject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class AccountInformation {

  Rank rank;
  Winrate winrate;
  int leaguePoints;
  String queueType;

}
