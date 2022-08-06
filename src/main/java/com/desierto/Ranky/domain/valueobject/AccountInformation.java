package com.desierto.Ranky.domain.valueobject;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.validation.constraints.Max;
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
@Embeddable
@ToString
public class AccountInformation {

  @Embedded
  Rank rank;
  @Embedded
  Winrate winrate;
  @Max(value = 100)
  int leaguePoints;
  String queueType;

}
