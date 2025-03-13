package com.desierto.Ranky.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LastCommandDTO {

  private String userId;

  private String lastCommand;

}
