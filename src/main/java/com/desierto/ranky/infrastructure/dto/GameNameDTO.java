package com.desierto.ranky.infrastructure.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameNameDTO {

  String gameName;

  String tagLine;

  public String getWholeName() {
    return gameName + "#" + tagLine;
  }
}
